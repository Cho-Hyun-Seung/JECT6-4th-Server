package com.ject6.boost.infrastructure.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewerCountService {

    private static final String PREFIX = "campaign:viewers:";
    private final RedisTemplate<String, String> redisTemplate;

    public void enter(Long campaignId) {
        redisTemplate.opsForValue().increment(PREFIX + campaignId);
    }

    public void leave(Long campaignId) {
        Long count = redisTemplate.opsForValue().decrement(PREFIX + campaignId);
        if (count != null && count < 0) {
            redisTemplate.opsForValue().set(PREFIX + campaignId, "0");
        }
    }

    public Long getCount(Long campaignId) {
        String val = redisTemplate.opsForValue().get(PREFIX + campaignId);
        return val == null ? 0L : Long.parseLong(val);
    }
}

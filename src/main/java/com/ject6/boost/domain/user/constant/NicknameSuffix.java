package com.ject6.boost.domain.user.constant;

import java.security.SecureRandom;

public enum NicknameSuffix {
    RUNNER("러너"),
    CREW("크루"),
    PRO("프로"),
    MAKER("메이커"),
    PARTNER("파트너"),
    CHALLENGER("챌린저"),
    MASTER("마스터"),
    PLAYER("플레이어"),
    BUILDER("빌더"),
    CONNECTOR("커넥터"),
    WORKER("워커"),
    GUIDE("가이드"),
    EXPLORER("탐험가"),
    PIONEER("개척자"),
    DREAMER("드리머"),
    SUPPORTER("서포터"),
    MANAGER("매니저"),
    LEADER("리더"),
    EXPERT("전문가"),
    PLANNER("플래너");

    private final String value;

    NicknameSuffix(String value) {
        this.value = value;
    }

    /**
     * 랜덤 닉네임 suffix 표시값을 반환하는 함수.
     */
    public static String randomValue(SecureRandom random) {
        NicknameSuffix[] values = values();
        return values[random.nextInt(values.length)].value;
    }
}

package com.ject6.boost.domain.onboarding.repository;

import com.ject6.boost.domain.onboarding.entity.OnboardingResponse;
import java.util.Optional;

public interface OnboardingResponseRepository {
    OnboardingResponse save(OnboardingResponse response);
    Optional<OnboardingResponse> findBySessionId(String sessionId);
}

package com.ject6.boost.infrastructure.onboarding.repository;

import com.ject6.boost.domain.onboarding.entity.OnboardingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OnboardingResponseJpaRepository extends JpaRepository<OnboardingResponse, Long> {
    Optional<OnboardingResponse> findBySessionId(String sessionId);
}

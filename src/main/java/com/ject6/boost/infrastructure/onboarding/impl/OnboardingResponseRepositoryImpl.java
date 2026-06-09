package com.ject6.boost.infrastructure.onboarding.impl;

import com.ject6.boost.domain.onboarding.entity.OnboardingResponse;
import com.ject6.boost.domain.onboarding.repository.OnboardingResponseRepository;
import com.ject6.boost.infrastructure.onboarding.repository.OnboardingResponseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OnboardingResponseRepositoryImpl implements OnboardingResponseRepository {
    private final OnboardingResponseJpaRepository jpa;
    @Override public OnboardingResponse save(OnboardingResponse r) { return jpa.save(r); }
    @Override public Optional<OnboardingResponse> findBySessionId(String s) { return jpa.findBySessionId(s); }
}

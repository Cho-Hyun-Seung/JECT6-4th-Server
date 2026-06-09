package com.ject6.boost.domain.onboarding.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "onboarding_responses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 100)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "step1_answer", length = 50)
    private String step1Answer;

    @Column(name = "step2_answer", length = 30)
    private String step2Answer;

    @Column(name = "step3_answer", length = 30)
    private String step3Answer;

    @Column(name = "step4_answer", length = 30)
    private String step4Answer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static OnboardingResponse create(String sessionId) {
        OnboardingResponse r = new OnboardingResponse();
        r.sessionId = sessionId;
        return r;
    }

    public void applyStep(int step, String answer) {
        switch (step) {
            case 1 -> this.step1Answer = answer;
            case 2 -> this.step2Answer = answer;
            case 3 -> this.step3Answer = answer;
            case 4 -> this.step4Answer = answer;
        }
    }

    public boolean isComplete() {
        return step1Answer != null && step2Answer != null && step3Answer != null && step4Answer != null;
    }

    public void mergeUser(Long userId) {
        this.userId = userId;
    }
}

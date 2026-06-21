package com.ject6.boost.infrastructure.common.queue;

import com.ject6.boost.infrastructure.common.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisQueuePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Long userId, Long documentId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANALYSIS_QUEUE,
                new AnalysisMessage(userId, documentId, null, null)
        );
    }

    public void publishWithCorrelation(Long userId, Long documentId, String correlationId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANALYSIS_QUEUE,
                new AnalysisMessage(userId, documentId, correlationId, null)
        );
    }

    public void publishWithMode(Long userId, Long documentId, String correlationId, String analysisMode) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANALYSIS_QUEUE,
                new AnalysisMessage(userId, documentId, correlationId, analysisMode)
        );
    }
}

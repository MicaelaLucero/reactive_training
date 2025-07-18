package cl.tenpo.learning.reactive.tasks.task2.event;

import static cl.tenpo.learning.reactive.tasks.task2.util.Topics.CR_RETRY_EXHAUSTED;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetryEventPublisher {

  private final ReactiveKafkaProducerTemplate<String, PercentageErrorEvent> producer;

  public Mono<Void> sendError(String errorMessage) {
    PercentageErrorEvent event = new PercentageErrorEvent(errorMessage);
    log.info("Sending event to topic '{}': {}", CR_RETRY_EXHAUSTED, event);

    return producer
        .send(CR_RETRY_EXHAUSTED, event)
        .doOnSuccess(senderResult -> log.info("Event sent successfully"))
        .doOnError(e -> log.error("Failed to send event: {} - {}", event, e.getMessage(), e))
        .then();
  }
}

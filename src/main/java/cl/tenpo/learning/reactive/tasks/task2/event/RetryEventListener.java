package cl.tenpo.learning.reactive.tasks.task2.event;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static cl.tenpo.learning.reactive.tasks.task2.util.Topics.CR_RETRY_EXHAUSTED;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryEventListener {
  private final ReactiveKafkaConsumerTemplate<String, PercentageErrorEvent> consumer;

  @PostConstruct
  public void run() {
    percentageErrorStream().subscribe();
  }

  public Flux<PercentageErrorEvent> percentageErrorStream() {
    return consumer
        .receiveAutoAck()
        .doOnSubscribe(sub -> log.info("Subscribed to topic: {}", CR_RETRY_EXHAUSTED))
        .map(ConsumerRecord::value)
        .doOnNext(event -> log.info("Event received with payload: {}", event))
        .onErrorContinue(
            (err, event) ->
                log.error("Error processing event: {} - cause: {}", event, err.getMessage(), err));
  }
}

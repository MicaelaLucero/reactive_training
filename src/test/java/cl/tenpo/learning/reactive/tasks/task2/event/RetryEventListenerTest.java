package cl.tenpo.learning.reactive.tasks.task2.event;

import static cl.tenpo.learning.reactive.tasks.task2.util.Topics.CR_RETRY_EXHAUSTED;
import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import java.time.Duration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;

public class RetryEventListenerTest {

  private ReactiveKafkaConsumerTemplate<String, PercentageErrorEvent> consumerTemplate;

  @BeforeEach
  void setUp() {
    consumerTemplate = mock(ReactiveKafkaConsumerTemplate.class);
  }

  @Test
  void shouldLogAndProcessIncomingEvent() {
    PercentageErrorEvent event = new PercentageErrorEvent("some error");
    ConsumerRecord<String, PercentageErrorEvent> record =
        new ConsumerRecord<>(CR_RETRY_EXHAUSTED, 0, 0L, "key", event);
    ReceiverRecord<String, PercentageErrorEvent> receiverRecord = mock(ReceiverRecord.class);
    when(receiverRecord.value()).thenReturn(event);

    when(consumerTemplate.receiveAutoAck()).thenReturn(Flux.just(record));

    Flux<PercentageErrorEvent> flux = listenerTestable().percentageErrorStream();

    StepVerifier.create(flux)
        .expectNextMatches(e -> e.error().equals("some error"))
        .expectComplete()
        .verify(Duration.ofSeconds(2));
  }

  private RetryEventListener listenerTestable() {
    return new RetryEventListener(consumerTemplate) {
      @Override
      public Flux<PercentageErrorEvent> percentageErrorStream() {
        return super.percentageErrorStream();
      }
    };
  }
}

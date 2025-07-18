package cl.tenpo.learning.reactive.tasks.task2.event;

import static cl.tenpo.learning.reactive.tasks.task2.util.Topics.CR_RETRY_EXHAUSTED;
import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

public class RetryEventPublisherTest {

  private ReactiveKafkaProducerTemplate<String, PercentageErrorEvent> producer;
  private RetryEventPublisher publisher;

  @BeforeEach
  void setUp() {
    producer = mock(ReactiveKafkaProducerTemplate.class);
    publisher = new RetryEventPublisher(producer);
  }

  @Test
  void shouldSendEventSuccessfully() {
    String errorMessage = "something went wrong";
    PercentageErrorEvent event = new PercentageErrorEvent(errorMessage);
    SenderResult<Void> mockResult = mock(SenderResult.class);
    when(producer.send(eq(CR_RETRY_EXHAUSTED), eq(event))).thenReturn(Mono.just(mockResult));

    Mono<Void> result = publisher.sendError(errorMessage);

    StepVerifier.create(result).verifyComplete();

    verify(producer).send(CR_RETRY_EXHAUSTED, event);
  }

  @Test
  void shouldHandleSendError() {
    String errorMessage = "something went wrong";
    PercentageErrorEvent event = new PercentageErrorEvent(errorMessage);

    when(producer.send(eq(CR_RETRY_EXHAUSTED), eq(event)))
        .thenReturn(Mono.error(new RuntimeException("Kafka is down")));

    Mono<Void> result = publisher.sendError(errorMessage);

    StepVerifier.create(result).expectErrorMessage("Kafka is down").verify();
  }
}

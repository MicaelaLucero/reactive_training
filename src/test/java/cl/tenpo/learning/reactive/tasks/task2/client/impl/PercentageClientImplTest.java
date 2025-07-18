package cl.tenpo.learning.reactive.tasks.task2.client.impl;

import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PercentageClientImplTest {

  private WebClient webClient;
  private ExchangeFunction exchangeFunction;
  private PercentageClientImpl client;

  @BeforeEach
  void setUp() {
    exchangeFunction = mock(ExchangeFunction.class);
    webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();

    client = new PercentageClientImpl(webClient);
    client.percentageUrl = "/percentage";
    client.timeout = 1L;
    client.maxRetries = 0L;
    client.backoff = 100L;
  }

  @Test
  void getPercentage_shouldReturnParsedValue() {
    ClientResponse response =
        ClientResponse.create(org.springframework.http.HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body("{\"percentage\":\"10.5\"}")
            .build();

    when(exchangeFunction.exchange(any())).thenReturn(Mono.just(response));

    Mono<Double> result = client.getPercentage();

    StepVerifier.create(result).expectNext(10.5).verifyComplete();
  }

  @Test
  void getPercentage_shouldFailOnTimeout() {
    when(exchangeFunction.exchange(any())).thenReturn(Mono.never());

    client.timeout = 1L;

    StepVerifier.create(client.getPercentage())
        .expectError(ExternalServiceException.class)
        .verify();
  }

  @Test
  void getPercentage_shouldRetryAndFail() {
    client.maxRetries = 2L;

    when(exchangeFunction.exchange(any())).thenReturn(Mono.error(new RuntimeException("Boom")));

    StepVerifier.create(client.getPercentage())
        .expectErrorMatches(
            e ->
                e instanceof ExternalServiceException
                    && e.getMessage().contains("Failed to get percentage"))
        .verify();

    verify(exchangeFunction, times(3)).exchange(any());
  }
}

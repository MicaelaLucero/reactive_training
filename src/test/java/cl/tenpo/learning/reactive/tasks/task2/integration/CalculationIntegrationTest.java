package cl.tenpo.learning.reactive.tasks.task2.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.event.RetryEventPublisher;
import cl.tenpo.learning.reactive.tasks.task2.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CalculationIntegrationTest {

  @Autowired private WebTestClient webTestClient;

  @MockBean private PercentageClient percentageClient;

  @MockBean private PercentageCacheService cache;

  @MockBean private RetryEventPublisher retryEventPublisher;

  @Test
  void shouldCalculateSuccessfully() {
    CalculationRequest request = new CalculationRequest(10.0, 5.0);

    when(percentageClient.getPercentage()).thenReturn(Mono.just(20.0));
    when(cache.save(20.0)).thenReturn(Mono.empty());

    webTestClient
        .post()
        .uri("/calculation")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.result")
        .isEqualTo(18.0);
  }

  @Test
  void shouldReturnBadRequestWhenRequestIsInvalid() {
    String body =
        """
      {
        "number1": 10
      }
    """;

    webTestClient
        .post()
        .uri("/calculation")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldUseCachedPercentageWhenClientFails() {
    CalculationRequest request = new CalculationRequest(10.0, 5.0);

    when(percentageClient.getPercentage())
        .thenReturn(Mono.error(new ExternalServiceException("API down")));
    when(retryEventPublisher.sendError(anyString())).thenReturn(Mono.empty());
    when(cache.get()).thenReturn(Mono.just(20.0));

    webTestClient
        .post()
        .uri("/calculation")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.result")
        .isEqualTo(18.0);
  }

  @Test
  void shouldRetryAndUseCacheWhenExternalServiceFails() {
    CalculationRequest request = new CalculationRequest(12.0, 8.0);

    when(percentageClient.getPercentage())
        .thenReturn(Mono.error(new ExternalServiceException("API down")));
    when(cache.get()).thenReturn(Mono.just(30.0));
    when(retryEventPublisher.sendError(anyString())).thenReturn(Mono.empty());

    webTestClient
        .post()
        .uri("/calculation")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.result")
        .isEqualTo(26.0);
  }

  @Test
  void shouldFallbackToCacheAndFailWhenNoCacheAvailable() {
    CalculationRequest request = new CalculationRequest(10.0, 5.0);

    when(percentageClient.getPercentage())
        .thenReturn(Mono.error(new ExternalServiceException("API down")));
    when(retryEventPublisher.sendError(anyString())).thenReturn(Mono.empty());
    when(cache.get()).thenReturn(Mono.empty());

    webTestClient
        .post()
        .uri("/calculation")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.error")
        .isEqualTo("No cached percentage available");
  }

  @Test
  void shouldReturnErrorWhenRetryEventFailsToSend() {
    CalculationRequest request = new CalculationRequest(8.0, 2.0);

    when(percentageClient.getPercentage())
        .thenReturn(Mono.error(new ExternalServiceException("API down")));
    when(cache.get()).thenReturn(Mono.error(new RuntimeException("Cache error")));
    when(retryEventPublisher.sendError(anyString()))
        .thenReturn(Mono.error(new RuntimeException("Kafka is down")));

    webTestClient
        .post()
        .uri("/calculation")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.error")
        .isEqualTo("Internal Server Error");
  }
}

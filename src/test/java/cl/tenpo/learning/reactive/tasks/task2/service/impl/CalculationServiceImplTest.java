package cl.tenpo.learning.reactive.tasks.task2.service.impl;

import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.event.RetryEventPublisher;
import cl.tenpo.learning.reactive.tasks.task2.exception.ExternalServiceException;
import cl.tenpo.learning.reactive.tasks.task2.exception.NoCachedPercentageException;
import cl.tenpo.learning.reactive.tasks.task2.service.CalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CalculationServiceImplTest {

  private PercentageClient percentageClient;
  private PercentageCacheService cache;
  private RetryEventPublisher retryEventPublisher;
  private CalculationService service;

  @BeforeEach
  void setUp() {
    percentageClient = mock(PercentageClient.class);
    cache = mock(PercentageCacheService.class);
    retryEventPublisher = mock(RetryEventPublisher.class);
    service = new CalculationServiceImpl(percentageClient, cache, retryEventPublisher);
  }

  @Test
  void calculate_successfulPercentageFetch_shouldReturnCalculatedResponse() {
    CalculationRequest request = new CalculationRequest(5.0, 5.0);
    when(percentageClient.getPercentage()).thenReturn(Mono.just(10.0));
    when(cache.save(10.0)).thenReturn(Mono.empty());

    StepVerifier.create(service.calculate(request))
        .expectNextMatches(response -> response.result() == 11.0)
        .verifyComplete();

    verify(cache).save(10.0);
    verifyNoInteractions(retryEventPublisher);
  }

  @Test
  void calculate_externalApiFails_butCachedPercentageExists_shouldReturnCalculatedResponse() {
    CalculationRequest request = new CalculationRequest(3.0, 3.0);
    when(percentageClient.getPercentage())
        .thenReturn(
            Mono.error(new ExternalServiceException("Failed to get percentage from external API")));
    when(retryEventPublisher.sendError("Failed to get percentage from external API"))
        .thenReturn(Mono.empty());
    when(cache.get()).thenReturn(Mono.just(5.0));

    StepVerifier.create(service.calculate(request))
        .expectNextMatches(response -> response.result() == 6.3)
        .verifyComplete();

    verify(retryEventPublisher).sendError("Failed to get percentage from external API");
    verify(cache).get();
  }

  @Test
  void calculate_externalApiFails_andNoCachedPercentage_shouldError() {
    CalculationRequest request = new CalculationRequest(1.0, 2.0);
    when(percentageClient.getPercentage())
        .thenReturn(
            Mono.error(new ExternalServiceException("Failed to get percentage from external API")));
    when(retryEventPublisher.sendError("Failed to get percentage from external API"))
        .thenReturn(Mono.empty());
    when(cache.get()).thenReturn(Mono.empty());

    StepVerifier.create(service.calculate(request))
        .expectError(NoCachedPercentageException.class)
        .verify();

    verify(retryEventPublisher).sendError("Failed to get percentage from external API");
    verify(cache).get();
  }
}

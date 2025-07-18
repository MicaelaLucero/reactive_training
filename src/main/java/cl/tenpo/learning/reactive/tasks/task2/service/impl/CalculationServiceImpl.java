package cl.tenpo.learning.reactive.tasks.task2.service.impl;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.event.RetryEventPublisher;
import cl.tenpo.learning.reactive.tasks.task2.exception.ExternalServiceException;
import cl.tenpo.learning.reactive.tasks.task2.exception.NoCachedPercentageException;
import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationResponse;
import cl.tenpo.learning.reactive.tasks.task2.service.CalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationServiceImpl implements CalculationService {

  private final PercentageClient percentageClient;
  private final PercentageCacheService cache;
  private final RetryEventPublisher retryEventPublisher;

  @Override
  public Mono<CalculationResponse> calculate(CalculationRequest request) {
    return fetchPercentage().map(percent -> calculateResponse(request, percent));
  }

  private CalculationResponse calculateResponse(CalculationRequest request, Double percentage) {
    double sum = request.number1() + request.number2();
    double result = sum + (sum * percentage / 100);
    log.info("Calculation result: sum={} percentage={} result={}", sum, percentage, result);
    return new CalculationResponse(result);
  }

  private Mono<Double> fetchPercentage() {
    return getFromExternalApi()
        .flatMap(this::saveInCache)
        .onErrorResume(ExternalServiceException.class, this::handleFallback);
  }

  private Mono<Double> getFromExternalApi() {
    return Mono.defer(percentageClient::getPercentage);
  }

  private Mono<Double> saveInCache(Double percentage) {
    log.info("Percentage obtained: {}", percentage);
    return cache.save(percentage).thenReturn(percentage);
  }

  private Mono<Double> handleFallback(ExternalServiceException ex) {
    log.warn("Falling back to cache due to external error: {}", ex.getMessage());
    return retryEventPublisher
        .sendError(ex.getMessage())
        .then(
            cache
                .get()
                .switchIfEmpty(
                    Mono.error(new NoCachedPercentageException("No cached percentage available"))));
  }
}

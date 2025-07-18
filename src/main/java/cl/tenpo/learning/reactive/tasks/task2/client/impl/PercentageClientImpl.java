package cl.tenpo.learning.reactive.tasks.task2.client.impl;

import cl.tenpo.learning.reactive.tasks.task2.client.PercentageClient;
import cl.tenpo.learning.reactive.tasks.task2.exception.ExternalServiceException;
import cl.tenpo.learning.reactive.tasks.task2.dto.PercentageResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
@Slf4j
public class PercentageClientImpl implements PercentageClient {

  @Value("${external.api.percentage.url}")
  public String percentageUrl;
  @Value("${external.api.max-retries}")
  public Long maxRetries;
  @Value("${external.api.timeout}")
  public Long timeout;
  @Value("${external.api.backoff}")
  public Long backoff;

  private final WebClient webClient;

  @Override
  public Mono<Double> getPercentage() {
    return webClient
        .get()
        .uri(percentageUrl)
        .retrieve()
        .bodyToMono(PercentageResponse.class)
        .timeout(Duration.ofSeconds(timeout))
        .map(PercentageResponse::parsed)
        .doOnNext(p -> log.info("Successfully fetched percentage from external API: {}", p))
        .retryWhen(
            Retry.backoff(maxRetries, Duration.ofMillis(backoff))
                .doBeforeRetry(
                    rs -> log.warn("Retrying fetch after timeout (attempt {})", rs.totalRetries())))
        .doOnError(e -> log.error("Final failure fetching percentage from API: {}", e.getMessage()))
        .onErrorMap(
            e -> new ExternalServiceException("Failed to get percentage from external API"));
  }
}
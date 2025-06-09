package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.exception.AuthorizationTimeoutException;
import cl.tenpo.learning.reactive.utils.exception.PaymentProcessingException;
import cl.tenpo.learning.reactive.utils.service.TransactionService;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.util.retry.Retry.backoff;

@Component
@RequiredArgsConstructor
@Slf4j
public class T1Question8 {

  private final TransactionService transactionService;

  public Mono<String> question8(int transactionId) {
    Mono<String> baseMono =
        transactionService
            .authorizeTransaction(transactionId)
            .doOnSubscribe(
                sub ->
                    log.info(
                        "[8] Subscribed to authorizeTransaction for transactionId: {}",
                        transactionId))
            .doOnNext(result -> log.info("[8] Received transaction result: {}", result));

    return applyTimeout(baseMono, transactionId)
        .transform(this::applyRetries)
        .transform(this::applyErrorMapping);
  }

  private Mono<String> applyTimeout(Mono<String> mono, int transactionId) {
    return mono.timeout(Duration.ofSeconds(3))
        .onErrorMap(
            throwable -> {
              if (throwable instanceof TimeoutException) {
                log.warn(
                    "[8] Transaction {} timeout detected, throwing AuthorizationTimeoutException",
                    transactionId,
                    throwable);
                return new AuthorizationTimeoutException("[8] Transaction timeout detected");
              }
              return throwable;
            });
  }

  private Mono<String> applyRetries(Mono<String> mono) {
    return mono.retryWhen(
        backoff(3, Duration.ofMillis(500))
            .filter(error -> !(error instanceof AuthorizationTimeoutException))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()));
  }

  private Mono<String> applyErrorMapping(Mono<String> mono) {
    return mono.onErrorMap(
        error -> {
          if (error instanceof AuthorizationTimeoutException) {
            return error;
          } else {
            log.error("[8] Payment processing failed, throwing PaymentProcessingException", error);
            return new PaymentProcessingException("[8] Payment processing failed", error);
          }
        });
  }
}

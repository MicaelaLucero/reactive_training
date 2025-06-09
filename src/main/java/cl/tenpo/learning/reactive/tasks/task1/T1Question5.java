package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CalculatorService;
import cl.tenpo.learning.reactive.utils.service.UserService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question5 {

  private final CalculatorService calculatorService;
  private final UserService userService;

  public Mono<String> question5A() {
    return buildCalculationFluxForRange()
        .collectList()
        .flatMap(list -> handleCalculationSuccessA())
        .onErrorResume(
            e -> {
              log.warn("[5A] Calculation failed", e);
              return Mono.just("Chuck Norris");
            });
  }

  public Flux<String> question5B() {
    return buildCalculationFluxForRange()
        .collectList()
        .flatMapMany(list -> handleCalculationSuccessB())
        .onErrorResume(
            e -> {
              log.warn("[5B] Calculation failed", e);
              return Flux.empty();
            });
  }

  private Flux<BigDecimal> buildCalculationFluxForRange() {
    return Flux.range(100, 901)
        .map(BigDecimal::valueOf)
        .concatMap(
            num ->
                calculatorService
                    .calculate(num)
                    .doOnError(e -> log.error("Error calculating for {}", num, e))
                    .onErrorMap(e -> new RuntimeException("Calculation failed", e)))
        .doOnSubscribe(sub -> log.info("Subscribed to calculations"));
  }

  private Mono<String> handleCalculationSuccessA() {
    log.info("[5A] All calculations done successfully");
    return userService
        .findFirstName()
        .doOnSubscribe(sub -> log.info("[5A] Subscribed to findFirstName"))
        .doOnNext(name -> log.info("[5A] Emitting name: {}", name));
  }

  private Flux<String> handleCalculationSuccessB() {
    log.info("[5B] All calculations done successfully");
    return userService
        .findAllNames()
        .doOnSubscribe(sub -> log.info("[5B] Subscribed to findAllNames"))
        .doOnNext(name -> log.info("[5B] Emitting name: {}", name))
        .take(3);
  }
}

package cl.tenpo.learning.reactive.tasks.task1;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question6 {

  public ConnectableFlux<Double> question6() {
    return Flux.interval(Duration.ofMillis(500))
        .map(tick -> generateRandomPrice())
        .doOnNext(price -> log.info("[6] Emitting stock price: {}", price))
        .publish();
  }

  private double generateRandomPrice() {
    return 1 + Math.random() * (500 - 1);
  }
}

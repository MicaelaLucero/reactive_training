package cl.tenpo.learning.reactive.tasks.task2.cache;

import reactor.core.publisher.Mono;

public interface PercentageCacheService {
  Mono<Void> save(Double percentage);
  Mono<Double> get();
  Mono<Void> clear();
}

package cl.tenpo.learning.reactive.tasks.task2.client;

import reactor.core.publisher.Mono;

public interface PercentageClient {
  Mono<Double> getPercentage();
}

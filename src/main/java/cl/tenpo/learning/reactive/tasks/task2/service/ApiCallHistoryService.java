package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiCallHistoryService {
  Mono<Void> log(ApiCallHistory log);

  Flux<ApiCallHistory> getAll();
}

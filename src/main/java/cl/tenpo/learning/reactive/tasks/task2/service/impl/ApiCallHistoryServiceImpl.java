package cl.tenpo.learning.reactive.tasks.task2.service.impl;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import cl.tenpo.learning.reactive.tasks.task2.repository.ApiCallHistoryRepository;
import cl.tenpo.learning.reactive.tasks.task2.service.ApiCallHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiCallHistoryServiceImpl implements ApiCallHistoryService {

  private final ApiCallHistoryRepository repository;

  @Override
  public Mono<Void> log(ApiCallHistory apiCallHistory) {
    return repository
        .save(apiCallHistory)
        .doOnError(e -> log.error("Failed to save api call", e))
        .onErrorResume(e -> Mono.empty())
        .then();
  }

  @Override
  public Flux<ApiCallHistory> getAll() {
    return repository.findAll();
  }
}

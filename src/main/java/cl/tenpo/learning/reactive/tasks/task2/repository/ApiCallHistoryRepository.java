package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ApiCallHistoryRepository extends ReactiveMongoRepository<ApiCallHistory, String> {}

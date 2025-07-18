package cl.tenpo.learning.reactive.tasks.task2.service.impl;

import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import cl.tenpo.learning.reactive.tasks.task2.repository.ApiCallHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ApiCallHistoryServiceImplTest {

  private ApiCallHistoryRepository repository;
  private ApiCallHistoryServiceImpl service;

  @BeforeEach
  void setUp() {
    repository = mock(ApiCallHistoryRepository.class);
    service = new ApiCallHistoryServiceImpl(repository);
  }

  @Test
  void log_shouldSaveSuccessfully() {
    ApiCallHistory history = ApiCallHistory.builder().endpoint("/test").httpStatus(200).build();

    when(repository.save(history)).thenReturn(Mono.just(history));

    StepVerifier.create(service.log(history)).verifyComplete();

    verify(repository).save(history);
  }

  @Test
  void log_shouldHandleSaveErrorGracefully() {
    ApiCallHistory history = ApiCallHistory.builder().endpoint("/fail").build();

    when(repository.save(history)).thenReturn(Mono.error(new RuntimeException("DB down")));

    StepVerifier.create(service.log(history)).verifyComplete();

    verify(repository).save(history);
  }

  @Test
  void getAll_shouldReturnAllEntries() {
    ApiCallHistory h1 = ApiCallHistory.builder().endpoint("/a").build();
    ApiCallHistory h2 = ApiCallHistory.builder().endpoint("/b").build();

    when(repository.findAll()).thenReturn(Flux.just(h1, h2));

    StepVerifier.create(service.getAll()).expectNext(h1).expectNext(h2).verifyComplete();
  }
}
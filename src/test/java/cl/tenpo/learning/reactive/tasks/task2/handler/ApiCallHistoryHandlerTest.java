package cl.tenpo.learning.reactive.tasks.task2.handler;

import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import cl.tenpo.learning.reactive.tasks.task2.service.ApiCallHistoryService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ApiCallHistoryHandlerTest {

  private ApiCallHistoryService service;
  private ApiCallHistoryHandler handler;

  @BeforeEach
  void setUp() {
    service = mock(ApiCallHistoryService.class);
    handler = new ApiCallHistoryHandler(service);
  }

  @Test
  void getAll_shouldReturnAllApiCallHistoryEntries() {
    ApiCallHistory entry1 = ApiCallHistory.builder()
                                          .id(String.valueOf(UUID.randomUUID()))
                                          .timestamp(LocalDateTime.now())
                                          .endpoint("/api/percentage")
                                          .httpStatus(200)
                                          .build();

    ApiCallHistory entry2 = ApiCallHistory.builder()
                                          .id(String.valueOf(UUID.randomUUID()))
                                          .timestamp(LocalDateTime.now())
                                          .endpoint("/api/calculate")
                                          .httpStatus(500)
                                          .build();

    when(service.getAll()).thenReturn(Flux.just(entry1, entry2));

    ServerRequest request = mock(ServerRequest.class);

    Mono<ServerResponse> response = handler.getAll(request);

    StepVerifier.create(response)
                .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
                .verifyComplete();

    verify(service).getAll();
  }
}
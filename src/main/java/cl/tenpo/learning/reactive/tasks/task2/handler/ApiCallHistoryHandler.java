package cl.tenpo.learning.reactive.tasks.task2.handler;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import cl.tenpo.learning.reactive.tasks.task2.service.ApiCallHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiCallHistoryHandler {

  private final ApiCallHistoryService service;

  public Mono<ServerResponse> getAll(ServerRequest request) {
    return ServerResponse.ok().body(service.getAll(), ApiCallHistory.class);
  }
}

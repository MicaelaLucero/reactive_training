package cl.tenpo.learning.reactive.tasks.task2.handler;

import static cl.tenpo.learning.reactive.tasks.task2.util.RequestUtils.getValidatedBody;

import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.service.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CalculationHandler {

  private final CalculationService calculationService;

  public Mono<ServerResponse> calculate(ServerRequest request) {
    var req = getValidatedBody(request, CalculationRequest.class);
    return calculationService
        .calculate(req)
        .flatMap(result -> ServerResponse.ok().bodyValue(result));
  }
}

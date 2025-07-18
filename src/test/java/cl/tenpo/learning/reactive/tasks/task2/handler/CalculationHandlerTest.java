package cl.tenpo.learning.reactive.tasks.task2.handler;

import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationResponse;
import cl.tenpo.learning.reactive.tasks.task2.service.CalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CalculationHandlerTest {

  private CalculationService calculationService;
  private CalculationHandler handler;

  @BeforeEach
  void setUp() {
    calculationService = mock(CalculationService.class);
    handler = new CalculationHandler(calculationService);
  }

  @Test
  void calculate_shouldReturnCalculationResponse() {
    CalculationRequest request = new CalculationRequest(5.0, 3.0);
    CalculationResponse response = new CalculationResponse(8.8);

    ServerRequest serverRequest = mock(ServerRequest.class);

    try (var mocked =
        Mockito.mockStatic(cl.tenpo.learning.reactive.tasks.task2.util.RequestUtils.class)) {
      mocked
          .when(
              () ->
                  cl.tenpo.learning.reactive.tasks.task2.util.RequestUtils.getValidatedBody(
                      serverRequest, CalculationRequest.class))
          .thenReturn(request);

      when(calculationService.calculate(request)).thenReturn(Mono.just(response));

      Mono<ServerResponse> result = handler.calculate(serverRequest);

      StepVerifier.create(result)
          .expectNextMatches(r -> r.statusCode().is2xxSuccessful())
          .verifyComplete();

      verify(calculationService).calculate(request);
    }
  }
}

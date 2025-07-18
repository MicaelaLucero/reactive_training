package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationResponse;
import reactor.core.publisher.Mono;

public interface CalculationService {
  Mono<CalculationResponse> calculate(CalculationRequest request);
}

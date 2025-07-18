package cl.tenpo.learning.reactive.tasks.task2.router;

import cl.tenpo.learning.reactive.tasks.task2.dto.CalculationRequest;
import cl.tenpo.learning.reactive.tasks.task2.filter.ValidationFilter;
import cl.tenpo.learning.reactive.tasks.task2.handler.CalculationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class CalculationRouter {

  @Bean
  public RouterFunction<ServerResponse> calculationRoutes(
      CalculationHandler handler, ValidationFilter validationFilter) {
    return RouterFunctions.route()
        .POST("/calculation", handler::calculate)
        .filter(validationFilter.validateBody(CalculationRequest.class))
        .build();
  }
}

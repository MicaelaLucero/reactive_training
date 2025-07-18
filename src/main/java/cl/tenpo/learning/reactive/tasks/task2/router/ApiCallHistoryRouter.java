package cl.tenpo.learning.reactive.tasks.task2.router;

import cl.tenpo.learning.reactive.tasks.task2.filter.AuthorizationFilter;
import cl.tenpo.learning.reactive.tasks.task2.handler.ApiCallHistoryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ApiCallHistoryRouter {

  @Bean
  public RouterFunction<ServerResponse> apiCallHistoryRoutes(
      ApiCallHistoryHandler handler, AuthorizationFilter authorizationFilter) {
    return RouterFunctions.route()
        .GET("/history", handler::getAll)
        .filter(authorizationFilter.requireAdmin())
        .build();
  }
}

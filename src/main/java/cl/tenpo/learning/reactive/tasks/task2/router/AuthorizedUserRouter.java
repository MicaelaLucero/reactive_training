package cl.tenpo.learning.reactive.tasks.task2.router;

import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserRequest;
import cl.tenpo.learning.reactive.tasks.task2.filter.ValidationFilter;
import cl.tenpo.learning.reactive.tasks.task2.handler.AuthorizedUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class AuthorizedUserRouter {

  @Bean
  public RouterFunction<ServerResponse> authorizedUserRoutes(
      AuthorizedUserHandler handler, ValidationFilter validationFilter) {
    return RouterFunctions.route()
        .add(
            RouterFunctions.route()
                .POST("/users", handler::createUser)
                .filter(validationFilter.validateBody(AuthorizedUserRequest.class))
                .build())
        .add(
            RouterFunctions.route()
                .DELETE("/users/{id}", handler::deleteUser)
                .GET("/users/{id}", handler::getById)
                .GET("/users", handler::getAll)
                .build())
        .build();
  }
}

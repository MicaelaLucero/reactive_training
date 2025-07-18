package cl.tenpo.learning.reactive.tasks.task2.router;

import cl.tenpo.learning.reactive.tasks.task2.handler.PercentageCacheHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class PercentageCacheRouter {

  @Bean
  public RouterFunction<ServerResponse> percentageCacheRoutes(PercentageCacheHandler handler) {
    return RouterFunctions.route()
        .add(
            RouterFunctions.route()
                .POST("/cache/percentage", handler::save)
                .GET("/cache/percentage", handler::get)
                .DELETE("/cache/percentage", handler::clear)
                .build())
        .build();
  }
}

package cl.tenpo.learning.reactive.tasks.task2.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

  private final ObjectMapper objectMapper;

  @Override
  @NonNull
  public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    if (exchange.getResponse().isCommitted()) {
      return Mono.error(ex);
    }

    if (ex instanceof BodyValidationException validationEx) {
      exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
      return writeJson(exchange, Map.of("errors", validationEx.getErrors()));
    }

    if (ex instanceof ResourceNotFoundException notFoundEx) {
      exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
      return writeJson(exchange, Map.of("error", notFoundEx.getMessage()));
    }

    if (ex instanceof UnauthorizedException uex) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return writeJson(exchange, Map.of("error", uex.getMessage()));
    }

    if (ex instanceof ForbiddenException fex) {
      exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
      return writeJson(exchange, Map.of("error", fex.getMessage()));
    }

    if (ex instanceof NoCachedPercentageException cacheEx) {
      exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
      return writeJson(exchange, Map.of("error", cacheEx.getMessage()));
    }

    log.error("Unhandled error", ex);
    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    return writeJson(exchange, Map.of("error", "Internal Server Error"));
  }

  private Mono<Void> writeJson(ServerWebExchange exchange, Map<String, Object> body) {
    try {
      byte[] bytes = objectMapper.writeValueAsBytes(body);
      DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
      return exchange.getResponse().writeWith(Mono.just(buffer));
    } catch (Exception e) {
      log.error("Error serializando respuesta", e);
      return exchange.getResponse().setComplete();
    }
  }
}

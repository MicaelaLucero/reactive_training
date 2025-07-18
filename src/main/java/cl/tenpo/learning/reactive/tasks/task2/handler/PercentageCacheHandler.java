package cl.tenpo.learning.reactive.tasks.task2.handler;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PercentageCacheHandler {

  private final PercentageCacheService cache;

  public Mono<ServerResponse> save(ServerRequest request) {
    return request
        .bodyToMono(Double.class)
        .flatMap(p -> cache.save(p).thenReturn(p))
        .flatMap(
            saved ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("saved", saved)));
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    return cache
        .get()
        .flatMap(
            value ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("percentage", value)))
        .switchIfEmpty(ServerResponse.noContent().build());
  }

  public Mono<ServerResponse> clear(ServerRequest request) {
    return cache
        .clear()
        .then(
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("message", "Cache cleared")));
  }
}

package cl.tenpo.learning.reactive.tasks.task2.handler;

import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PercentageCacheHandlerTest {

  private PercentageCacheService cache;
  private PercentageCacheHandler handler;

  @BeforeEach
  void setUp() {
    cache = mock(PercentageCacheService.class);
    handler = new PercentageCacheHandler(cache);
  }

  @Test
  void save_shouldStoreValueAndReturnIt() {
    double value = 12.5;

    ServerRequest request = mock(ServerRequest.class);
    when(request.bodyToMono(Double.class)).thenReturn(Mono.just(value));
    when(cache.save(value)).thenReturn(Mono.empty());

    Mono<ServerResponse> response = handler.save(request);

    StepVerifier.create(response)
        .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
        .verifyComplete();

    verify(cache).save(value);
  }

  @Test
  void get_shouldReturnCachedValue() {
    double cached = 15.0;
    when(cache.get()).thenReturn(Mono.just(cached));

    ServerRequest request = MockServerRequest.builder().build();

    Mono<ServerResponse> response = handler.get(request);

    StepVerifier.create(response)
        .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
        .verifyComplete();

    verify(cache).get();
  }

  @Test
  void get_shouldReturnNoContentIfCacheEmpty() {
    when(cache.get()).thenReturn(Mono.empty());

    ServerRequest request = MockServerRequest.builder().build();

    Mono<ServerResponse> response = handler.get(request);

    StepVerifier.create(response)
        .expectNextMatches(
            res -> res.statusCode().is2xxSuccessful() && res.statusCode().value() == 204)
        .verifyComplete();
  }

  @Test
  void clear_shouldClearCache() {
    when(cache.clear()).thenReturn(Mono.empty());

    ServerRequest request = MockServerRequest.builder().build();

    Mono<ServerResponse> response = handler.clear(request);

    StepVerifier.create(response)
        .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
        .verifyComplete();

    verify(cache).clear();
  }
}
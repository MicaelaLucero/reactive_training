package cl.tenpo.learning.reactive.tasks.task2.cache.impl;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.CACHE_KEY;
import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PercentageCacheServiceImplTest {

  @Mock private ReactiveRedisTemplate<String, Double> redisTemplate;
  @Mock private ReactiveValueOperations<String, Double> valueOperations;

  private PercentageCacheService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    PercentageCacheServiceImpl impl = new PercentageCacheServiceImpl(redisTemplate);
    impl.ttl = 30L;
    service = impl;
  }

  @Test
  void save_shouldStoreValue() {
    Double value = 10.0;
    when(valueOperations.set(CACHE_KEY, value, Duration.ofMinutes(30))).thenReturn(Mono.just(true));

    StepVerifier.create(service.save(value)).verifyComplete();

    verify(valueOperations).set(CACHE_KEY, value, Duration.ofMinutes(30));
  }

  @Test
  void get_shouldReturnValue() {
    Double cached = 15.0;
    when(valueOperations.get(CACHE_KEY)).thenReturn(Mono.just(cached));

    StepVerifier.create(service.get()).expectNext(cached).verifyComplete();

    verify(valueOperations).get(CACHE_KEY);
  }

  @Test
  void clear_shouldDeleteKey() {
    when(redisTemplate.delete(eq(CACHE_KEY))).thenReturn(Mono.just(1L));

    StepVerifier.create(service.clear()).verifyComplete();

    verify(redisTemplate).delete(CACHE_KEY);
  }
}

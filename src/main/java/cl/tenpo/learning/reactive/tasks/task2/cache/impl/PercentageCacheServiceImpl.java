package cl.tenpo.learning.reactive.tasks.task2.cache.impl;

import cl.tenpo.learning.reactive.tasks.task2.cache.PercentageCacheService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.CACHE_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageCacheServiceImpl implements PercentageCacheService {

  @Value("${spring.data.redis.ttl}")
  public Long ttl;

  private final ReactiveRedisTemplate<String, Double> redisTemplate;

  @Override
  public Mono<Void> save(Double percentage) {
    return redisTemplate
        .opsForValue()
        .set(CACHE_KEY, percentage, Duration.ofMinutes(ttl))
        .doOnSuccess(success -> log.info("Saved percentage {} to Redis cache", percentage))
        .doOnError(e -> log.warn("Error saving percentage to Redis cache", e))
        .then();
  }

  @Override
  public Mono<Double> get() {
    return redisTemplate
        .opsForValue()
        .get(CACHE_KEY)
        .doOnNext(p -> log.info("Retrieved percentage {} from Redis cache", p))
        .doOnError(e -> log.warn("Error retrieving percentage from Redis cache", e));
  }

  @Override
  public Mono<Void> clear() {
    return redisTemplate
        .delete(CACHE_KEY)
        .doOnSuccess(deleted -> log.info("Deleted percentage from Redis cache"))
        .doOnError(e -> log.warn("Error deleting percentage from Redis cache", e))
        .then();
  }
}

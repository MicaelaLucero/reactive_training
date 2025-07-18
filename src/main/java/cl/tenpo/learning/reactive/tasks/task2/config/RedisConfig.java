package cl.tenpo.learning.reactive.tasks.task2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public ReactiveRedisTemplate<String, Double> percentageRedisTemplate(
      ReactiveRedisConnectionFactory factory) {

    RedisSerializationContext<String, Double> context = RedisSerializationContext
        .<String, Double>newSerializationContext(new StringRedisSerializer())
        .value(new GenericToStringSerializer<>(Double.class))
        .build();

    return new ReactiveRedisTemplate<>(factory, context);
  }
}
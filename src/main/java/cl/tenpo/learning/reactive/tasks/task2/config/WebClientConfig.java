package cl.tenpo.learning.reactive.tasks.task2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

  @Value("${external.api.base-url}")
  private String externalApiBaseUrl;

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl(externalApiBaseUrl).build();
  }
}
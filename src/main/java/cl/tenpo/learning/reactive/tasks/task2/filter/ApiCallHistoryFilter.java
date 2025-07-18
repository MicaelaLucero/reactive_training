package cl.tenpo.learning.reactive.tasks.task2.filter;

import cl.tenpo.learning.reactive.tasks.task2.entity.ApiCallHistory;
import cl.tenpo.learning.reactive.tasks.task2.service.ApiCallHistoryService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiCallHistoryFilter implements WebFilter {

  private final ApiCallHistoryService callHistoryService;

  @Override
  @NonNull
  public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().value();

    if (shouldSkipLogging(path)) {
      return chain.filter(exchange);
    }

    String method = Optional.of(request.getMethod()).map(HttpMethod::name).orElse(UNKNOWN);

    String queryParams = request.getQueryParams().toString();

    return chain
        .filter(exchange)
        .doOnError(error -> logCall(exchange, method, path, queryParams, error.getMessage(), true))
        .doFinally(
            signal -> {
              if (signal != SignalType.ON_ERROR) {
                logCall(exchange, method, path, queryParams, OK, false);
              }
            });
  }

  private boolean shouldSkipLogging(String path) {
    return path.startsWith(HISTORY_PATH_PREFIX);
  }

  private void logCall(
      ServerWebExchange exchange,
      String method,
      String path,
      String queryParams,
      String responseBody,
      boolean isError) {

    int status = extractStatusCode(exchange, isError);

    ApiCallHistory history = buildHistory(method, path, queryParams, responseBody, status);

    callHistoryService.log(history).doOnError(e -> log.error("Error logging API call history", e)).subscribe();
  }

  private int extractStatusCode(ServerWebExchange exchange, boolean isError) {
    return Optional.ofNullable(exchange.getResponse().getStatusCode())
        .map(HttpStatusCode::value)
        .orElse(isError ? 500 : 200);
  }

  private ApiCallHistory buildHistory(
      String method, String path, String queryParams, String responseBody, int status) {
    return ApiCallHistory.builder()
        .timestamp(LocalDateTime.now())
        .method(method)
        .endpoint(path)
        .queryParams(queryParams)
        .httpStatus(status)
        .responseBody(responseBody)
        .build();
  }
}

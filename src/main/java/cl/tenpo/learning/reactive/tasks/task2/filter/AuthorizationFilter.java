package cl.tenpo.learning.reactive.tasks.task2.filter;

import cl.tenpo.learning.reactive.tasks.task2.exception.ForbiddenException;
import cl.tenpo.learning.reactive.tasks.task2.exception.UnauthorizedException;
import cl.tenpo.learning.reactive.tasks.task2.entity.UserRole;
import cl.tenpo.learning.reactive.tasks.task2.repository.AuthorizedUserRepository;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.ERROR_FORBIDDEN;
import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.ERROR_MISSING_USER_ID;
import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.ERROR_USER_NOT_FOUND;
import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.HEADER_USER_ID;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter {

  private final AuthorizedUserRepository repository;

  public HandlerFilterFunction<ServerResponse, ServerResponse> requireAdmin() {
    return (request, next) -> {
      var userId = request.headers().firstHeader(HEADER_USER_ID);
      if (userId == null) {
        return Mono.error(new UnauthorizedException(ERROR_MISSING_USER_ID));
      }

      return repository
          .findById(UUID.fromString(userId))
          .switchIfEmpty(Mono.error(new UnauthorizedException(ERROR_USER_NOT_FOUND)))
          .flatMap(
              user -> {
                if (Objects.equals(user.getRole(), UserRole.ADMIN)) {
                  return next.handle(request);
                } else {
                  return Mono.error(new ForbiddenException(ERROR_FORBIDDEN));
                }
              });
    };
  }
}

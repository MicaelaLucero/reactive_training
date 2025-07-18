package cl.tenpo.learning.reactive.tasks.task2.handler;

import static cl.tenpo.learning.reactive.tasks.task2.util.RequestUtils.getValidatedBody;

import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserResponse;
import cl.tenpo.learning.reactive.tasks.task2.exception.ResourceNotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.service.AuthorizedUserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthorizedUserHandler {
  private final AuthorizedUserService service;

  public Mono<ServerResponse> createUser(ServerRequest request) {
    var req = getValidatedBody(request, AuthorizedUserRequest.class);
    return service.createUser(req).flatMap(saved -> ServerResponse.ok().bodyValue(saved));
  }

  public Mono<ServerResponse> deleteUser(ServerRequest request) {
    UUID id = UUID.fromString(request.pathVariable("id"));
    return service.deleteUser(id).then(ServerResponse.noContent().build());
  }

  public Mono<ServerResponse> getById(ServerRequest request) {
    UUID id = UUID.fromString(request.pathVariable("id"));
    return service.getById(id).flatMap(user -> ServerResponse.ok().bodyValue(user));
  }

  public Mono<ServerResponse> getAll(ServerRequest request) {
    return ServerResponse.ok().body(service.getAll(), AuthorizedUserResponse.class);
  }
}

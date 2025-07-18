package cl.tenpo.learning.reactive.tasks.task2.handler;

import static cl.tenpo.learning.reactive.tasks.task2.entity.UserRole.ADMIN;
import static cl.tenpo.learning.reactive.tasks.task2.entity.UserRole.USER;
import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserResponse;
import cl.tenpo.learning.reactive.tasks.task2.service.AuthorizedUserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class AuthorizedUserHandlerTest {

  private AuthorizedUserService service;
  private AuthorizedUserHandler handler;

  @BeforeEach
  void setUp() {
    service = mock(AuthorizedUserService.class);
    handler = new AuthorizedUserHandler(service);
  }

  @Test
  void createUser_shouldReturnCreatedUser() {
    var request = new AuthorizedUserRequest("Mica", "mica@mail.com", ADMIN);
    var response = new AuthorizedUserResponse(UUID.randomUUID(), "Mica", "mica@mail.com", ADMIN);

    ServerRequest serverRequest = mock(ServerRequest.class);

    try (var mocked =
        Mockito.mockStatic(cl.tenpo.learning.reactive.tasks.task2.util.RequestUtils.class)) {
      mocked
          .when(
              () ->
                  cl.tenpo.learning.reactive.tasks.task2.util.RequestUtils.getValidatedBody(
                      serverRequest, AuthorizedUserRequest.class))
          .thenReturn(request);

      when(service.createUser(request)).thenReturn(Mono.just(response));

      StepVerifier.create(handler.createUser(serverRequest))
          .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
          .verifyComplete();

      verify(service).createUser(request);
    }
  }

  @Test
  void deleteUser_shouldReturnNoContent() {
    UUID id = UUID.randomUUID();
    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariable("id")).thenReturn(id.toString());

    when(service.deleteUser(id)).thenReturn(Mono.empty());

    StepVerifier.create(handler.deleteUser(request))
        .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
        .verifyComplete();

    verify(service).deleteUser(id);
  }

  @Test
  void getById_shouldReturnUser() {
    UUID id = UUID.randomUUID();
    var user = new AuthorizedUserResponse(id, "Mica", "mica@mail.com", ADMIN);

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariable("id")).thenReturn(id.toString());

    when(service.getById(id)).thenReturn(Mono.just(user));

    StepVerifier.create(handler.getById(request))
        .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
        .verifyComplete();

    verify(service).getById(id);
  }

  @Test
  void getAll_shouldReturnAllUsers() {
    var user1 = new AuthorizedUserResponse(UUID.randomUUID(), "Mica", "mica@mail.com", ADMIN);
    var user2 = new AuthorizedUserResponse(UUID.randomUUID(), "Martin", "martin@mail.com", USER);

    ServerRequest request = mock(ServerRequest.class);
    when(service.getAll()).thenReturn(Flux.fromIterable(List.of(user1, user2)));

    StepVerifier.create(handler.getAll(request))
        .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
        .verifyComplete();

    verify(service).getAll();
  }
}

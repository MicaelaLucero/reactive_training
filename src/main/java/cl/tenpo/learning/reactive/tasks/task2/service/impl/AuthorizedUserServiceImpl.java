package cl.tenpo.learning.reactive.tasks.task2.service.impl;

import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserResponse;
import cl.tenpo.learning.reactive.tasks.task2.entity.AuthorizedUser;
import cl.tenpo.learning.reactive.tasks.task2.exception.ResourceNotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.repository.AuthorizedUserRepository;
import cl.tenpo.learning.reactive.tasks.task2.service.AuthorizedUserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.ERROR_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthorizedUserServiceImpl implements AuthorizedUserService {

  private final AuthorizedUserRepository repository;

  @Override
  public Mono<AuthorizedUserResponse> createUser(AuthorizedUserRequest request) {
    AuthorizedUser user =
        AuthorizedUser.builder()
            .name(request.name())
            .email(request.email())
            .role(request.role())
            .build();

    return repository.save(user).map(this::toResponse);
  }

  @Override
  public Mono<Void> deleteUser(UUID id) {
    return repository
        .findById(id)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException(ERROR_USER_NOT_FOUND)))
        .flatMap(user -> repository.deleteById(id));
  }

  @Override
  public Mono<AuthorizedUserResponse> getById(UUID id) {
    return repository
        .findById(id)
        .switchIfEmpty(
            Mono.error(new ResourceNotFoundException(ERROR_USER_NOT_FOUND)))
        .map(this::toResponse);
  }

  @Override
  public Flux<AuthorizedUserResponse> getAll() {
    return repository.findAll().map(this::toResponse);
  }

  private AuthorizedUserResponse toResponse(AuthorizedUser user) {
    return new AuthorizedUserResponse(
        user.getId(), user.getName(), user.getEmail(), user.getRole());
  }
}

package cl.tenpo.learning.reactive.tasks.task2.service;

import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserRequest;
import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserResponse;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthorizedUserService {
  Mono<AuthorizedUserResponse> createUser(AuthorizedUserRequest request);
  Mono<Void> deleteUser(UUID id);
  Mono<AuthorizedUserResponse> getById(UUID id);
  Flux<AuthorizedUserResponse> getAll();
}
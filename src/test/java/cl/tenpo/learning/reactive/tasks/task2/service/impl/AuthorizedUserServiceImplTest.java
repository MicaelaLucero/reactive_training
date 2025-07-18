package cl.tenpo.learning.reactive.tasks.task2.service.impl;

import static cl.tenpo.learning.reactive.tasks.task2.entity.UserRole.ADMIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.tenpo.learning.reactive.tasks.task2.dto.AuthorizedUserRequest;
import cl.tenpo.learning.reactive.tasks.task2.entity.AuthorizedUser;
import cl.tenpo.learning.reactive.tasks.task2.exception.ResourceNotFoundException;
import cl.tenpo.learning.reactive.tasks.task2.repository.AuthorizedUserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AuthorizedUserServiceImplTest {

  private AuthorizedUserRepository repository;
  private AuthorizedUserServiceImpl service;

  @BeforeEach
  void setUp() {
    repository = mock(AuthorizedUserRepository.class);
    service = new AuthorizedUserServiceImpl(repository);
  }

  @Test
  void createUser_shouldReturnSavedUser() {
    AuthorizedUserRequest request = new AuthorizedUserRequest("Mica", "mica@example.com", ADMIN);
    AuthorizedUser savedUser =
        AuthorizedUser.builder()
            .id(UUID.randomUUID())
            .name("Mica")
            .email("mica@example.com")
            .role(ADMIN)
            .build();

    when(repository.save(any())).thenReturn(Mono.just(savedUser));

    StepVerifier.create(service.createUser(request))
        .expectNextMatches(res -> res.name().equals("Mica") && res.role() == ADMIN)
        .verifyComplete();

    verify(repository).save(any());
  }

  @Test
  void deleteUser_existingUser_shouldDeleteSuccessfully() {
    UUID id = UUID.randomUUID();
    AuthorizedUser user = AuthorizedUser.builder().id(id).build();

    when(repository.findById(id)).thenReturn(Mono.just(user));
    when(repository.deleteById(id)).thenReturn(Mono.empty());

    StepVerifier.create(service.deleteUser(id)).verifyComplete();

    verify(repository).deleteById(id);
  }

  @Test
  void deleteUser_nonExistentUser_shouldThrowNotFound() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Mono.empty());

    StepVerifier.create(service.deleteUser(id))
        .expectError(ResourceNotFoundException.class)
        .verify();
  }

  @Test
  void getById_existingUser_shouldReturnUser() {
    UUID id = UUID.randomUUID();
    AuthorizedUser user =
        AuthorizedUser.builder().id(id).name("Mica").email("mica@example.com").role(ADMIN).build();

    when(repository.findById(id)).thenReturn(Mono.just(user));

    StepVerifier.create(service.getById(id))
        .expectNextMatches(res -> res.email().equals("mica@example.com"))
        .verifyComplete();
  }

  @Test
  void getById_nonExistentUser_shouldThrowNotFound() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Mono.empty());

    StepVerifier.create(service.getById(id)).expectError(ResourceNotFoundException.class).verify();
  }

  @Test
  void getAll_shouldReturnListOfUsers() {
    AuthorizedUser user1 =
        AuthorizedUser.builder()
            .id(UUID.randomUUID())
            .name("A")
            .email("a@mail.com")
            .role(ADMIN)
            .build();
    AuthorizedUser user2 =
        AuthorizedUser.builder()
            .id(UUID.randomUUID())
            .name("B")
            .email("b@mail.com")
            .role(ADMIN)
            .build();

    when(repository.findAll()).thenReturn(Flux.just(user1, user2));

    StepVerifier.create(service.getAll()).expectNextCount(2).verifyComplete();
  }
}

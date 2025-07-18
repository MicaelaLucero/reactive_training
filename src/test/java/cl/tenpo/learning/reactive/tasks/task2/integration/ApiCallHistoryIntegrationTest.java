package cl.tenpo.learning.reactive.tasks.task2.integration;

import cl.tenpo.learning.reactive.tasks.task2.T2Application;
import cl.tenpo.learning.reactive.tasks.task2.entity.AuthorizedUser;
import cl.tenpo.learning.reactive.tasks.task2.entity.UserRole;
import cl.tenpo.learning.reactive.tasks.task2.repository.AuthorizedUserRepository;
import cl.tenpo.learning.reactive.tasks.task2.testConfig.E2ETestConfiguration;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.HEADER_USER_ID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ApiCallHistoryIntegrationTest {

  @Autowired private WebTestClient webTestClient;

  @Autowired private AuthorizedUserRepository authorizedUserRepository;

  private UUID adminId;
  private UUID nonAdminId;

  @BeforeEach
  void setUp() {
    authorizedUserRepository.deleteAll().block();

    AuthorizedUser user1 =
        AuthorizedUser.builder().name("Mica").email("mica@mail.com").role(UserRole.ADMIN).build();

    AuthorizedUser user2 =
        AuthorizedUser.builder().name("Juan").email("juan@mail.com").role(UserRole.USER).build();

    adminId = Objects.requireNonNull(authorizedUserRepository.save(user1).block()).getId();
    nonAdminId = Objects.requireNonNull(authorizedUserRepository.save(user2).block()).getId();
  }

  @Test
  void shouldReturnOkWhenAdminUserCallsHistory() {
    webTestClient
        .get()
        .uri("/history")
        .header(HEADER_USER_ID, adminId.toString())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldReturnUnauthorizedWhenNoUserIdProvided() {
    webTestClient
        .get()
        .uri("/history")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.error")
        .isEqualTo("Missing user ID");
  }

  @Test
  void shouldReturnUnauthorizedWhenUserNotFound() {
    webTestClient
        .get()
        .uri("/history")
        .header(HEADER_USER_ID, UUID.randomUUID().toString())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .jsonPath("$.error")
        .isEqualTo("User not found");
  }

  @Test
  void shouldReturnForbiddenWhenUserIsNotAdmin() {
    webTestClient
        .get()
        .uri("/history")
        .header(HEADER_USER_ID, nonAdminId.toString())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectBody()
        .jsonPath("$.error")
        .isEqualTo("Insufficient permissions");
  }
}

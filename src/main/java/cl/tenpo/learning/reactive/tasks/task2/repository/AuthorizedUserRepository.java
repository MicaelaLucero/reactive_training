package cl.tenpo.learning.reactive.tasks.task2.repository;

import cl.tenpo.learning.reactive.tasks.task2.entity.AuthorizedUser;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedUserRepository extends ReactiveCrudRepository<AuthorizedUser, UUID> {}

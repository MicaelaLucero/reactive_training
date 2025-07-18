package cl.tenpo.learning.reactive.tasks.task2.dto;

import cl.tenpo.learning.reactive.tasks.task2.entity.UserRole;
import java.util.UUID;

public record AuthorizedUserResponse(UUID id, String name, String email, UserRole role) {}

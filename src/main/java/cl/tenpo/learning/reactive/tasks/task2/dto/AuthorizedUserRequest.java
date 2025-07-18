package cl.tenpo.learning.reactive.tasks.task2.dto;

import cl.tenpo.learning.reactive.tasks.task2.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorizedUserRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Email is required") @Email(message = "Email format is invalid")
        String email,
    @NotNull(message = "Role is required") UserRole role) {}
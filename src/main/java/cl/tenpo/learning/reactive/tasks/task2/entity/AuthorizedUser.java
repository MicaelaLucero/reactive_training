package cl.tenpo.learning.reactive.tasks.task2.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("authorized_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedUser {
  @Id private UUID id;
  private String name;
  private String email;
  private UserRole role;
}

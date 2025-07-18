package cl.tenpo.learning.reactive.tasks.task2.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class BodyValidationException extends RuntimeException {
  private final List<String> errors;

  public BodyValidationException(List<String> errors) {
    super("Body validation failed");
    this.errors = errors;
  }
}

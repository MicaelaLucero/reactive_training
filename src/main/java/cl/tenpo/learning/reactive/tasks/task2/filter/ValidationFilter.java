package cl.tenpo.learning.reactive.tasks.task2.filter;

import cl.tenpo.learning.reactive.tasks.task2.exception.BodyValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.VALIDATED_BODY;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidationFilter {

  private final Validator validator;

  public <T> HandlerFilterFunction<ServerResponse, ServerResponse> validateBody(Class<T> clazz) {
    return (request, next) ->
        request
            .bodyToMono(clazz)
            .flatMap(
                body -> {
                  log.info("Validating request from class: {}", clazz.getSimpleName());
                  var violations = validator.validate(body);
                  if (!violations.isEmpty()) {
                    List<String> errors =
                        violations.stream().map(ConstraintViolation::getMessage).toList();
                    throw new BodyValidationException(errors);
                  }
                  return next.handle(
                      ServerRequest.from(request).attribute(VALIDATED_BODY, body).build());
                });
  }
}

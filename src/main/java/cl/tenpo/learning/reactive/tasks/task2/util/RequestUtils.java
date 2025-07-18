package cl.tenpo.learning.reactive.tasks.task2.util;

import org.springframework.web.reactive.function.server.ServerRequest;

import static cl.tenpo.learning.reactive.tasks.task2.util.Constants.VALIDATED_BODY;

public class RequestUtils {

  @SuppressWarnings("unchecked")
  public static <T> T getValidatedBody(ServerRequest request, Class<T> clazz) {
    Object body = request.attributes().get(VALIDATED_BODY);
    if (body == null) {
      throw new IllegalStateException("Missing validated body of type " + clazz.getSimpleName());
    }
    if (!clazz.isInstance(body)) {
      throw new IllegalStateException(
          "Invalid validated body type, expected: " + clazz.getSimpleName());
    }
    return (T) body;
  }
}

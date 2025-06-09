package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.exception.ResourceNotFoundException;
import cl.tenpo.learning.reactive.utils.exception.UserServiceException;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question1 {

  private final UserService userService;

  public Mono<Integer> question1A() {
    return userService
        .findFirstName()
        .doOnSubscribe(sub -> log.info("[1A] Subscribed to findFirstName"))
        .doOnNext(name -> log.info("[1A] Processing name: {}", name))
        .filter(name -> name.startsWith("A"))
        .map(String::length)
        .defaultIfEmpty(-1)
        .doOnSuccess(result -> log.info("[1A] Completed successfully. Result: {}", result))
        .doOnError(error -> log.error("[1A] Error processing the name", error));
  }

  public Mono<String> question1B() {
    return userService
        .findFirstName()
        .doOnSubscribe(sub -> log.info("[1B] Subscribed to findFirstName"))
        .doOnNext(name -> log.info("[1B] Processing name: {}", name))
        .flatMap(this::processName)
        .doOnSuccess(result -> log.info("[1B] Completed successfully. Result: {}", result))
        .doOnError(error -> log.error("[1B] Error processing the name", error));
  }

  public Mono<String> question1C(String name) {
    return userService
        .findFirstByName(name)
        .doOnSubscribe(sub -> log.info("[1C] Subscribed to findFirstByName with name: {}", name))
        .doOnNext(result -> log.info("[1C] Processing result for name '{}'", name))
        .switchIfEmpty(Mono.error(new ResourceNotFoundException()))
        .onErrorResume(this::handleFindFirstByNameError)
        .doOnSuccess(result -> log.info("[1C] Completed successfully. Result: {}", result))
        .doOnError(
            error ->
                log.error(
                    "[1C] Error processing the name, Exception: '{}'",
                    error.getClass().getSimpleName()));
  }

  private Mono<String> processName(String name) {
    return userService
        .existByName(name)
        .doOnNext(exists -> log.info("[1B] Name '{}' exists in DB: {}", name, exists))
        .flatMap(exists -> handleUpdateOrInsert(name, exists));
  }

  private Mono<String> handleUpdateOrInsert(String name, Boolean exists) {
    if (exists) {
      return userService
          .update(name)
          .doOnNext(updated -> log.info("[1B] Updating name '{}': {}", name, updated));
    } else {
      return userService
          .insert(name)
          .doOnNext(inserted -> log.info("[1B] Inserting name '{}': {}", name, inserted));
    }
  }

  private Mono<String> handleFindFirstByNameError(Throwable error) {
    if (error instanceof ResourceNotFoundException) {
      return Mono.error(error);
    } else {
      return Mono.error(new UserServiceException());
    }
  }
}

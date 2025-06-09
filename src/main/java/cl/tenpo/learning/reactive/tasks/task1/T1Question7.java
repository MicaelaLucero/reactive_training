package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.model.Account;
import cl.tenpo.learning.reactive.utils.model.User;
import cl.tenpo.learning.reactive.utils.model.UserAccount;
import cl.tenpo.learning.reactive.utils.service.AccountService;
import cl.tenpo.learning.reactive.utils.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question7 {

  private final UserService userService;
  private final AccountService accountService;

  public Mono<UserAccount> question7(String userId) {
    return Mono.zip(getUser(userId), getAccount(userId))
        .map(tuple -> new UserAccount(tuple.getT1(), tuple.getT2()))
        .doOnSuccess(userAccount -> log.info("[7] Created UserAccount: {}", userAccount))
        .doOnError(error -> log.error("[7] Error creating UserAccount", error));
  }

  private Mono<User> getUser(String userId) {
    return userService
        .getUserById(userId)
        .doOnSubscribe(sub -> log.info("[7] Subscribed to getUserById for userId: {}", userId))
        .doOnNext(user -> log.info("[7] Retrieved user: {}", user));
  }

  private Mono<Account> getAccount(String userId) {
    return accountService
        .getAccountByUserId(userId)
        .doOnSubscribe(
            sub -> log.info("[7] Subscribed to getAccountByUserId for userId: {}", userId))
        .doOnNext(account -> log.info("[7] Retrieved account: {}", account));
  }
}

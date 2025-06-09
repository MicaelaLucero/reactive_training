package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question2 {

  private final CountryService countryService;

  public Flux<String> question2A() {
    return countryService
        .findAllCountries()
        .doOnSubscribe(sub -> log.info("[2A] Subscribed to findAllCountries"))
        .doOnNext(country -> log.info("[2A] Processing country: {}", country))
        .distinct()
        .take(5)
        .doOnComplete(() -> log.info("[2A] Completed successfully"))
        .doOnError(error -> log.error("[2A] Error processing countries", error));
  }

  public Flux<String> question2B() {
    return countryService
        .findAllCountries()
        .doOnSubscribe(sub -> log.info("[2B] Subscribed to findAllCountries"))
        .doOnNext(country -> log.info("[2B] Processing country: {}", country))
        .takeUntil(country -> country.equals("Argentina"))
        .doOnComplete(() -> log.info("[2B] Completed successfully"))
        .doOnError(error -> log.error("[2B] Error processing countries", error));
  }

  public Flux<String> question2C() {
    return countryService
        .findAllCountries()
        .doOnSubscribe(sub -> log.info("[2C] Subscribed to findAllCountries"))
        .doOnNext(country -> log.info("[2C] Processing country: {}", country))
        .takeWhile(country -> !country.equals("France"))
        .doOnComplete(() -> log.info("[2C] Completed successfully"))
        .doOnError(error -> log.error("[2C] Error processing countries", error));
  }
}

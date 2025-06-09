package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.model.Page;
import cl.tenpo.learning.reactive.utils.service.CountryService;
import cl.tenpo.learning.reactive.utils.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question3 {

  private final CountryService countryService;
  private final TranslatorService translatorService;

  public Flux<String> question3A(Page<String> page) {
    return Mono.justOrEmpty(page)
        .flatMapMany(p -> Flux.fromIterable(p.items()))
        .doOnSubscribe(sub -> log.info("[3A] Subscribed to process Page"))
        .doOnNext(element -> log.info("[3A] Processing element: {}", element))
        .doOnComplete(() -> log.info("[3A] Completed successfully"))
        .doOnError(error -> log.error("[3A] Error processing page", error));
  }

  public Flux<String> question3B(String country) {
    return countryService
        .findCurrenciesByCountry(country)
        .doOnSubscribe(
            sub -> log.info("[3B] Subscribed to findCurrenciesByCountry for '{}'", country))
        .doOnNext(currency -> log.info("[3B] Processing currency: {}", currency))
        .distinct()
        .doOnComplete(() -> log.info("[3B] Completed successfully"))
        .doOnError(error -> log.error("[3B] Error processing currencies", error));
  }

  public Flux<String> question3C() {
    return countryService
        .findAllCountries()
        .doOnSubscribe(sub -> log.info("[3C] Subscribed to findAllCountries"))
        .take(3)
        .flatMap(country -> Mono.justOrEmpty(translatorService.translate(country)))
        .doOnNext(translation -> log.info("[3C] Processing translated country: {}", translation))
        .doOnComplete(() -> log.info("[3C] Completed successfully"))
        .doOnError(error -> log.error("[3C] Error processing translated countries", error));
  }
}

package cl.tenpo.learning.reactive.tasks.task1;

import cl.tenpo.learning.reactive.utils.service.CountryService;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class T1Question4 {

  private final CountryService countryService;

  public Flux<String> question4A() {
    Flux<String> cachedFlux = getCachedCountryFlux();
    logCountryRepetitions(cachedFlux);
    return getSortedUniqueCountries(cachedFlux);
  }

  private Flux<String> getCachedCountryFlux() {
    return countryService
        .findAllCountries()
        .doOnSubscribe(sub -> log.info("[4] Subscribed to findAllCountries"))
        .take(200)
        .cache();
  }

  private void logCountryRepetitions(Flux<String> countryFlux) {
    countryFlux
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .doOnNext(map -> log.info("[4] Country repetition map: {}", map))
        .subscribe();
  }

  private Flux<String> getSortedUniqueCountries(Flux<String> countryFlux) {
    return countryFlux
        .distinct()
        .sort()
        .doOnSubscribe(sub -> log.info("[4] Subscribed to emit distinct sorted countries"))
        .doOnNext(country -> log.info("[4] Emitting country: {}", country))
        .doOnComplete(() -> log.info("[4] Completed successfully"))
        .doOnError(error -> log.error("[4] Error processing countries", error));
  }
}

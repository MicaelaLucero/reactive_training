package cl.tenpo.learning.reactive.tasks.task2.config;

import static cl.tenpo.learning.reactive.tasks.task2.util.Topics.CR_RETRY_EXHAUSTED;

import cl.tenpo.learning.reactive.tasks.task2.dto.event.PercentageErrorEvent;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaConfig {

  @Bean
  public ReactiveKafkaConsumerTemplate<String, PercentageErrorEvent> percentageErrorConsumerTemplate(
      KafkaProperties kafkaProperties
  ) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PercentageErrorEvent.class.getName());
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    ReceiverOptions<String, PercentageErrorEvent> receiverOptions = ReceiverOptions.<String, PercentageErrorEvent>create(props)
                                                                                   .subscription(Collections.singletonList(CR_RETRY_EXHAUSTED));

    return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
  }

  @Bean
  public ReactiveKafkaProducerTemplate<String, PercentageErrorEvent> percentageErrorProducerTemplate(
      KafkaProperties kafkaProperties
  ) {
    Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    SenderOptions<String, PercentageErrorEvent> senderOptions = SenderOptions.create(props);

    return new ReactiveKafkaProducerTemplate<>(senderOptions);
  }
}
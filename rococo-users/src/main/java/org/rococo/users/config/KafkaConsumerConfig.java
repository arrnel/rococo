package org.rococo.users.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.rococo.users.model.UserDTO;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

//    @Bean
//    public ConsumerFactory<String, UserDTO> consumerFactory(SslBundles sslBundles) {
//        final JsonDeserializer<UserDTO> jsonDeserializer = new JsonDeserializer<>();
//        jsonDeserializer.addTrustedPackages("*");
//        return new DefaultKafkaConsumerFactory<>(
//                kafkaProperties.buildConsumerProperties(sslBundles),
//                new StringDeserializer(),
//                jsonDeserializer
//        );
//    }

    @Bean
    public ConsumerFactory<String, UserDTO> consumerFactory(SslBundles sslBundles) {
        final JsonDeserializer<UserDTO> jsonDeserializer = new JsonDeserializer<>(UserDTO.class);
        jsonDeserializer.setUseTypeHeaders(false); // Необязательно, но добавляет надёжности
        jsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(sslBundles),
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDTO> kafkaListenerContainerFactory(SslBundles sslBundles) {
        ConcurrentKafkaListenerContainerFactory<String, UserDTO> concurrentKafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory(sslBundles));
        return concurrentKafkaListenerContainerFactory;
    }

}

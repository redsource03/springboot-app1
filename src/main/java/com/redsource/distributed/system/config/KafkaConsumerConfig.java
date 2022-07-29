package com.redsource.distributed.system.config;

import com.redsource.distributed.system.prop.KafkaProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaProps kafkaProps;


    @Bean
    public ConsumerFactory<String, String> consumerFactory() throws FileNotFoundException, URISyntaxException {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProps.getBootstrapAddress());
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                kafkaProps.getConsumerId());
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, true);
        if (kafkaProps.isSslEnabled()) {
            URL pathTrust = getClass().getClassLoader().getResource(kafkaProps.getSsl().get("truststore-location"));
            URL pathKeystore = getClass().getClassLoader().getResource(kafkaProps.getSsl().get("keystore-location"));

            if (Objects.isNull(pathTrust)
                    ||
                    Objects.isNull(pathKeystore)) {
                throw new FileNotFoundException("Truststore or keystore not found");
            }
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.location", pathTrust.getPath());
            props.put("ssl.truststore.password", kafkaProps.getSsl().get("truststore-password"));

            props.put("ssl.key.password", kafkaProps.getSsl().get("key-password"));
            props.put("ssl.keystore.password", kafkaProps.getSsl().get("keystore-password"));
            props.put("ssl.keystore.location", pathKeystore.getPath());
            props.put("ssl.endpoint.identification.algorithm", "");
        }
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory()
            throws FileNotFoundException, URISyntaxException {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

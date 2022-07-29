package com.redsource.distributed.system.config;

import com.redsource.distributed.system.prop.KafkaProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProps kafkaProps;

    @Bean
    public ProducerFactory<String, String> producerFactory() throws URISyntaxException, FileNotFoundException {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProps.getBootstrapAddress());
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        if (kafkaProps.isSslEnabled()) {
            URL pathTrust = getClass().getClassLoader().getResource(kafkaProps.getSsl().get("truststore-location"));
            URL pathKeystore = getClass().getClassLoader().getResource(kafkaProps.getSsl().get("keystore-location"));

            if (Objects.isNull(pathTrust)
                    ||
                    Objects.isNull(pathKeystore)) {
                throw new FileNotFoundException("Truststore or keystore not found");
            }
            configProps.put("security.protocol", "SSL");
            configProps.put("ssl.truststore.location", pathTrust.getPath());
            configProps.put("ssl.truststore.password", kafkaProps.getSsl().get("truststore-password"));

            configProps.put("ssl.key.password", kafkaProps.getSsl().get("key-password"));
            configProps.put("ssl.keystore.password", kafkaProps.getSsl().get("keystore-password"));
            configProps.put("ssl.keystore.location", pathKeystore.getPath());
            configProps.put("ssl.endpoint.identification.algorithm", "");
        }
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() throws FileNotFoundException, URISyntaxException {
        return new KafkaTemplate<>(producerFactory());
    }
}

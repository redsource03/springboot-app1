package com.redsource.distributed.system.config;

import com.redsource.distributed.system.prop.KafkaProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {
    private final KafkaProps kafkaProps;

    @Bean
    public AdminClient adminClient() throws URISyntaxException, FileNotFoundException {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapAddress());
        props.put(AdminClientConfig.RETRIES_CONFIG, 5);
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

        return AdminClient.create(props);
    }

}

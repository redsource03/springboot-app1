package com.redsource.distributed.system.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProps {
    private String bootstrapAddress;
    private String consumerId;
    private List<Map<String, String>> topics;
    private boolean sslEnabled;
    private Map<String, String> ssl;
}

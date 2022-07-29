package com.redsource.distributed.system.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "mongo")
public class MongoDBProps {
    private List<String> clusters;
    private String databaseName;
    private String localUser;
    private String localPassword;

}

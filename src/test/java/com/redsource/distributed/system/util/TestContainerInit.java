package com.redsource.distributed.system.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;

import java.time.Duration;
import java.util.Optional;


@Slf4j
public class TestContainerInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final MongoDBWrapperContainer MONGO_DB_CONTAINER =
            new MongoDBWrapperContainer();
    private static final KafkaWrapperContainer KAFKA_WRAPPER_CONTAINER = new KafkaWrapperContainer();
    private static final String PROPERTY_FILE = "application.yml";
    private static boolean initialized = false;

    @SneakyThrows
    @Override
    public void initialize(
            final ConfigurableApplicationContext applicationContext) {
        log.info("Starting Test Containers");

        val yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ClassPathResource(PROPERTY_FILE));
        val properties =
                Optional.ofNullable(yamlFactory.getObject()).orElseThrow();
        if (!initialized) {
            MONGO_DB_CONTAINER.start();
            KAFKA_WRAPPER_CONTAINER.withStartupTimeout(Duration.ofMillis(180000));
            KAFKA_WRAPPER_CONTAINER.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Stopping test containers");
                MONGO_DB_CONTAINER.stop();
                KAFKA_WRAPPER_CONTAINER.stop();
            }));


            initialized = true;
        }

        MONGO_DB_CONTAINER.setupProperties(properties);
        KAFKA_WRAPPER_CONTAINER.setupProperties(properties);

        applicationContext.getEnvironment()
                .getPropertySources()
                .addFirst(new PropertiesPropertySource(PROPERTY_FILE,
                        properties));

    }
}

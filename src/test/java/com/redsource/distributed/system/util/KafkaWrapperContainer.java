package com.redsource.distributed.system.util;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

@Slf4j
public class KafkaWrapperContainer extends KafkaContainer {

    private static final String DOCKER_IMAGE_NAME = "confluentinc/cp-kafka:6.2.1";

    public KafkaWrapperContainer() {
        super(DockerImageName.parse(DOCKER_IMAGE_NAME));

    }

    @Override
    public void start() {
        log.info("Starting Kafka Container");
        super.start();
    }

    @Override
    public void stop() {
        log.info("Stopping Kafka Container");
        super.stop();
    }

    public void setupProperties(final Properties properties) {
        log.info("Overriding Kafka properties");
        properties.replace("kafka.bootstrap-address", getBootstrapServers());
    }
}

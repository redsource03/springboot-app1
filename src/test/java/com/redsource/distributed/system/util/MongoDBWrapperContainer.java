package com.redsource.distributed.system.util;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

import static java.lang.String.format;

@Slf4j
public class MongoDBWrapperContainer extends MongoDBContainer {
    private static final String DOCKER_IMAGE_NAME = "mongo:4.0.10";

    public MongoDBWrapperContainer() {
        super(DockerImageName.parse(DOCKER_IMAGE_NAME));
    }

    @Override
    public void start() {
        log.info("Starting MongoDB Test Container");
        super.start();
    }

    @Override
    public void stop() {
        log.info("Stopping MongoDB Test Container");
    }

    public void setupProperties(final Properties properties) {
        log.info("Overriding mongoDB properties");
        properties.replace("mongo.clusters", format("%s:%s", getContainerIpAddress(), super.getFirstMappedPort()));
    }

}

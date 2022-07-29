package com.redsource.distributed.system.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.redsource.distributed.system.consumers.KafkaConsumer;
import com.redsource.distributed.system.prop.KafkaProps;
import com.redsource.distributed.system.service.PublisherService;
import com.redsource.distributed.system.util.LogHelper;
import com.redsource.distributed.system.util.TestContainerInit;
import com.tyro.oss.logtesting.logback.LogbackCaptor;
import kotlin.jvm.JvmField;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = TestContainerInit.class)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class, MongoDataAutoConfiguration.class}
)
class ComponentIntegrationTest {

    @JvmField
    @RegisterExtension
    private final LogbackCaptor logPublisherService = new LogbackCaptor(PublisherService.class);

    @JvmField
    @RegisterExtension
    private final LogbackCaptor logKafkaConsumer = new LogbackCaptor(KafkaConsumer.class);

    @Autowired
    private KafkaProps kafkaProps;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private PublisherService publisherService;

    @Test
    @DisplayName("Application Should be able to save and retrieve to the MongoDB")
    void mongoDBShouldInitialize() {
        val dummyName = "Dummy Master";
        reactiveMongoTemplate.save(DummyModel.builder().id("123").name(dummyName).build()).block();

        StepVerifier.create(reactiveMongoTemplate.findAll(DummyModel.class)).assertNext(dummyModel -> {
            dummyModel.getName().equals(dummyName);
        }).expectComplete().verify();

    }

    @Test
    @DisplayName("Should be able to publish to kafka topic")
    void publishConsumeKafka() throws InterruptedException {

        Long waitTime = 1000L;

        Thread.sleep(waitTime);
        publisherService.sendMessage(kafkaProps.getTopics().get(0).get("name"), "Payload");
        Thread.sleep(waitTime);
        List<ILoggingEvent> logEvents = LogHelper.getlogs(Level.INFO, logPublisherService);
        assertThat(logEvents.size()).isEqualTo(4);
        assertThat(logEvents.get(1).getFormattedMessage()
                .startsWith("Successful sending message payload to topic")).isTrue();

        logEvents = LogHelper.getlogs(Level.INFO, logKafkaConsumer);
        assertThat(logEvents.size()).isEqualTo(4);
        assertThat(logEvents.get(0).getFormattedMessage().startsWith("I received a message from topic")).isTrue();
    }

    @Document(collection = "dummy")
    @Builder
    @Data
    public static class DummyModel {
        @Id
        private String id;
        private String name;
    }
}

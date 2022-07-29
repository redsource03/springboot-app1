package com.redsource.distributed.system.acceptance;

import com.redsource.distributed.system.model.Payload;
import com.redsource.distributed.system.service.PublisherService;
import com.redsource.distributed.system.util.TestContainerInit;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Ignore
@RunWith(SpringRunner.class)
@CucumberContextConfiguration
@ContextConfiguration(initializers = TestContainerInit.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class, MongoDataAutoConfiguration.class}
)
public class AcceptanceSteps {


    private static final String COLLECTION = "payload";

    @Spy
    @Autowired
    private PublisherService publisherService;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;


    @Given("^A Message is published to topic:(.*) with payload:(.*)$")
    public void publishMessageToTopic(final String topic, final String payload) {
        publisherService.sendMessage(topic, payload);
    }

    @Given("^Collection is empty$")
    public void cleanCollection() {
        reactiveMongoTemplate.dropCollection(COLLECTION).block();
        reactiveMongoTemplate.createCollection(COLLECTION).block();
    }

    @Given("^Wait-Time: (.*) ms is over$")
    public void sleep(final long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    @Then("^Payload should be saved in the collection times:(.*)$")
    public void getPayload(final String times) {
        assertThat(reactiveMongoTemplate.findAll(Payload.class).collectList().block().size())
                .isEqualTo(Integer.valueOf(times));
    }

    @Then("^Verify message is published to topic:(.*) with message:(.*)$")
    public void verifyPublishedMessage(final String topic, final String message) {
        verify(publisherService, times(1)).sendMessage(topic, message);
    }
}

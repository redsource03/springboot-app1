package com.redsource.distributed.system.consumers;

import com.redsource.distributed.system.model.Payload;
import com.redsource.distributed.system.service.PublisherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    private static final String TOPIC_1 = "spring-app1-topic";
    private static final String TOPIC_2 = "spring-app2-topic";
    private static final String PAYLOAD = "payload";
    @Mock
    private PublisherService publisherService;
    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    @DisplayName("Should be able process payload without errors: Topic1")
    void processingSuccessfulTopic1() {
        when(reactiveMongoTemplate.save(any(Payload.class))).thenReturn(
                Mono.just(Payload.builder().id("123").payload(PAYLOAD).build())
        );
        doNothing().when(publisherService).sendMessage(anyString(), anyString());
        kafkaConsumer.topic1Consumer(PAYLOAD);
        verify(reactiveMongoTemplate, times(1)).save(any(Payload.class));
        verify(publisherService, times(1)).sendMessage(TOPIC_2, PAYLOAD);
        verifyNoMoreInteractions(reactiveMongoTemplate);
        verifyNoMoreInteractions(publisherService);

    }

    @Test
    @DisplayName("Error on saving to MongoDb: Topic1")
    void processingErrorOnSaveTopic1() {
        when(reactiveMongoTemplate.save(any(Payload.class))).thenReturn(
                Mono.error(new Throwable("oopss"))
        );
        kafkaConsumer.topic1Consumer(PAYLOAD);
        verify(reactiveMongoTemplate, times(1)).save(any(Payload.class));
        verifyNoInteractions(publisherService);
    }

    @Test
    @DisplayName("Should be able process payload without errors: Topic2")
    void processingSuccessfulTopic2() {
        when(reactiveMongoTemplate.save(any(Payload.class))).thenReturn(
                Mono.just(Payload.builder().id("123").payload(PAYLOAD).build())
        );
        kafkaConsumer.topic2Consumer(PAYLOAD);
        verify(reactiveMongoTemplate, times(1)).save(any(Payload.class));
        verifyNoMoreInteractions(reactiveMongoTemplate);
        verifyNoInteractions(publisherService);

    }

    @Test
    @DisplayName("Error on saving to MongoDb: Topic2")
    void processingErrorOnSaveTopic2() {
        when(reactiveMongoTemplate.save(any(Payload.class))).thenReturn(
                Mono.error(new Throwable("oopss"))
        );
        kafkaConsumer.topic2Consumer(PAYLOAD);
        verify(reactiveMongoTemplate, times(1)).save(any(Payload.class));
        verifyNoInteractions(publisherService);
    }

}

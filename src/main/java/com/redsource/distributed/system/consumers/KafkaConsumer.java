package com.redsource.distributed.system.consumers;

import com.redsource.distributed.system.model.Payload;
import com.redsource.distributed.system.service.PublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class KafkaConsumer {

    private static final String TOPIC_1 = "spring-app1-topic";
    private static final String TOPIC_2 = "spring-app2-topic";

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private final PublisherService publisherService;

    @KafkaListener(topics = TOPIC_2)
    public void topic2Consumer(final String payload) {
        log.info(format("I received a message from topic:%s value:%s, I'm just going to save it and be on my merry way",
                TOPIC_2, payload));
        reactiveMongoTemplate.save(Payload.builder().payload(payload).build())
                .doOnSuccess(payload1 -> log.info(format("payload saved with id:%s", payload1.getId())))
                .onErrorResume(e -> {
                    log.error(format("Error in saving payload received from topic:%s", TOPIC_2));
                    return Mono.error(e);
                }).subscribe();

    }

    @KafkaListener(topics = TOPIC_1)
    public void topic1Consumer(final String payload) {
        log.info(format("I received a message from topic:%s, I'm just going to save it "
                + "and send it to topic:%s , value: %s", TOPIC_1, TOPIC_2, payload));

        reactiveMongoTemplate.save(Payload.builder().payload(payload).build())
                .flatMap(payload1 ->
                        Mono.fromCallable(() -> {
                                    publisherService.sendMessage(TOPIC_2, payload1.getPayload());
                                    return payload1;
                                }
                        )
                )
                .doOnSuccess(payload1 -> log.info(format("payload saved with id:%s", payload1.getId())))
                .onErrorResume(e -> {
                    log.error(format("Error in processing payload received from topic:%s", TOPIC_2));
                    return Mono.error(e);
                }).subscribe();


    }


}

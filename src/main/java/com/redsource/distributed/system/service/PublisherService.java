package com.redsource.distributed.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublisherService {

    private final KafkaTemplate kafkaTemplate;

    public void sendMessage(final String topic, final String message) {
        log.info(format("Sending message payload to topic:%s", topic));

        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic, message);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(final Throwable ex) {
                log.error(format("Failure on sending message payload to topic:%s", topic));
                throw new RuntimeException(ex);
            }

            @Override
            public void onSuccess(final SendResult<String, String> result) {
                log.info(format("Successful sending message payload to topic:%s", topic));
            }
        });

    }
}

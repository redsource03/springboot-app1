package com.redsource.distributed.system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @InjectMocks
    private PublisherService publisherService;

    @Mock
    private KafkaTemplate kafkaTemplate;

    @Test
    @DisplayName("Test publish successful")
    void publishSuccessful() {

        SendResult<String, Object> sendResult = mock(SendResult.class);
        ListenableFuture<SendResult<String, Object>> responseFuture = mock(ListenableFuture.class);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(responseFuture);

        doAnswer(invocationOnMock -> {
            ListenableFutureCallback listenableFutureCallback = invocationOnMock.getArgument(0);
            listenableFutureCallback.onSuccess(sendResult);
            return null;
        }).when(responseFuture).addCallback(any(ListenableFutureCallback.class));

        publisherService.sendMessage("any", "payload");

        verify(kafkaTemplate, times(1)).send("any", "payload");
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    @DisplayName("should process onFailure")
    void onFailure() {

        ListenableFuture<SendResult<String, Object>> responseFuture = mock(ListenableFuture.class);
        Throwable throwable = mock(Throwable.class);

        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(responseFuture);
        doAnswer(invocationOnMock -> {
            ListenableFutureCallback listenableFutureCallback = invocationOnMock.getArgument(0);
            listenableFutureCallback.onFailure(throwable);
            return null;
        }).when(responseFuture).addCallback(any(ListenableFutureCallback.class));
        assertThrows(RuntimeException.class, () -> publisherService.sendMessage("any", "payload"));

        verify(kafkaTemplate, times(1)).send("any", "payload");
        verifyNoMoreInteractions(kafkaTemplate);

    }

}
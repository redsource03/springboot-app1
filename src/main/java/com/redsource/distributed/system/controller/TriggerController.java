package com.redsource.distributed.system.controller;

import com.redsource.distributed.system.service.PublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TriggerController {
    private static final String TOPIC_1 = "spring-app1-topic";

    private final PublisherService publisherService;

    @GetMapping(path = "/trigger/{message}")
    public ResponseEntity<String> trigger(@PathVariable final String message) {
        publisherService.sendMessage(TOPIC_1, message);
        return ResponseEntity.ok("OK");
    }
}

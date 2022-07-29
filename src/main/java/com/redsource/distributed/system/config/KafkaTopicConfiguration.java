package com.redsource.distributed.system.config;

import com.redsource.distributed.system.prop.KafkaProps;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class KafkaTopicConfiguration {

    private static final String TOPIC_NAME = "name";
    private static final String TOPIC_PARTITIONS = "partitions";
    private static final String TOPIC_REPLICATIONS = "replications";

    private final AdminClient adminClient;
    private final KafkaProps kafkaProps;

    @PostConstruct
    void initTopics() {
        List<NewTopic> newTopics = kafkaProps.getTopics().stream().map(topic -> new NewTopic(topic.get(TOPIC_NAME),
                Integer.valueOf(topic.get(TOPIC_PARTITIONS)),
                Short.valueOf(topic.get(TOPIC_REPLICATIONS)))).collect(Collectors.toList());
        adminClient.createTopics(newTopics);
    }
}

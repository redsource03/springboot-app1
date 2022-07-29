package com.redsource.distributed.system.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "springboot-app1")
@Data
@Builder
public class Payload {

    @Id
    private String id;
    private String payload;
}

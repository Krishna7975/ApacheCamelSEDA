package com.tetrus.ilink.cl.aggregator.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomMergeAggregationStrategy implements AggregationStrategy {

    private final ObjectMapper mapper;

    public CustomMergeAggregationStrategy() {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @SneakyThrows
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Map<String, Object> newEntry = newExchange.getMessage().getBody(Map.class);
        if (newEntry == null) {
            return oldExchange != null ? oldExchange : newExchange;
        }

        Map<String, Object> oldEntry = oldExchange != null
                ? oldExchange.getMessage().getBody(Map.class)
                : new HashMap<>();

        Map<String, Object> merged = new HashMap<>(oldEntry);
        for (Map.Entry<String, Object> entry : newEntry.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null && !"".equals(value)) {
                merged.put(key, value);
            } else if (!merged.containsKey(key)) {
                merged.put(key, "NA");
            }
        }
        Message message = oldExchange != null ? oldExchange.getMessage() : newExchange.getMessage();
        message.setBody(merged);
        return oldExchange != null ? oldExchange : newExchange;

    }
}


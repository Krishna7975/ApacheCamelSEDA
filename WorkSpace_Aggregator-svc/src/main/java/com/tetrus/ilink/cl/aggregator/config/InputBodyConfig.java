package com.tetrus.ilink.cl.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "inputbodies")
public class InputBodyConfig {
    private Map<String, String> source = new HashMap<>();

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    public String getBodyForSource(String sourceName) {
        return source.getOrDefault(sourceName, "{}");
    }
}

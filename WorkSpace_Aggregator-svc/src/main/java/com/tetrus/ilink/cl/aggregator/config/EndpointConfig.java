package com.tetrus.ilink.cl.aggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "config")
public class EndpointConfig {
    private List<Endpoint> endpoints;


    public List<Endpoint> getEndpoints() {
        return endpoints;
    }
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public EndpointConfig(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Data
    public static class Endpoint {
        private String name;
        private String url;
        private Map<String, Object> response;

        @Override
        public String toString() {
            return
                    "Endpoint" +'\'' +
                    "{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", response=" + response +
                    "}" + System.lineSeparator();
        }




        public String getName() {
            return name;
        }

        public Map<String, Object> getResponse() {
            return response;
        }

        public void setResponse(Map<String, Object> response) {
            this.response = response;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Endpoint(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}

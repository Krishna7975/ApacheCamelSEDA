package com.tetrus.ilink.cl.aggregator.router;

import com.tetrus.ilink.cl.aggregator.config.EndpointConfig;
import com.tetrus.ilink.cl.aggregator.config.InputBodyConfig;
import com.tetrus.ilink.cl.aggregator.model.Request;
import com.tetrus.ilink.cl.aggregator.service.CustomMergeAggregationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CamelRouteBuilder extends RouteBuilder {

    @Autowired
    private final EndpointConfig endpointConfig;

    private final InputBodyConfig inputBodyConfig;

    public CamelRouteBuilder(EndpointConfig endpointConfig, InputBodyConfig inputBodyConfig, CustomMergeAggregationStrategy customMergeAggregationStrategy) {
        this.endpointConfig = endpointConfig;
        this.inputBodyConfig = inputBodyConfig;
        this.customMergeAggregationStrategy = customMergeAggregationStrategy;
    }

    @Autowired
    private final CustomMergeAggregationStrategy customMergeAggregationStrategy;

    @Override
    public void configure() throws Exception {

        from("direct:aggregateRoute")
                .routeId("rest-triggered-aggregation")
                .log("CAMEL AGGREGATE ROUTE TRIGGERED")
                .process(exchange -> {
                    Request input = exchange.getIn().getBody(Request.class);
                    exchange.setProperty("inputBodyMap", input);
                    List<EndpointConfig.Endpoint> endpoints = endpointConfig.getEndpoints();
                    log.info("Loaded Endpoints from YAML: {}", endpoints);
                    exchange.getIn().setBody(endpoints);
                })
                .split().method("endpointConfig", "getEndpoints")
                .parallelProcessing()
                .setProperty("endpoint", simple("${body}"))
                .to("seda:fetchEachEndpoint?timeout=60000&waitForTaskToComplete=Always")
                .end()
                .aggregate(constant(true), new CustomMergeAggregationStrategy())
                .completionSize(endpointConfig.getEndpoints().size())
                .completionTimeout(30000)
                .process(exchange -> {
                    List<EndpointConfig.Endpoint> endpointsWithResponses = endpointConfig.getEndpoints();
                    Map<String, Object> finalResponse = new HashMap<>();
                    finalResponse.put("endpoints", endpointsWithResponses);
                    exchange.getIn().setBody(finalResponse);
                })
                .to("log:finalAggregatedResponse?showAll=true&multiline=true");


        from("seda:fetchEachEndpoint?concurrentConsumers=10")
                .routeId("seda-endpoint-fetch")
                .doTry()
                .process(exchange -> {
                    EndpointConfig.Endpoint endpoint = exchange.getProperty("endpoint", EndpointConfig.Endpoint.class);
                    String sourceName = endpoint.getName();
                    exchange.setProperty("sourceName", sourceName);
                    exchange.setProperty("endpoint.url", endpoint.getUrl());
                    Request fullRequest = exchange.getProperty("inputBodyMap", Request.class);
                    List<Map<String, Object>> selectedBody;
                    switch (sourceName) {
                        case "s1": selectedBody = fullRequest.getS1(); break;
                        case "s2": selectedBody = fullRequest.getS2(); break;
                        case "s3": selectedBody = fullRequest.getS3(); break;
                        default: selectedBody = Collections.emptyList();
                    }
                    Map<String, Object> wrappedBody = new HashMap<>();
                    wrappedBody.put("sets", selectedBody);
                    String jsonBody = new ObjectMapper().writeValueAsString(wrappedBody);
                    exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    exchange.getIn().setBody(jsonBody);

                    log.info("Sending request to {} with payload: {}", exchange.getProperty("endpoint.url"), jsonBody);
                })
                .toD("${exchangeProperty.endpoint.url}?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    String sourceName = exchange.getProperty("sourceName", String.class);
                    String body = exchange.getIn().getBody(String.class);
                    log.info("Raw response from {}: {}", sourceName, body);

                    Map<String, Object> responseMap = new ObjectMapper().readValue(body, Map.class);
                    Object sourceData = responseMap.getOrDefault(sourceName, responseMap);
                    EndpointConfig.Endpoint endpoint = exchange.getProperty("endpoint", EndpointConfig.Endpoint.class);
                    endpoint.setResponse((Map<String, Object>) sourceData);
                    Map<String, Object> wrapped = new HashMap<>();
                    wrapped.put(sourceName, sourceData);
                    exchange.getIn().setBody(wrapped);
                })
                .doCatch(Exception.class)
                .process(exchange -> {
                    String sourceName = exchange.getProperty("sourceName", String.class);
                    Map<String, Object> fallback = new HashMap<>();
                    fallback.put(sourceName, Collections.emptyMap());

                    EndpointConfig.Endpoint endpoint = exchange.getProperty("endpoint", EndpointConfig.Endpoint.class);
                    endpoint.setResponse(Collections.emptyMap());
                    log.error("Error fetching response for {}: {}", sourceName, exchange.getException());

                    exchange.getIn().setBody(fallback);
                })
                .end();
    }
}









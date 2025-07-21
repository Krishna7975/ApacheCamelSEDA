package com.tetrus.ilink.cl.aggregator.controller;
import com.tetrus.ilink.cl.aggregator.model.Request;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AggregationController
{
    @Autowired
    private final ProducerTemplate producerTemplate;

    public AggregationController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/trigger")
    public ResponseEntity<Object> triggerCamelRoute(@RequestBody Request request) {
        Object result = producerTemplate.requestBody("direct:aggregateRoute", request);
        return ResponseEntity.ok(result);
    }
    }




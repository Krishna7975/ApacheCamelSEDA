package com.tetrus.ilink.cl.aggregator;


import com.tetrus.ilink.cl.aggregator.config.EndpointConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EndpointConfig.class)
public class ApacheCamelIntergartionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApacheCamelIntergartionApplication.class, args);
	}

}

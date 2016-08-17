package org.yawlfoundation.cluster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.yawlfoundation.cluster.backend.properties.zkProperties;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(value = zkProperties.class)
public class BackendServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendServerApplication.class, args);
	}
}

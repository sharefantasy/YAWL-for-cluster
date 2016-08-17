package org.yawlfoundation.cluster.naming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by fantasy on 2016/8/6.
 */
@SpringBootApplication
@EnableAutoConfiguration
public class NamingServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(NamingServerApplication.class, args);
	}
}

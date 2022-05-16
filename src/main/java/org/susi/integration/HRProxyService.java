package org.susi.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient; - we dnt want this atm
import org.springframework.integration.http.config.EnableIntegrationGraphController;

//@EnableDiscoveryClient - we dnt want this atm
@SpringBootApplication
@EnableIntegrationGraphController(allowedOrigins = "*")
//i have some classes in our.susi.integregin, pls look that too
public class HRProxyService {

	
	public static void main(String[] args) {
		SpringApplication.run(HRProxyService.class, args);
	}
}


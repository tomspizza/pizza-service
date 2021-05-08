package org.susi.integration.two.controllers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.ResponseErrorHandler;
import org.susi.integration.CentralConfig;

@Configuration
@EnableIntegration
public class CPizzaMakerHttpSender {
	
	Logger log = Logger.getLogger(CPizzaMakerHttpSender.class);

	@Autowired 
	CentralConfig ccentralConf;
	
    @Bean("pizzaMakerRestChannel")
    public MessageChannel pizzaMakerRestChannel() {
       DirectChannel dc = new DirectChannel();
       return dc;
    }
    
    @Bean("pizzaMakerRestResponseChannel")
    public MessageChannel pizzaMakerRestResponseChannel() {
       DirectChannel dc = new DirectChannel();
       return dc;
    }
	
	@ServiceActivator(inputChannel = "pizzaMakerRestChannel")
	@Bean
	public HttpRequestExecutingMessageHandler outbound() {
	    HttpRequestExecutingMessageHandler handler = new HttpRequestExecutingMessageHandler(ccentralConf.getOutputEndpoint());

	    handler.setHttpMethod(HttpMethod.POST);
	    handler.setExpectedResponseType(String.class);
	    handler.setExpectReply(true);
	    handler.setOutputChannel(pizzaMakerRestResponseChannel());
	    handler.setErrorHandler(new ResponseErrorHandler() {
			
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				boolean error = (response.getStatusCode() != HttpStatus.OK);
				System.out.println("5555 check if error method " + error);
				return error;
			}
			
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				//System.out.println("Executing handleError method");
				System.out.println("5555" + response.toString());
			}
		});
	    return handler;
	}

}

package org.susi.integration.two.controllers;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.susi.integration.CentralConfig;
import org.susi.integration.LoggerConfig;

@Service
@EnableIntegration
public class BPizzaBakeOrderJmsToHttpHandler {
	
	@Autowired 
	CentralConfig centralConf;
	
	@Autowired
	@Qualifier("azureLogChannel")
	MessageChannel azureLogChannel;
	
	@Autowired
	@Qualifier("pizzaOutputJmsReceiverChannel")
	MessageChannel pizzaOutputJmsReceiverChannel;
	
	@Autowired
	@Qualifier("pizzaMakerRestChannel")
	MessageChannel pizzaMakerRestChannel;
	
	@Autowired
	@Qualifier("pizzaMakerRestResponseChannel") 
	MessageChannel pizzaMakerRestResponseChannel;
    

    @Bean
    @ServiceActivator(inputChannel = "pizzaOutputJmsReceiverChannel")
    public MessageHandler pizzaBakeRequestMessageHandler() {
        
    	return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("33333.111 receiveFromJms AndSend to BakeHTTP payload=" + message.getPayload());
				GenericMessage<String> gmOut = getOutputMessage(message);
				azureLogChannel.send(gmOut);
				pizzaMakerRestChannel.send(gmOut);
			}
			
        };
    }
    
    
    @Bean
    @ServiceActivator(inputChannel = "pizzaMakerRestResponseChannel")
    public MessageHandler pizzaMakerRestResponseHandler() {
        
    	return new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("5555 pizzaBake response receioved" + message.getPayload());
				GenericMessage<String> gmOutResponse = null; 
				long statusCode = getResponseCode(message);
				if (statusCode != 200) {
					gmOutResponse = getErrorMessage(message);
				} else  {
					gmOutResponse = getOutputResponseMessage(message);
				}
				
				azureLogChannel.send(gmOutResponse);
			}
    	};
    }
    

	private GenericMessage<String> getOutputMessage(Message<?> message) {
		Map<String, Object> headersOut = new HashMap<String, Object>();
		headersOut.putAll(message.getHeaders());
		headersOut.put(LoggerConfig.ENDPOINT_TYPE, "HTTPS");
		headersOut.put(LoggerConfig.LOG_DIRECTION, "T");
		headersOut.put(LoggerConfig.ENDPOINT_META, getOutputChannelMetadata());		
		GenericMessage<String> gm = new GenericMessage<String>((String)message.getPayload(), headersOut);
		return gm;
	}
	
	private GenericMessage<String> getOutputResponseMessage(Message<?> message) {
		Map<String, Object> headersOut = new HashMap<String, Object>();
		headersOut.putAll(message.getHeaders());
		headersOut.put(LoggerConfig.LOG_DIRECTION, "TR");
		GenericMessage<String> gm = new GenericMessage<String>((String)message.getPayload(), headersOut);
		return gm;
	}
	
	private GenericMessage<String> getErrorMessage(Message<?> message) {
		Map<String, Object> headersIn = new HashMap<String, Object>();
		headersIn.putAll(message.getHeaders());
		headersIn.put(LoggerConfig.LOG_TYPE, LoggerConfig.LOG_TYPE_ERROR);
		headersIn.put(LoggerConfig.LOG_DIRECTION, "TR");
		GenericMessage<String> gm = new GenericMessage<String>((String)message.getPayload(), headersIn);
		return gm;
	}

	
	private String getOutputChannelMetadata() {
		String meta = LoggerConfig.HTTP_URL + "=" + centralConf.getOutputEndpoint()+ ",";
		meta += LoggerConfig.JMS_DESTINATION + "=" + centralConf.getOutputJmsDestination() + ",";
	    meta += LoggerConfig.JMS_HOSTPORT + "=" + centralConf.getMbHost() + ":" + centralConf.getMbPort() + ",";
		meta += LoggerConfig.JMS_USER + "=" + centralConf.getMbUserName(); 
		return meta;
	}
	
	
	//@SuppressWarnings({ })
	private long getResponseCode(Message<?> message) {
		Object httpStatus =  message.getHeaders().get("http_statusCode");
		if (httpStatus == null) {
			return 500;
		} else  {
			return ((HttpStatus)httpStatus).value();
		}
	}

}

package org.susi.integration.mediate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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
public class BPizzaOrderProcessorJmsToJmsHandler {
	
	@Autowired 
	CentralConfig centralConfig;
	
	@Autowired
	@Qualifier("azureLogChannel")
	MessageChannel azureLogChannel;
	
	@Autowired
	@Qualifier("jmsOrderProcessorWriterChannel")
	MessageChannel jmsOrderProcessorWriterChannel;
	
	@Autowired
	@Qualifier("pizzaInputJmsReceiverChannel")
	MessageChannel pizzaInputJmsReceiverChannel;
    

    @Bean
    @ServiceActivator(inputChannel = "pizzaInputJmsReceiverChannel")
    public MessageHandler pizzaOrderProcesorMessageHandler() {
        
    	return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("22222.111 Pizza order received at order processor Jms interface payload=" + message.getPayload());
				
				List<String> orderArray = OrderConverter.convert((String)message.getPayload());
				if (orderArray != null) {
					for (String order : orderArray) {
						GenericMessage<String> gmOut = getOutputMessage(message, order);
						System.out.println("22222.111 Pizza order sending to next JMS=" + gmOut.getPayload());
						azureLogChannel.send(gmOut);
						jmsOrderProcessorWriterChannel.send(gmOut);
					}
				} else {
					System.out.println("22222.111 Pizza order error=" + message.getPayload());
					GenericMessage<String> gmErr = getErrorMessage(message, "Invalid Order Request");
					azureLogChannel.send(gmErr);
				}
			}
        };
    } 
    

	private GenericMessage<String> getOutputMessage(Message<?> message, String orderStr) {
		Map<String, Object> headersOut = new HashMap<String, Object>();
		headersOut.putAll(message.getHeaders());
		headersOut.put(LoggerConfig.ENDPOINT_TYPE, "JMS");
		headersOut.put(LoggerConfig.LOG_TYPE, LoggerConfig.LOG_TYPE_INFO);
		headersOut.put(LoggerConfig.LOG_DIRECTION, "T1");
		headersOut.put(LoggerConfig.ENDPOINT_META, getOutputChannelMetadata());		
		GenericMessage<String> gm = new GenericMessage<String>(orderStr, headersOut);
		return gm;
	}
	
	private GenericMessage<String> getErrorMessage(Message<?> message, String orderStr) {
		Map<String, Object> headersOut = new HashMap<String, Object>();
		headersOut.putAll(message.getHeaders());
		headersOut.put(LoggerConfig.ENDPOINT_TYPE, "JMS");
		headersOut.put(LoggerConfig.LOG_TYPE, LoggerConfig.LOG_TYPE_ERROR);
		headersOut.put(LoggerConfig.LOG_DIRECTION, LoggerConfig.LOG_DIRECTION_T);
		headersOut.put(LoggerConfig.ENDPOINT_META, getOutputChannelMetadata());		
		GenericMessage<String> gm = new GenericMessage<String>(orderStr, headersOut);
		return gm;
	}
	
	private String getOutputChannelMetadata() {
		String meta = LoggerConfig.JMS_DESTINATION + "=" + centralConfig.getOutputJmsDestination() + ",";
	    meta += LoggerConfig.JMS_HOSTPORT + "=" + centralConfig.getMbHost() + ":" + centralConfig.getMbPort() + ",";
		meta += LoggerConfig.JMS_USER + "=" + centralConfig.getMbUserName(); 
		return meta;
	}

}

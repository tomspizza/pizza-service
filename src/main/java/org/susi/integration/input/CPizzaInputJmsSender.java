package org.susi.integration.input;
 

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.susi.integration.CentralConfig;

@Configuration
@EnableIntegration
public class CPizzaInputJmsSender {

	Logger log = Logger.getLogger(CPizzaInputJmsSender.class);
 
	@Autowired
	CentralConfig centralConfig;
	
	@Autowired
	@Qualifier("solaceTopicSenderTemplate")
	JmsTemplate solaceTopicSenderTemplate;
	
    @Bean("pizzaInputJmsWriterChannel")
    public MessageChannel pizzaInputJmsWriterChannel() {
       DirectChannel dc = new DirectChannel();
       return dc;
    }
    
    @Bean
	@ServiceActivator(inputChannel = "pizzaInputJmsWriterChannel")
	public MessageHandler pizzaInputJmsMessageHandler() {
	  JmsSendingMessageHandler handler = new JmsSendingMessageHandler(solaceTopicSenderTemplate);
	  handler.setDestinationName(centralConfig.getInputJmsDestination());
	  return handler;
	}

}
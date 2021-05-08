package org.susi.integration.mediate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.GenericMessage;
import org.susi.integration.CentralConfig;
import org.susi.integration.LoggerConfig;


@Configuration
@EnableIntegration
public class APizzaInputJmsReceiver {
	
    @Bean("pizzaInputJmsReceiverChannel")
    public MessageChannel pizzaInputJmsReceiverChannel() {
       DirectChannel dc = new DirectChannel();
       return dc;
    }

	@JmsListener(destination = "${input.jms_destination}", containerFactory = "topicConsumerFactory")
	public void processMsg(Message<?> msg) {
		System.out.println("22222 recevied from JMS quque " + msg.getPayload());
		pizzaInputJmsReceiverChannel().send(msg);
	}
	
	
	
	@Autowired
	@Qualifier("solaceTopicSenderTemplate")
	JmsTemplate solaceTopicSenderTemplate;
	
	@Autowired
	CentralConfig centralConfig;
    
   @PostConstruct
	public void pizzaInputJmsMessageHandler1() {
	   Map<String, Object> headers = new HashMap<String, Object>();
       headers.put(LoggerConfig.PEID, UUID.randomUUID().toString());
	  GenericMessage<String> gm =  new GenericMessage<String>("hellow world", headers);
	  //solaceTopicSenderTemplate.convertAndSend(centralConfig.getInputJmsDestination(), gm);
	}
}
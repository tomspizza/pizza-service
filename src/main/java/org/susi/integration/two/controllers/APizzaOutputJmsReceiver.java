package org.susi.integration.two.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


@Configuration
@EnableIntegration
public class APizzaOutputJmsReceiver {
	
    @Bean("pizzaOutputJmsReceiverChannel")
    public MessageChannel pizzaOutputJmsReceiverChannel() {
       DirectChannel dc = new DirectChannel();
       return dc;
    }

	@JmsListener(destination = "${output.jms_destination}", containerFactory = "topicConsumerFactory")
	public void processMsg(Message<?> msg) {
		System.out.println("333333 recevied from JMS quque " + msg.getPayload());
		pizzaOutputJmsReceiverChannel().send(msg);
	}
}
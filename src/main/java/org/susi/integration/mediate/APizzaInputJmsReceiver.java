package org.susi.integration.mediate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


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
}
package org.susi.integration.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.susi.integration.CentralConfig;
import org.susi.integration.LoggerConfig;
import org.susi.integration.mediate.OrderConverter;



@RestController
@RequestMapping("/order")
@EnableIntegration
public class OrderControler {
	
	@Autowired 
	CentralConfig config;
	
	@Autowired
    MessageChannel inputWriterChannel;
	
	@Autowired
    MessageChannel outputWriterChannel;
	
	@Autowired
	MessageChannel inputReceiverChannel;

	@Autowired
	MessageChannel outputReceiverChannel;
	
	
	@PostMapping(value = "/add")
	public String replay(@RequestBody String inputMsg) {
		inputWriterChannel.send(Codeobe.getMessageWithHeaders(inputMsg, LogType.INFO, LogDirection.SOURCE));
		return "Message recived";
	}
	

    @Bean
    @ServiceActivator(inputChannel = "inputReceiverChannel")
    public MessageHandler inputMessageHandler() {
        
    	return new MessageHandler() {
    		@Override
			public void handleMessage(Message<?> message) throws MessagingException {
    			String convMsg = new OrderConverter().convert(message.getPayload());
    			outputWriterChannel.send(Codeobe.getMessageWithHeaders(convMsg, LogType.INFO, LogDirection.TARGET));
    		}
    	};
    }
    
    
    @Bean
    @ServiceActivator(inputChannel = "outputReceiverChannel")
    public MessageHandler outputMessageHandler() {
        
    	return new MessageHandler() {
    		@Override
			public void handleMessage(Message<?> message) throws MessagingException {
    			HttpUtil.send(message.getPayload(), config.getOutputEndpoint());
    		}
    	};
    }

}

package org.susi.integration.input;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.susi.integration.CentralConfig;
import org.susi.integration.LoggerConfig;


@RestController
@RequestMapping("/orderpizza")
@EnableIntegration
public class APizzaRestReceiverController {
	
	@Autowired 
	CentralConfig centralConfig;
	
	@Autowired
	@Qualifier("azureLogChannel")
	MessageChannel azureLogChannel;
	
	@Autowired
	@Qualifier("pizzaInputJmsWriterChannel")
    MessageChannel pizzaInputJmsWriterChannel;
	

	@PostMapping(value = "/add")
	public String replay(@RequestBody String request) {
		System.out.println("1111 Pizza order received @REST interface order= " + request);
		GenericMessage<String> gmIn = getInputMessage(request);
		azureLogChannel.send(gmIn);
		pizzaInputJmsWriterChannel.send(gmIn);
		return request;
	}

    
	private GenericMessage<String> getInputMessage(String request) {
		
		Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(LoggerConfig.PEID, UUID.randomUUID().toString());
        headers.put(LoggerConfig.LOG_TYPE, LoggerConfig.LOG_TYPE_INFO);
        headers.put(LoggerConfig.LOG_DIRECTION, LoggerConfig.LOG_DIRECTION_S);
	    headers.put(LoggerConfig.EXECUTION_MODE, LoggerConfig.EXECUTION_MODE_LISTENER);
	    headers.put(LoggerConfig.EXECUTION_USER, LoggerConfig.EXECUTION_USER_SYSTEM);
	    headers.put(LoggerConfig.ENDPOINT_TYPE, LoggerConfig.ENDPOINT_TYPE_JMS);
	    headers.put(LoggerConfig.ENDPOINT_META, getInputtJMSMetadata());
	    headers.put(LoggerConfig.SOURCE_APP, centralConfig.getLogSrcApp());
	    headers.put(LoggerConfig.TARGET_APP, centralConfig.getLogTargetApp());
	    headers.put(LoggerConfig.PROCESS_NAME, centralConfig.getSpringAppLicationName());
	    headers.put(LoggerConfig.PROCESS_GROUP, centralConfig.getProcessGroup());
	    GenericMessage<String> gm = new GenericMessage<String>(request, headers);
		return gm;
	}

	
	private String getInputtJMSMetadata() {
		String meta = LoggerConfig.JMS_DESTINATION + "=" + centralConfig.getInputJmsDestination() + ",";
	    meta += LoggerConfig.JMS_HOSTPORT + "=" + centralConfig.getMbHost() + ":" + centralConfig.getMbPort() + ",";
		meta += LoggerConfig.JMS_USER + "=" + centralConfig.getMbUserName(); 
		return meta;
	}
 
}

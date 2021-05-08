package org.susi.integration;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableIntegration
public class JmsReceiverConfig {
	
	@Autowired 
	CentralConfig centralConfig;

	
	private JndiTemplate jndiTemplate() {
	  JndiTemplate jt = new JndiTemplate();
	  Properties p = new Properties();
	  p.setProperty(Context.INITIAL_CONTEXT_FACTORY, centralConfig.getMbInitialContextFactory()); 
	  p.setProperty(Context.PROVIDER_URL, centralConfig.getMbHost() + ":" + centralConfig.getMbPort() ); 
	  p.setProperty(Context.SECURITY_PRINCIPAL,  centralConfig.getMbUserName() + "@" +  centralConfig.getMbMsgVPN());  
	  p.setProperty(Context.SECURITY_CREDENTIALS, centralConfig.getMbPassword());  
//	  p.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.solacesystems.jndi.SolJNDIInitialContextFactory"); 
//	  p.setProperty(Context.PROVIDER_URL, "tcps://mr1gh5vh0idpun.messaging.solace.cloud" + ":" + "55443" ); 
//	  p.setProperty(Context.SECURITY_PRINCIPAL,  "solace-cloud-client" + "@" +  "susisolace");  
//	  p.setProperty(Context.SECURITY_CREDENTIALS, "nsr6cojucdr36s9l1i4u1lsh8");  
	  jt.setEnvironment(p);
	  return jt;
	}


 	private JndiObjectFactoryBean consumerConnectionFactory() {
 		JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiTemplate(jndiTemplate());
        factoryBean.setJndiName(centralConfig.getMbJndiName());
        //following ensures all the properties are injected before returning
        try {
			factoryBean.afterPropertiesSet();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
        return factoryBean;
    }
    
    // Configure the destination resolver for the consumer:
    // Here we are using JndiDestinationResolver for JNDI destinations
    // Other options include using DynamicDestinationResolver for non-JNDI destinations
    private JndiDestinationResolver consumerJndiDestinationResolver() {
    	JndiDestinationResolver jdr = new JndiDestinationResolver();
        jdr.setCache(true);
        jdr.setJndiTemplate(jndiTemplate());
        return jdr;
    }
    
 
    @Bean("topicConsumerFactory")
    public DefaultJmsListenerContainerFactory topicConsumerFactory(DemoErrorHandler errorHandler) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory((ConnectionFactory) consumerConnectionFactory().getObject());
        factory.setDestinationResolver(consumerJndiDestinationResolver());
        factory.setErrorHandler(errorHandler);
        //factory.setConcurrency("3-10");
        factory.setPubSubDomain(true); //Use this for topics
       return factory;
    }
    
    @Bean("queueConsumerFactory")
    public DefaultJmsListenerContainerFactory queueConsumerFactory(DemoErrorHandler errorHandler) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory((ConnectionFactory) consumerConnectionFactory().getObject());
        factory.setDestinationResolver(consumerJndiDestinationResolver());
        factory.setErrorHandler(errorHandler);
        //factory.setConcurrency("3-10");
        factory.setPubSubDomain(false); //Use this for topics
       return factory;
    }

    @Service
    public class DemoErrorHandler implements ErrorHandler{   

        public void handleError(Throwable t) {
        	ByteArrayOutputStream os = new ByteArrayOutputStream();
        	PrintStream ps = new PrintStream(os);
        	t.printStackTrace(ps);
        	try {
			   String output = os.toString("UTF8");
	           System.out.println("============= Error processing message: " + t.getMessage()+"\n"+output);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
 
        }
    }

}

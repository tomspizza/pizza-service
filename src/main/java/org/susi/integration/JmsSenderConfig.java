package org.susi.integration;

import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

@Configuration
@EnableIntegration
public class JmsSenderConfig {

	
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
	  jt.setEnvironment(p);
	  return jt;
	}

	
    private JndiObjectFactoryBean producerConnectionFactory() {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiTemplate(jndiTemplate());
        factoryBean.setJndiName(centralConfig.getMbJndiName());
        // following ensures all the properties are injected before returning
        try {
			factoryBean.afterPropertiesSet();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
        return factoryBean;
    }

    private CachingConnectionFactory producerCachingConnectionFactory() {
		CachingConnectionFactory ccf = new CachingConnectionFactory((ConnectionFactory) producerConnectionFactory().getObject());
		ccf.setSessionCacheSize(10);
		return ccf;
	}
	

    // Configure the destination resolver for the producer:
    // Here we are using JndiDestinationResolver for JNDI destinations
    // Other options include using DynamicDestinationResolver for non-JNDI destinations
	private JndiDestinationResolver producerJndiDestinationResolver() {
    	JndiDestinationResolver jdr = new JndiDestinationResolver();
        jdr.setCache(true);
        jdr.setJndiTemplate(jndiTemplate());
        return jdr;
    }

 	@Bean("solaceTopicSenderTemplate")
	public JmsTemplate solaceTopicSenderTemplate() {
		JmsTemplate jt = new JmsTemplate(producerCachingConnectionFactory());
		jt.setDeliveryPersistent(true);
		jt.setDestinationResolver(producerJndiDestinationResolver());
		jt.setPubSubDomain(true); //Set this true for TOPIC, leave this or set false for QUEUES
		return jt;
	}
 	
 	@Bean("solaceQueueSenderTemplate")
	public JmsTemplate solaceQueueSenderTemplate() {
		JmsTemplate jt = new JmsTemplate(producerCachingConnectionFactory());
		jt.setDeliveryPersistent(true);
		jt.setDestinationResolver(producerJndiDestinationResolver());
		jt.setPubSubDomain(false); //Set this true for TOPIC, leave this or set false for QUEUES
		return jt;
	}

}

package org.susi.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
public class CentralConfig {
	
	@Value("${spring.application.name}")
	String springApplicationName;
	public String getSpringAppLicationName() {
		return springApplicationName;
	}

	
	@Value("${mb.initial_context_factory}")
	String mbInitialContextFactory;
	public String getMbInitialContextFactory() {
		return mbInitialContextFactory;
	}


	@Value("${mb.host}")
	String mbHost;
	public String getMbHost() {
		return mbHost;
	}
	
	
	@Value("${mb.port}")
	int mbPort;
	public int getMbPort() {
		return mbPort;
	}
	
	
	@Value("${mb.msg_vpn}")
	String mbMsgVPN;
	public String getMbMsgVPN() {
		return mbMsgVPN;
	}
	
	
	@Value("${mb.username}")
	String mbUserName;
	public String getMbUserName() {
		return mbUserName;
	}
	
	
	@Value("${mb.password}")
	String mbPassword;
	public String getMbPassword() {
		return mbPassword;
	}
	
	
	@Value("${mb.truststore}")
	String mbTrustStore;
	public String getMbTrustStore() {
		return mbTrustStore;
	}
	
	
	@Value("${mb.truststore_password}")
	String mbTrustStorePassword;
	public String getMbTrustStorePassword() {
		return mbTrustStorePassword;
	}
	 
	
	@Value("${log.src_app}")
	String logSrcApp;
	public String getLogSrcApp() {
		return logSrcApp;
	}
	
 
	@Value("${log.target_app}")
	String logTargetApp;
	public String getLogTargetApp() {
		return logTargetApp;
	}
	
 
	@Value("${log.interface_id}")
	String logInterfaceId;
	public String getLogInterfaceId() {
		return logInterfaceId;
	}
	
	
	@Value("${log.process_group}")
	String processGroup;
	public Object getProcessGroup() {
		return processGroup;
	}
	
	
	@Value("${log.jms_destination}")
	String logJmsDestination;
	public String getLogJmsDestination() {
		return logJmsDestination;
	}
	 
	
	@Value("${output.jms_destination}")
	String outputJmsDestination;
	public String getOutputJmsDestination() {
		return outputJmsDestination;
	}
	
	@Value("${output.http_endpoint}")
	String outputEndpoint;
	public String getOutputEndpoint() {
		return outputEndpoint;
	}
	
//	@Value("${input.solace_destination}")
//	String inputSoalceDestination;
//	public String getInputSolaceDestination() {
//		return inputSoalceDestination;
//	}
	
	@Value("${input.jms_destination}")
	String inputJmsDestination;
	public String getInputJmsDestination() {
		return inputJmsDestination;
	}
	
	

	@Value("${mb.jndiname}")
	String mbJndiName;
	public String getMbJndiName() {
		return mbJndiName;
	}
	
	
	@Override
	public String toString() {
		return "CentralConfig [springApplicationName=" + springApplicationName + ", mbInitialContextFactory="
				+ mbInitialContextFactory + ", mbHost=" + mbHost + ", mbPort=" + mbPort + ", mbMsgVPN=" + mbMsgVPN
				+ ", mbUserName=" + mbUserName + ", mbTrustStore=" + mbTrustStore + ", mbTrustStorePassword="
				+ mbTrustStorePassword + ", logSrcApp=" + logSrcApp + ", logTargetApp=" + logTargetApp
				+ ", logInterfaceId=" + logInterfaceId + ", outputJmsDestination=" + outputJmsDestination
				+ ", inputJmsDestination=" + inputJmsDestination + ", toyotaMvmtTransformConfig="
				+ ", mbJndiName=" + mbJndiName + "]";
	}

}

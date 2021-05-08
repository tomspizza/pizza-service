package org.susi.integration.common.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.susi.integration.CentralConfig;

@RestController
@RequestMapping("/service")
class ServiceInfoController {

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired 
	CentralConfig ccentralConf;
	

	@RequestMapping(value="/check-service-discovery", method=RequestMethod.GET)
	public List<ServiceInstance> serviceInstancesByApplicationName() {
		return this.discoveryClient.getInstances(ccentralConf.getSpringAppLicationName());
	}

	@GetMapping(value = "/check-config")
    public String getDescription() { 
        return "CONFIG\n" + ccentralConf.toString();
    }
    
}

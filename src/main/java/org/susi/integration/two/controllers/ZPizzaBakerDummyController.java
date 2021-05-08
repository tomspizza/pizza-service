package org.susi.integration.two.controllers;

import org.springframework.integration.config.EnableIntegration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/makepizza")
@EnableIntegration
public class ZPizzaBakerDummyController {
		
	@PostMapping(value = "/bake")
	public String replay(@RequestBody String request) {
		System.out.println("4444 pizza bake order received this " + request);
		return "OK";
	}

}

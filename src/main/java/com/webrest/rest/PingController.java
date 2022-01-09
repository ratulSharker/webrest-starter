package com.webrest.rest;

import com.webrest.rest.constants.RestEndpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

	@GetMapping(RestEndpoint.PING_ROUTE_V1)
	public String ping() {
		return "ok";
	}
}

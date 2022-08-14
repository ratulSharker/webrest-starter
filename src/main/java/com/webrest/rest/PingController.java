package com.webrest.rest;

import com.webrest.rest.constants.RestRoutes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

	@GetMapping(RestRoutes.PING_ROUTE_V1)
	public String ping() {
		return "ok";
	}
}

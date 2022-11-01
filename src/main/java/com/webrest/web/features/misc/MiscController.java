package com.webrest.web.features.misc;

import com.webrest.web.constants.WebRoutes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MiscController {
	
	@GetMapping(WebRoutes.ACCESS_DENIED)
	public String accessDenied() {
		return "features/misc/access-denied";
	}
}

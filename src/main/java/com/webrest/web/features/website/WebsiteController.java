package com.webrest.web.features.website;

import com.webrest.web.constants.WebRoutes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebsiteController {
	
	@GetMapping(WebRoutes.BLANK)
	public String home() {
		return "features/website/home";
	}
}

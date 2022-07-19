package com.webrest.web.features.role;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.webrest.web.constants.WebEndpoint;

@Controller
public class RoleController {
	
	@GetMapping(WebEndpoint.ROLE)
	public String list() {
		
	}
}

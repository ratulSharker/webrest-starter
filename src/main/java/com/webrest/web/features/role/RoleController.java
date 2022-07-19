package com.webrest.web.features.role;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.webrest.web.constants.WebEndpoint;

@Controller
public class RoleController {
	
	@GetMapping(WebEndpoint.ROLE)
	public String list() {
		
		return "features/role/role-list";
	}

	// @GetMapping(WebEndpoint.ROLE_LOAD_DATA)
	// public String loadRoleData() {

	// }

}

package com.webrest.web.features.dashboard;

import com.webrest.web.constants.WebRoutes;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
	


	@GetMapping(WebRoutes.DASHBOARD)
	public String getDashboard(Model model) {

		

		return "features/dashboard/dashboard";
	}
}

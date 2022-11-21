package com.webrest.web.features.dashboard;

import com.webrest.common.dto.dashboard.DashboardDto;
import com.webrest.web.common.Breadcrumb;
import com.webrest.web.constants.WebRoutes;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

	@GetMapping(WebRoutes.DASHBOARD)
	public String getDashboard(Model model) {
		Breadcrumb.builder().addItem("Admin Dashboard").build(model);

		DashboardDto dashboardDto = DashboardDto.builder().roleCount(10L).userCount(10L).build();
		model.addAttribute("dashboardDto", dashboardDto);

		return "features/dashboard/dashboard";
	}
}

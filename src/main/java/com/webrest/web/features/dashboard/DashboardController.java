package com.webrest.web.features.dashboard;

import com.webrest.common.dto.dashboard.DashboardDto;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.interceptor.AuthorizationInterceptor;
import com.webrest.common.service.DashboardService;
import com.webrest.web.common.Breadcrumb;
import com.webrest.web.constants.WebRoutes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping(WebRoutes.DASHBOARD)
	public String getDashboard(Model model, HttpServletRequest request) {

		Map<AuthorizedFeature, Set<AuthorizedAction>> authorizedFeatureActions = AuthorizationInterceptor.getAuthorizedFeatureActions(request);

		Breadcrumb.builder().addItem("Admin Dashboard").build(model);

		DashboardDto dashboardDto = dashboardService.getDashboardDto(authorizedFeatureActions);
		model.addAttribute("dashboardDto", dashboardDto);

		return "features/dashboard/dashboard";
	}
}

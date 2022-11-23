package com.webrest.common.service;

import java.util.Map;
import java.util.Set;

import com.webrest.common.dto.dashboard.DashboardDto;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.utils.AuthorizationUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	
	private final RoleService roleService;
	private final AppUserService appUserService;

	@Transactional(readOnly = true)
	public DashboardDto getDashboardDto(Map<AuthorizedFeature, Set<AuthorizedAction>> authorizedFeatureActions) {
		DashboardDto dashboardDto = DashboardDto.builder().build();

		if (AuthorizationUtils.hasAccess(AuthorizedFeature.ROLE, AuthorizedAction.LISTING, authorizedFeatureActions)) {
			dashboardDto.setHasAccessToRoleCount(true);
			dashboardDto.setRoleCount(roleService.getActiveRoleCount());
		} else {
			dashboardDto.setHasAccessToRoleCount(false);
		}

		if (AuthorizationUtils.hasAccess(AuthorizedFeature.USER, AuthorizedAction.LISTING, authorizedFeatureActions)) {
			dashboardDto.setHasAccessToUserCount(true);
			dashboardDto.setUserCount(appUserService.getUserCount());
		} else {
			dashboardDto.setHasAccessToUserCount(false);
		}

		return dashboardDto;
	}

}

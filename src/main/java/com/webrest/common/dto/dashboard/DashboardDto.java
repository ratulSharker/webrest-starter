package com.webrest.common.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardDto {
	private boolean hasAccessToRoleCount;
	private Long roleCount;
	private boolean hasAccessToUserCount;
	private Long userCount;

	public boolean hasAnyDashboard() {
		return hasAccessToRoleCount || hasAccessToUserCount;
	}
}

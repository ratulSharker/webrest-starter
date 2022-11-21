package com.webrest.common.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardDto {
	private Long roleCount;
	private Long userCount;	
}

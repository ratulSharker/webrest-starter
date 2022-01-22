package com.webrest.web.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BreadcrumbItem {

	private final String link;
	private final String title;
}

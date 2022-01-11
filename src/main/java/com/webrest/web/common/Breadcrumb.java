package com.webrest.web.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Breadcrumb {
	private final List<BreadcrumbItem> items;

	public static Breadcrumb builder() {
		return new Breadcrumb(new ArrayList<BreadcrumbItem>());
	}

	public Breadcrumb addItem(String title, String link) {
		items.add(BreadcrumbItem.builder().title(title).link(link).build());
		return this;
	}

	public Breadcrumb addItem(String title) {
		items.add(BreadcrumbItem.builder().title(title).build());
		return this;
	}

	public Breadcrumb build(Model model) {
		model.addAttribute("breadcrumb", this);
		return this;
	}
}

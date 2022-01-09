package com.webrest.web.common;

import org.springframework.ui.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alert {
	private boolean success;
	private String title;
	private String details;

	public static void addExceptionAlertAttribuiteToModel(String title, Exception ex, Model model) {
		Alert alert = Alert.builder().success(false).title(title).details(ex.getMessage()).build();
		model.addAttribute("alert", alert);
	}

	public static void addSuccessAlertAttributeToModel(String title, String details, Model model) {
		Alert alert = Alert.builder().success(true).title(title).details(details).build();
		model.addAttribute("alert", alert);
	}
}

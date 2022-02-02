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

	public static void addExceptionAlertAttributeToModel(String title, Exception ex, Model model) {
		addAlertAttributeToModel(false, title, ex.getMessage(), model);
	}

	public static void addSuccessAlertAttributeToModel(String title, String details, Model model) {
		addAlertAttributeToModel(true, title, details, model);
	}

	public static void addFailureAlertAttributeToModel(String title, String details, Model model) {
		addAlertAttributeToModel(false, title, details, model);
	}

	public static void addAlertAttributeToModel(boolean success, String title, String details, Model model) {
		Alert alert = Alert.builder().success(success).title(title).details(details).build();
		model.addAttribute("alert", alert);
	} 
}

package com.webrest.web.features.login;

import com.webrest.common.dto.authentication.request.web.ForgotPasswordRequestDto;
import com.webrest.common.dto.authentication.request.web.RecoverPasswordRequestDto;
import com.webrest.common.service.PasswordRecoveryService;
import com.webrest.web.common.Alert;
import com.webrest.web.constants.WebRoutes;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordRecoveryController {

	private Logger logger = LoggerFactory.getLogger(PasswordRecoveryController.class);

	private PasswordRecoveryService passwordRecoveryService;

	public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
		this.passwordRecoveryService = passwordRecoveryService;
	}

	@GetMapping(value = WebRoutes.FORGOT_PASSWORD)
	public String forgotPasswordGetForm(Model model) {

		model.addAttribute("forgotPasswordForm", new ForgotPasswordRequestDto());
		model.addAttribute("loginPath", WebRoutes.LOGIN);
		return "features/login/forgot-password-form";
	}

	@PostMapping(value = WebRoutes.FORGOT_PASSWORD)
	public String forgotPasswordSubmitForm(
			@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordRequestDto forgotPasswordRequestDto,
			BindingResult result, Model model) {

		model.addAttribute("loginPath", WebRoutes.LOGIN);

		if (result.hasErrors()) {
			model.addAttribute("forgotPasswordForm", forgotPasswordRequestDto);
			return "features/login/forgot-password-form";
		}

		try {
			passwordRecoveryService.sendPasswordRecoveryEmail(forgotPasswordRequestDto.getEmail());
			Alert.addSuccessAlertAttributeToModel("Success",
					"Sent a password recovery email, please check your email", model);
		} catch (Exception ex) {
			logger.error("ERROR: Sending recovery password email", ex);
			Alert.addExceptionAlertAttributeToModel("Failure", ex, model);
		}

		return "features/login/forgot-password-form";
	}

	@GetMapping(value = WebRoutes.RECOVER_PASSWORD)
	public String recoverPasswordForm(@PathVariable("token") String token, Model model) {

		RecoverPasswordRequestDto recoverPasswordRequestDto = RecoverPasswordRequestDto.builder().token(token).build();
		model.addAttribute("recoverPasswordForm", recoverPasswordRequestDto);
		model.addAttribute("loginPath", WebRoutes.LOGIN);

		return "features/login/recover-password-form";
	}

	@PostMapping(value = WebRoutes.RECOVER_PASSWORD)
	public String recoverPasswordSubmitForm(
			@Valid @ModelAttribute("recoverPasswordForm") RecoverPasswordRequestDto recoverPasswordRequestDto,
			BindingResult result, Model model) {

		model.addAttribute("loginPath", WebRoutes.LOGIN);
		if (result.hasErrors()) {
			model.addAttribute("recoverPasswordForm", recoverPasswordRequestDto);
			return "features/login/recover-password-form";
		}

		try {
			passwordRecoveryService.recoverPassword(recoverPasswordRequestDto.getToken(),
					recoverPasswordRequestDto.getPassword());
			Alert.addSuccessAlertAttributeToModel("Success", "Password reset successful", model);
		} catch (Exception ex) {
			Alert.addExceptionAlertAttributeToModel("Unable to reset password", ex, model);
		}

		return "features/login/recover-password-form";
	}
}

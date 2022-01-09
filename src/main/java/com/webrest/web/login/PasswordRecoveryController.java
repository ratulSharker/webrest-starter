package com.webrest.web.login;

import javax.validation.Valid;

import com.webrest.common.dto.authentication.request.web.ForgotPasswordRequestDto;
import com.webrest.common.dto.authentication.request.web.RecoverPasswordRequestDto;
import com.webrest.common.service.PasswordRecoveryService;
import com.webrest.web.common.Alert;
import com.webrest.web.constants.WebEndpoint;

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

	@GetMapping(value = WebEndpoint.FORGOT_PASSWORD)
	public String forgotPasswordGetForm(Model model) {

		model.addAttribute("forgotPasswordForm", new ForgotPasswordRequestDto());
		model.addAttribute("loginPath", WebEndpoint.LOGIN);
		return "login/forgot-password-form";
	}

	@PostMapping(value = WebEndpoint.FORGOT_PASSWORD)
	public String forgotPasswordSubmitForm(
			@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordRequestDto forgotPasswordRequestDto,
			BindingResult result, Model model) {

		model.addAttribute("loginPath", WebEndpoint.LOGIN);

		if (result.hasErrors()) {
			model.addAttribute("forgotPasswordForm", forgotPasswordRequestDto);
			return "login/forgot-password-form";
		}

		try {
			passwordRecoveryService.sendPasswordRecoveryEmail(forgotPasswordRequestDto.getEmail());
			Alert alert = Alert.builder().success(true).title("Success")
					.details("Sent a password recovery email, please check your email").build();
			model.addAttribute("alert", alert);
		} catch (Exception ex) {
			logger.error("ERROR: Sending recovery password email", ex);
			Alert alert = Alert.builder().success(false).title("Failure").details(ex.getMessage()).build();
			model.addAttribute("alert", alert);
		}

		return "login/forgot-password-form";
	}

	@GetMapping(value = WebEndpoint.RECOVER_PASSWORD)
	public String recoverPasswordForm(@PathVariable("token") String token, Model model) {

		RecoverPasswordRequestDto recoverPasswordRequestDto = RecoverPasswordRequestDto.builder().token(token).build();
		model.addAttribute("recoverPasswordForm", recoverPasswordRequestDto);
		model.addAttribute("loginPath", WebEndpoint.LOGIN);

		return "login/recover-password-form";
	}

	@PostMapping(value = WebEndpoint.RECOVER_PASSWORD)
	public String recoverPasswordSubmitForm(
			@Valid @ModelAttribute("recoverPasswordForm") RecoverPasswordRequestDto recoverPasswordRequestDto,
			BindingResult result, Model model) {

		model.addAttribute("loginPath", WebEndpoint.LOGIN);
		if (result.hasErrors()) {
			model.addAttribute("recoverPasswordForm", recoverPasswordRequestDto);
			return "login/recover-password-form";
		}

		try {
			passwordRecoveryService.recoverPassword(recoverPasswordRequestDto.getToken(),
					recoverPasswordRequestDto.getPassword());
			Alert alert = Alert.builder().success(true).title("Success").details("Password reset successful").build();
			model.addAttribute("alert", alert);
		} catch (Exception ex) {
			Alert alert = Alert.builder().success(false).title("Unable to reset password").details(ex.getMessage())
					.build();
			model.addAttribute("alert", alert);
		}

		return "login/recover-password-form";
	}
}

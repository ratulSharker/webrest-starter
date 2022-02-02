package com.webrest.web.features.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.webrest.common.dto.authentication.request.web.AuthenticationRequestDto;
import com.webrest.common.dto.authentication.response.AuthenticationResponseDto;
import com.webrest.common.service.AuthenticationService;
import com.webrest.common.service.JWTService;
import com.webrest.common.utils.CookieUtils;
import com.webrest.web.common.Alert;
import com.webrest.web.constants.WebEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(LoginController.class);

	private final AuthenticationService authenticationService;
	private final JWTService jwtService;

	public LoginController(AuthenticationService authenticationService, JWTService jwtService) {
		this.authenticationService = authenticationService;
		this.jwtService = jwtService;
	}

	@GetMapping(WebEndpoint.BLANK)
	public String blankURL() {
		return String.format("redirect:%s", WebEndpoint.LOGIN);
	}

	@GetMapping(WebEndpoint.LOGIN)
	public String getLoginForm(Model model, HttpServletRequest request, HttpServletResponse response) {

		String token = CookieUtils.getAuthorization(request);
		if (StringUtils.isNotBlank(token)) {
			try {
				jwtService.verifyToken(token);

				CookieUtils.setLastSelectedMenu(WebEndpoint.USER, response, Integer.MAX_VALUE);
				return String.format("redirect:%s", WebEndpoint.USER);
			} catch (Exception ex) {
				logger.error("Error during token verification", ex);
				CookieUtils.clearAuthorization(request, response);
			}
		}

		model.addAttribute("loginForm", new AuthenticationRequestDto());
		model.addAttribute("forgotPasswordPath", WebEndpoint.FORGOT_PASSWORD);
		return "features/login/login-form";
	}

	@PostMapping(WebEndpoint.LOGIN)
	public String submitLoginForm(@Valid @ModelAttribute("loginForm") AuthenticationRequestDto authenticationRequestDto,
			BindingResult result, Model model, HttpServletResponse response) {

		if (result.hasErrors()) {
			model.addAttribute("loginForm", authenticationRequestDto);
			model.addAttribute("forgotPasswordPath", WebEndpoint.FORGOT_PASSWORD);
			return "features/login/login-form";
		}

		try {
			AuthenticationResponseDto authenticationResponseDto = authenticationService
					.authenticate(authenticationRequestDto);
			int tokenExpiryTimeInSeconds = (int) authenticationService.tokenExpiryTimeInMinutes() * 60;
			CookieUtils.setAuthorization(authenticationResponseDto.getToken(), response, tokenExpiryTimeInSeconds);
		} catch (Exception ex) {
			// TODO: Use updated alert builder api.
			Alert alert = Alert.builder().success(false).title("Authentication failed").details(ex.getMessage())
					.build();
			model.addAttribute("alert", alert);
			model.addAttribute("forgotPasswordPath", WebEndpoint.FORGOT_PASSWORD);
			return "features/login/login-form";
		}

		CookieUtils.setLastSelectedMenu(WebEndpoint.USER, response, Integer.MAX_VALUE);
		return String.format("redirect:%s", WebEndpoint.USER);
	}

	@GetMapping(WebEndpoint.LOGOUT)
	public String logout(Model model, HttpServletResponse response, HttpServletRequest request) {

		CookieUtils.clearAuthorization(request, response);

		return String.format("redirect:%s", WebEndpoint.LOGIN);
	}
}

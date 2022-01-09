package com.webrest.common.service;

import java.util.List;
import java.util.Map;

import com.auth0.jwt.interfaces.Claim;
import com.webrest.common.entity.AppUser;
import com.webrest.common.enums.AppUserType;
import com.webrest.common.utils.HashUtils;
import com.webrest.web.constants.WebEndpoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordRecoveryService {

	private AppUserService appUserService;
	private JWTService jwtService;
	private EmailService emailService;

	@Value("${APP_HOST_URL}")
	private String appHost;

	public PasswordRecoveryService(AppUserService appUserService, JWTService jwtService, EmailService emailService) {
		this.appUserService = appUserService;
		this.jwtService = jwtService;
		this.emailService = emailService;
	}

	public void sendPasswordRecoveryEmail(String email) {
		AppUser appUser = this.appUserService.getAppUserByEmail(email, List.of(AppUserType.ADMIN));
		String token = jwtService.generateJWTTokenForPasswordRecovery(appUser);
		String redirectUrl = String.format("%s%s%s", appHost, WebEndpoint.RECOVER_PASSWORD.replace("{token}", ""),
				token);
		emailService.sendPasswordRecoveryEmail(email, redirectUrl);
	}

	public void recoverPassword(String token, String plainTextPassword) {
		Map<String, Claim> claims = jwtService.verifyToken(token);

		Long appUserId = claims.get(jwtService.APP_USER_ID_CLAIM_KEY).asLong();
		String hashedPassword = HashUtils.hashPassword(plainTextPassword);

		appUserService.updatePassword(appUserId, hashedPassword);
	}

}

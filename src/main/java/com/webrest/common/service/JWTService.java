package com.webrest.common.service;

import java.util.Date;
import java.util.Map;
import jakarta.annotation.PostConstruct;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.webrest.common.entity.AppUser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
public class JWTService {

	public final String APP_USER_ID_CLAIM_KEY = "uid";
	public final String APP_USER_ROLE_IDS = "roleIds";

	@Value("${JWT_EXPIRY_IN_MINUTES}")
	@Getter
	private long jwtExpiryTimeInMinutes;

	@Value("${PASSWORD_RECOVERY_EMAIL_TOKEN_TIMEOUT_IN_MINUTES}")
	private long passwordRecoveryTokenTimeoutInMinutes;

	@Value("${JWT_SYMETTRIC_SECRET}")
	private String symettricSecret = "secret";

	private Algorithm algorithm;

	@PostConstruct
	private void postConstruct() {
		algorithm = Algorithm.HMAC256(symettricSecret);
	}

	public String generateJWTTokenForAuthentication(AppUser appUser) {
		return generateToken(appUser, jwtExpiryTimeInMinutes);
	}

	public String generateJWTTokenForPasswordRecovery(AppUser appUser) {
		return generateToken(appUser, passwordRecoveryTokenTimeoutInMinutes);
	}

	private String generateToken(AppUser appUser, long timeout) {
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + (timeout * 60 * 1000));

		return JWT.create().withIssuedAt(issuedAt).withExpiresAt(expiresAt)
				.withClaim(APP_USER_ID_CLAIM_KEY, appUser.getAppUserId())
				.withClaim(APP_USER_ROLE_IDS, appUser.getRoleIds()).sign(algorithm);
	}

	public Map<String, Claim> verifyToken(String token) {
		JWTVerifier verifier = JWT.require(algorithm).build();
		return verifier.verify(token).getClaims();
	}
}

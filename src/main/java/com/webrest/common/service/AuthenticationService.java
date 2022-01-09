package com.webrest.common.service;

import javax.transaction.Transactional;

import com.webrest.common.dto.authentication.request.web.AuthenticationRequestDto;
import com.webrest.common.dto.authentication.response.AuthenticationResponseDto;
import com.webrest.common.entity.AppUser;
import com.webrest.common.exception.InvalidCredentialsException;
import com.webrest.common.utils.HashUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService {

	private Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	private AppUserService appUserService;
	private JWTService jwtService;

	public AuthenticationService(AppUserService appUserService, JWTService jwtService) {
		this.appUserService = appUserService;
		this.jwtService = jwtService;
	}

	public AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationRequestDto) {
		try {
			String hashedPwd = HashUtils.hashPassword(authenticationRequestDto.getPassword());
			AppUser user = appUserService
					.getAppUserMobileOrEmailAndPassword(authenticationRequestDto.getMobileOrEmail(), hashedPwd);
			String token = jwtService.generateJWTTokenForAuthentication(user);
			return AuthenticationResponseDto.builder().token(token).build();
		} catch (Exception ex) {
			logger.error("ERROR : During authentication", ex);
			throw new InvalidCredentialsException("Wrong username or password");
		}
	}

	public long tokenExpiryTimeInMinutes() {
		return jwtService.getJwtExpiryTimeInMinutes();
	}
}

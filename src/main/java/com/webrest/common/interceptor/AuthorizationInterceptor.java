package com.webrest.common.interceptor;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webrest.common.dto.response.ErrorResponse;
import com.webrest.common.dto.response.Metadata;
import com.webrest.common.dto.response.Response;
import com.webrest.common.entity.AppUser;
import com.webrest.common.service.AppUserService;
import com.webrest.common.service.AuthorizationService;
import com.webrest.common.service.JWTService;
import com.webrest.common.utils.CookieUtils;
import com.webrest.rest.constants.RestRoutes;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

	public static final String PRINCIPLE_APP_USER_KEY = "principle_app_user";
	public final String REST_AUTHORIZATION_HEADER = "Authorization";

	private final JWTService jwtService;
	// TODO: Remove this app user service from here
	private final AppUserService appUserService;
	private final ObjectMapper objectMapper;
	private final AuthorizationService authorizationService;

	// public AuthorizationInterceptor(JWTService jwtService, AppUserService appUserService, ObjectMapper objectMapper) {
	// 	this.jwtService = jwtService;
	// 	this.appUserService = appUserService;
	// 	this.objectMapper = objectMapper;
	// }

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI().substring(request.getContextPath().length());

		if (path.startsWith(RestRoutes.PREFIX)) {
			// Coming from API
			return handleRestAuthorization(request, response);
		} else {
			// Coming from Web Admin
			authorizationService.printEndpointDetails(request);
			return handleWebappAuthorization(request, response);
		}

	}

	private boolean handleRestAuthorization(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String token = request.getHeader(REST_AUTHORIZATION_HEADER);

		try {
			verifyTokenAndInjectAppUser(request, token);
			return true;
		} catch (Exception ex) {
			// Send 401, with appropriate reasoning
			response.setHeader("content-type", "application/json");
			response.setStatus(401);

			String message = ex.getMessage() == null ? "Unauthorized" : ex.getMessage();
			Response<Object> responseBody = Response.<Object>builder()
					.metadata(Metadata.builder().error(ErrorResponse.builder().message(message).build()).build())
					.build();
			response.getWriter().write(objectMapper.writeValueAsString(responseBody));
			return false;
		}

	}

	private boolean handleWebappAuthorization(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String token = CookieUtils.getAuthorization(request);

		try {
			verifyTokenAndInjectAppUser(request, token);
			return true;
		} catch (Exception ex) {
			// Logout the web user
			response.sendRedirect(com.webrest.web.constants.WebRoutes.LOGOUT);
			return false;
		}
	}

	// Delegate this verification process to `AuthenticationService`
	private void verifyTokenAndInjectAppUser(HttpServletRequest request, String token) {
		Map<String, Claim> claims = jwtService.verifyToken(token);

		Long appUserId = claims.get(jwtService.APP_USER_ID_CLAIM_KEY).asLong();

		// TODO: This db call needed to be removed ASAP.
		AppUser appUser = appUserService.findById(appUserId);
		appUserService.detachAppUserFromJPA(appUser);

		request.setAttribute(PRINCIPLE_APP_USER_KEY, appUser);
	}

	public static AppUser getPrincipleObject(HttpServletRequest request) {
		return (AppUser) request.getAttribute(PRINCIPLE_APP_USER_KEY);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		if (principleObject != null && modelAndView != null && !(modelAndView.getView() instanceof RedirectView)) {
			modelAndView.addObject("loggedInUser", principleObject);
			modelAndView.addObject("fileDownloadPath", RestRoutes.FILE_DOWNLOAD.replace("**", ""));
		}

	}
}

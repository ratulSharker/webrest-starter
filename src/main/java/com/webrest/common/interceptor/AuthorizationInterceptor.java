package com.webrest.common.interceptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webrest.common.dto.response.ErrorResponse;
import com.webrest.common.dto.response.Metadata;
import com.webrest.common.dto.response.Response;
import com.webrest.common.entity.AppUser;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.service.AppUserService;
import com.webrest.common.service.AuthorizationService;
import com.webrest.common.service.AuthorizationService.Endpoint;
import com.webrest.common.service.JWTService;
import com.webrest.common.utils.CookieUtils;
import com.webrest.rest.constants.RestRoutes;
import com.webrest.web.constants.WebRoutes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

	public static final String PRINCIPLE_APP_USER_KEY = "principle_app_user";
	public static final String PRINCIPLE_APP_USER_ROLE_IDS_KEY = "principle_app_user_role_ids";
	public static final String AUTHORIZED_FEATURE_ACTIONS = "authorized_feature_actions";
	public static final String REST_AUTHORIZATION_HEADER = "Authorization";

	private final JWTService jwtService;
	// TODO: Remove this app user service from here
	private final AppUserService appUserService;
	private final ObjectMapper objectMapper;
	private final AuthorizationService authorizationService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI().substring(request.getContextPath().length());

		if (path.startsWith(RestRoutes.PREFIX)) {
			// Coming from API
			return handleRestAuthorization(request, response);
		} else {
			// Coming from Web Admin
			// authorizationService.printEndpointDetails(request);
			return handleWebappAuthorization(request, response);
		}

	}

	private boolean handleRestAuthorization(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		try {

			Endpoint endpoint = authorizationService.getEndpoint(request);
			if (endpoint.isPublic())
				return true;

			String token = request.getHeader(REST_AUTHORIZATION_HEADER);
			List<Long> roleIds = verifyTokenInjectAppUserAndExtractRoleIds(request, token);
			if (hasAuthorizationToWebRoute(request, endpoint, roleIds) == false) {
				sendForbiddenMessage(response);
				return false;
			}
			return true;
		} catch (Exception ex) {
			// Send 401, with appropriate reasoning
			log.error("Error during REST request authentication", ex);
			sendUnauthorizedMessage(response);
			return false;
		}
	}

	private void sendForbiddenMessage(HttpServletResponse response) throws IOException {
		sendJSONMessage(response, "Forbidden", 403);
	}

	private void sendUnauthorizedMessage(HttpServletResponse response)
			throws IOException {
		sendJSONMessage(response, "Unauthorized", 401);
	}

	private void sendJSONMessage(HttpServletResponse response, String message, int statusCode)
			throws IOException {
		response.setHeader("content-type", "application/json");
		response.setStatus(statusCode);

		Response<Object> responseBody =
				Response.<Object>builder()
						.metadata(Metadata.builder()
								.error(ErrorResponse.builder().message(message).build()).build())
						.build();
		response.getWriter().write(objectMapper.writeValueAsString(responseBody));
	}

	private boolean handleWebappAuthorization(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		boolean isPublicButOptionalAuthorizedUser = false;

		try {

			Endpoint endpoint = authorizationService.getEndpoint(request);

			if(endpoint.isPublic()) return true;

			isPublicButOptionalAuthorizedUser = endpoint.isPublicButOptionalAuthorizedUser();

			String token = CookieUtils.getAuthorization(request);

			if(isPublicButOptionalAuthorizedUser && StringUtils.isBlank(token)) return true;

			List<Long> roleIds = verifyTokenInjectAppUserAndExtractRoleIds(request, token);
			if(hasAuthorizationToWebRoute(request, endpoint, roleIds) == false) {
				response.sendRedirect(WebRoutes.ACCESS_DENIED);
				return false;
			}
			return true;
		} catch (Exception ex) {
			if (isPublicButOptionalAuthorizedUser) {
				log.error(
						"Error during web app authorization, but route marked as `isPublicButOptionalAuthorizedUser`",
						ex);
				return true;
			} else {
				// Logout the web user
				log.error("Error during web app authorization", ex);
				response.sendRedirect(WebRoutes.LOGIN);
				return false;
			}
		}
	}

	// Delegate this verification process to `AuthenticationService`
	private List<Long> verifyTokenInjectAppUserAndExtractRoleIds(HttpServletRequest request,
			String token) {
		Map<String, Claim> claims = jwtService.verifyToken(token);

		Long appUserId = claims.get(jwtService.APP_USER_ID_CLAIM_KEY).asLong();
		List<Long> roleIds = claims.get(jwtService.APP_USER_ROLE_IDS).asList(Long.class);

		// TODO: This db call needed to be removed ASAP.
		AppUser appUser = appUserService.findById(appUserId);
		appUserService.detachAppUserFromJPA(appUser);

		request.setAttribute(PRINCIPLE_APP_USER_KEY, appUser);
		request.setAttribute(PRINCIPLE_APP_USER_ROLE_IDS_KEY, roleIds);

		return roleIds;
	}

	private boolean hasAuthorizationToWebRoute(HttpServletRequest request, Endpoint endpoint,
			List<Long> principleObjectRoleIds)
			throws JsonProcessingException {
		Map<AuthorizedFeature, Set<AuthorizedAction>> authorizedFeatureActions = authorizationService
				.getAuthorizedFeatureActions(principleObjectRoleIds);
		request.setAttribute(AUTHORIZED_FEATURE_ACTIONS, authorizedFeatureActions);

		if (endpoint.isPublic() || endpoint.isPublicButOptionalAuthorizedUser()
				|| endpoint.isPublicForAuthorizedUser()) {
			return true;
		}
		return authorizationService.hasAccess(endpoint, authorizedFeatureActions);
	}

	public static AppUser getPrincipleObject(HttpServletRequest request) {
		return (AppUser) request.getAttribute(PRINCIPLE_APP_USER_KEY);
	}

	public static List<Long> getPrincipleObjectRoleIds(HttpServletRequest request) {
		return (List<Long>) request.getAttribute(PRINCIPLE_APP_USER_ROLE_IDS_KEY);
	}

	public static Map<AuthorizedFeature, Set<AuthorizedAction>> getAuthorizedFeatureActions(
			HttpServletRequest request) {
		return (Map<AuthorizedFeature, Set<AuthorizedAction>>) request.getAttribute(AUTHORIZED_FEATURE_ACTIONS);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		AppUser principleObject = AuthorizationInterceptor.getPrincipleObject(request);
		if (principleObject != null && modelAndView != null && !(modelAndView.getView() instanceof RedirectView)) {
			modelAndView.addObject("loggedInUser", principleObject);
			modelAndView.addObject("fileDownloadPath", RestRoutes.FILE_DOWNLOAD.replace("**", ""));

			Map<AuthorizedFeature, Set<AuthorizedAction>> authorizedFeatureActions = AuthorizationInterceptor
					.getAuthorizedFeatureActions(request);
			modelAndView.addObject("authorizedFeatureActions", authorizedFeatureActions);
		}

	}
}

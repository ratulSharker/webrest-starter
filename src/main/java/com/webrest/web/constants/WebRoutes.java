package com.webrest.web.constants;

import java.util.List;

import com.webrest.common.annotation.Authorization;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;

import org.springframework.http.HttpMethod;

public class WebRoutes {
	private WebRoutes() {

	}

	@Authorization(isPublic = true)
	public static final String FAVICON = "/favicon.ico";
	@Authorization(isPublic = true)
	public static final String BLANK = "/";
	@Authorization(isPublic = true)
	public static final String LOGIN = "/login";
	@Authorization(isPublic = true)
	public static final String ERROR = "/error";
	@Authorization(isPublicForAuthorizedUser = true)
	public static final String LOGOUT = "/logout";

	@Authorization(isPublic = true)
	public static final String FORGOT_PASSWORD = "/forgot-password";
	@Authorization(isPublic = true)
	public static final String RECOVER_PASSWORD = "/recover-password/{token}";

	@Authorization(isPublicForAuthorizedUser = true)
	public static final String MY_PROFILE = "/profile";
	@Authorization(isPublicForAuthorizedUser = true)
	public static final String MY_PROFILE_UPDATE = "/update-profile";

	// User
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.LISTING)
	public static final String USER = "/user";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.LISTING)
	public static final String USER_LOAD_DATA = "/user-load-data";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.CREATE, httpMethods = { HttpMethod.GET,
			HttpMethod.POST })
	public static final String CREATE_USER = "/create-user";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.VIEW)
	public static final String USER_DETAILS = "/user-details/{appUserId}";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.UPDATE, httpMethods = { HttpMethod.GET,
			HttpMethod.POST })
	public static final String UPDATE_USER = "/update-user/{appUserId}";

	// Role
	@Authorization(feature = AuthorizedFeature.ROLE, action = AuthorizedAction.LISTING)
	public static final String ROLE = "/role";
	@Authorization(feature = AuthorizedFeature.ROLE, action = AuthorizedAction.LISTING)
	public static final String ROLE_LOAD_DATA = "/role-load-data";
	@Authorization(feature = AuthorizedFeature.ROLE, action = AuthorizedAction.CREATE, httpMethods = { HttpMethod.GET,
			HttpMethod.POST })
	public static final String CREATE_ROLE = "/create-role";
	@Authorization(feature = AuthorizedFeature.ROLE, action = AuthorizedAction.UPDATE, httpMethods = { HttpMethod.GET,
			HttpMethod.POST })
	public static final String UPDATE_ROLE = "/update-role/{roleId}";

	public static final List<String> PUBLIC_ROUTE_PATTERNS = List.of(FAVICON, BLANK, LOGIN, LOGOUT, "/static/**",
			"/webjars/**",
			FORGOT_PASSWORD, RECOVER_PASSWORD.replace("{token}", "**"));
}

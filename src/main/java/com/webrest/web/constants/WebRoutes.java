package com.webrest.web.constants;

import java.util.List;

import com.webrest.common.annotation.Authorization;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;

public class WebRoutes {
	private WebRoutes() {

	}

	@Authorization(isPublic = true)
	public static final String FAVICON = "/favicon.ico";
	@Authorization(isPublic = true)
	public static final String BLANK = "/";
	@Authorization(isPublic = true)
	public static final String LOGIN = "/login";
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

	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.LISTING)
	public static final String USER = "/user";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.LISTING)
	public static final String USER_LOAD_DATA = "/user-load-data";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.CREATE)
	public static final String CREATE_USER = "/create-user";

	public static final String UPDATE_END_USER = "/update-end-user/{appUserId}";

	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.VIEW)
	public static final String USER_DETAILS = "/user-details/{appUserId}";
	@Authorization(feature = AuthorizedFeature.USER, action = AuthorizedAction.UPDATE)
	public static final String UPDATE_USER = "/update-user/{appUserId}";

	public static final String ROLE = "/role";
	public static final String ROLE_LOAD_DATA = "/role-load-data";
	public static final String CREATE_ROLE = "/create-role";
	public static final String UPDATE_ROLE = "/update-role/{roleId}";

	public static final List<String> PUBLIC_ROUTE_PATTERNS = List.of(FAVICON, BLANK, LOGIN, LOGOUT, "/static/**",
			"/webjars/**",
			FORGOT_PASSWORD, RECOVER_PASSWORD.replace("{token}", "**"));
}

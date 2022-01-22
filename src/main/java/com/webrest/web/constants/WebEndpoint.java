package com.webrest.web.constants;

import java.util.List;

public class WebEndpoint {
	private WebEndpoint() {

	}

	public static final String FAVICON = "/favicon.ico";
	public static final String BLANK = "/";
	public static final String LOGIN = "/login";
	public static final String LOGOUT = "/logout";

	public static final String FORGOT_PASSWORD = "/forgot-password";
	public static final String RECOVER_PASSWORD = "/recover-password/{token}";

	public static final String MY_PROFILE = "/profile";
	public static final String MY_PROFILE_UPDATE = "/update-profile";

	public static final String USER = "/user";
	public static final String USER_LOAD_DATA = "/user-load-data";
	public static final String CREATE_ADMIN_USER = "/create-admin-user";
	public static final String CREATE_END_USER = "/create-end-user";
	public static final String ADMIN_USER_DETAILS = "/admin-user-details/{appUserId}";
	public static final String UPDATE_ADMIN_USER = "/update-admin-user/{appUserId}";
	public static final String END_USER_DETAILS = "/end-user-details/{appUserId}";
	public static final String UPDATE_END_USER = "/update-end-user/{appUserId}";

	public static final List<String> PUBLIC_ROUTE_PATTERNS = List.of(FAVICON, BLANK, LOGIN, LOGOUT, "/static/**",
			"/webjars/**",
			FORGOT_PASSWORD, RECOVER_PASSWORD.replace("{token}", "**"));
}

package com.webrest.rest.constants;

import com.webrest.common.annotation.Authorization;
import com.webrest.common.enums.authorization.http.HttpMethod;

public final class RestRoutes {
	private RestRoutes() {

	}

	// API Parts
	public static final String PREFIX = "/api";
	public static final String PREFIX_VERSION = PREFIX + "/v1";

	@Authorization(isPublic = true)
	public static final String PING_ROUTE_V1 = PREFIX_VERSION + "/ping";

	// API Parts
	public static final String USERS = PREFIX_VERSION + "/users";
	@Authorization(isPublicForAuthorizedUser = true, httpMethods = {HttpMethod.GET, HttpMethod.PUT})
	public static final String USERS_ME = USERS + "/me";

	@Authorization(isPublicButOptionalAuthorizedUser = true, httpMethods = {HttpMethod.POST})
	public static final String FILE_UPLOAD = PREFIX_VERSION + "/upload";
	@Authorization(isPublic = true)
	public static final String FILE_DOWNLOAD = PREFIX_VERSION + "/download/**";
}

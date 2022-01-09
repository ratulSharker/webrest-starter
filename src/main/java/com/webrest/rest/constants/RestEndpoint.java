package com.webrest.rest.constants;

import java.util.List;

public final class RestEndpoint {
	private RestEndpoint() {

	}

	public static final String PREFIX = "/api";
	public static final String PREFIX_VERSION = PREFIX + "/v1";

	public static final String PING_ROUTE_V1 = PREFIX_VERSION + "/ping";

	public static final String USERS = PREFIX_VERSION + "/users";
	public static final String USERS_ME = USERS + "/me";

	public static final String FILE_UPLOAD = PREFIX_VERSION + "/upload";
	public static final String FILE_DOWNLOAD = PREFIX_VERSION + "/download/**";

	public static final List<String> PUBLIC_ROUTE_PATTERNS = List.of(PING_ROUTE_V1, FILE_DOWNLOAD);
}

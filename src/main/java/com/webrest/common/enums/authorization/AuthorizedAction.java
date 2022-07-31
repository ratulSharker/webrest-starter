package com.webrest.common.enums.authorization;

public enum AuthorizedAction {
	NONE,

	// Common
	CREATE, UPDATE, LISTING, DELETE,
	
	// User Action
	ADMIN_USER_VIEW_DETAILS,
}

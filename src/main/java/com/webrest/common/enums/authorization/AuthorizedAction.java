package com.webrest.common.enums.authorization;

public enum AuthorizedAction {
	NONE,

	// Common
	UPDATE, LISTING, DELETE,
	
	// User Action
	CREATE_ADMIN_USER, CREATE_END_USER,
	ADMIN_USER_VIEW_DETAILS,
}

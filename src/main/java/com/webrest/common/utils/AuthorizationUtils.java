package com.webrest.common.utils;

import java.util.Map;
import java.util.Set;

import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;

import org.springframework.util.CollectionUtils;

public final class AuthorizationUtils {
	
	public static boolean hasAccess(AuthorizedFeature feature, AuthorizedAction action,
			Map<AuthorizedFeature, Set<AuthorizedAction>> authorizedFeatureActions) {
		Set<AuthorizedAction> authorizedActions = authorizedFeatureActions.get(feature);

		if (CollectionUtils.isEmpty(authorizedActions)) {
			return false;
		}

		return authorizedActions.contains(action);
	}
}

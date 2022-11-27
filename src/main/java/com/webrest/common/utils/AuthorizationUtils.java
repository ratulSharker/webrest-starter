package com.webrest.common.utils;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.service.AuthorizationService.Endpoint;

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

	public static boolean hasAccess(Endpoint endpoint,
			Map<AuthorizedFeature, Set<AuthorizedAction>> authroizedFeatureActions)
			throws JsonProcessingException {
		Set<AuthorizedAction> features = authroizedFeatureActions.get(endpoint.getFeature());

		// TODO: Use apache common utils
		if (CollectionUtils.isEmpty(features)) {
			return false;
		}

		return features.contains(endpoint.getAction());
	}
}

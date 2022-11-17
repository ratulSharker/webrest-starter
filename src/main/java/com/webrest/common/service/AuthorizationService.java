package com.webrest.common.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webrest.common.annotation.Authorization;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.web.constants.WebRoutes;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class AuthorizationService {

	private final RoleService roleService;

	@Getter
	@AllArgsConstructor
	public class Endpoint {
		private final AuthorizedFeature feature;
		private final AuthorizedAction action;
		private final boolean isPublic;
		private final boolean isPublicForAuthorizedUser;
		private final boolean isPublicButOptionalAuthorizedUser;
		private final String path;
		private final HttpMethod[] methods;
	}

	// TODO: Needed to be stored into the redis
	private Map<HttpMethod, Map<String, Endpoint>> endpointByVerbAndPath = new HashMap<>();
	private Map<AuthorizedFeature, Set<AuthorizedAction>> featuresWithActions = new HashMap<>();

	@PostConstruct
	public void postConstruct() {
		collectEndpoints();
	}

	private void collectEndpoints() {
		Field[] fields = WebRoutes.class.getDeclaredFields();
		for (int index = 0; index < fields.length; index++) {
			Field field = fields[index];

			Annotation[] annotations = field.getAnnotations();
			for(int index2 = 0; index2 < annotations.length; index2++) {
				Annotation annotation = annotations[index2];
				if(annotation instanceof Authorization) {
					Authorization authorization = (Authorization) annotation;
					try {
						Object value = field.get(WebRoutes.class);
						if(value instanceof String) {
							String fieldValue = (String) value;
							Endpoint endpoint = new Endpoint(authorization.feature(),
									authorization.action(), authorization.isPublic(),
									authorization.isPublicForAuthorizedUser(),
									authorization.isPublicButOptionalAuthorizedUser(), fieldValue,
									authorization.httpMethods());
							addEndpoint(endpoint);
							addFeatureAndActions(endpoint);
						}
						
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void addEndpoint(Endpoint endpoint) {
		HttpMethod[] httpMethods = endpoint.getMethods();
		for (int index = 0; index < httpMethods.length; index++) {
			HttpMethod httpMethod = httpMethods[index];
			Map<String, Endpoint> endpointByPath = endpointByVerbAndPath.get(httpMethod);

			if (endpointByPath == null)
				endpointByPath = new HashMap<>();

			endpointByPath.put(endpoint.getPath(), endpoint);
			endpointByVerbAndPath.put(httpMethod, endpointByPath);
		}
	}

	private void addFeatureAndActions(Endpoint endpoint) {
		Set<AuthorizedAction> actions = featuresWithActions.get(endpoint.getFeature());

		if(actions == null) {
			actions = new HashSet<AuthorizedAction>();
			featuresWithActions.put(endpoint.getFeature(), actions);
		}

		actions.add(endpoint.getAction());
	}

	public void printEndpointDetails(HttpServletRequest request) {
		HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		String bestMatchPattern =
				(String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		// Endpoint endpoint = endpointByVerbAndPath.get(httpMethod).get(bestMatchPattern);
		// log.info(endpoint.getFeature().toString());
		// log.info(endpoint.getAction().toString());
		// log.info(endpoint.getPath());
	}

	public Endpoint getEndpoint(HttpServletRequest request) {
		HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		String bestMatchPattern =
				(String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		Map<String, Endpoint> endpointByPattern = endpointByVerbAndPath.get(httpMethod);

		if(endpointByPattern == null || endpointByPattern.isEmpty()) {
			throwHttpRequestDoesNotMatchAnyEndpoint(httpMethod, bestMatchPattern);
		}

		Endpoint endpoint = endpointByPattern.get(bestMatchPattern);

		if(endpoint == null) {
			throwHttpRequestDoesNotMatchAnyEndpoint(httpMethod, bestMatchPattern);
		}

		return endpoint;
	}

	// TODO: Current implementation does db call and checking is not efficient.
	// We want to introduce redis and optimize the lookup.
	public boolean hasAccess(Endpoint endpoint, Map<AuthorizedFeature, Set<AuthorizedAction>> authroizedFeatureActions)
			throws JsonProcessingException {
		Set<AuthorizedAction> features = authroizedFeatureActions.get(endpoint.getFeature());

		// TODO: Use apache common utils
		if (CollectionUtils.isEmpty(features)) {
			return false;
		}

		return features.contains(endpoint.getAction());
	}

	// TODO: Current implementation does db call and checking is not efficient.
	// We want to introduce redis and optimize the lookup.
	public Map<AuthorizedFeature, Set<AuthorizedAction>> getAuthorizedFeatureActions(List<Long> roleIds) throws JsonProcessingException {
		return roleService.getAuthorizedFeatureActionsForGivenRoleIds(roleIds);
	}

	private void throwHttpRequestDoesNotMatchAnyEndpoint(HttpMethod method, String bestMatchPattern) {
		String errMsg = method + " : " + bestMatchPattern + " (No Endpoint Found)";
		throw new RuntimeException(errMsg);
	}
}

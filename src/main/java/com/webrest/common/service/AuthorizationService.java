package com.webrest.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webrest.common.annotation.Authorization;
import com.webrest.common.annotation.Authorizations;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.common.enums.authorization.http.HttpMethod;
import com.webrest.rest.constants.RestRoutes;
import com.webrest.web.constants.WebRoutes;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		// TODO: Prepare a provision such that, these classes can be sent via `application.properties`
		collectEndpoints(WebRoutes.class.getDeclaredFields());
		collectEndpoints(RestRoutes.class.getDeclaredFields());
	}

	private void collectEndpoints(Field[] fields) {
		for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
			Field field = fields[fieldIndex];

			Annotation[] annotations = field.getAnnotations();
			for(Annotation annotation : annotations) {

				try {
					Object value = field.get(WebRoutes.class);
					if(value instanceof String) {
						String fieldValue = (String) value;

						if(annotation instanceof Authorization) {
							Authorization authorization = (Authorization) annotation;
							processAuthorizationAnnotation(authorization, fieldValue);
						} else if (annotation instanceof Authorizations) {
							Authorizations authorizations = (Authorizations) annotation;
							for(Authorization authorization: authorizations.value()) {
								processAuthorizationAnnotation(authorization, fieldValue);
							}
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void processAuthorizationAnnotation(Authorization authorization, String fieldValue) {
		try {
			Endpoint endpoint = new Endpoint(authorization.feature(),
					authorization.action(), authorization.isPublic(),
					authorization.isPublicForAuthorizedUser(),
					authorization.isPublicButOptionalAuthorizedUser(), fieldValue,
					authorization.httpMethods());
			addEndpoint(endpoint);
			addFeatureAndActions(endpoint);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
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
	public Map<AuthorizedFeature, Set<AuthorizedAction>> getAuthorizedFeatureActions(List<Long> roleIds) throws JsonProcessingException {
		return roleService.getAuthorizedFeatureActionsForGivenRoleIds(roleIds);
	}

	private void throwHttpRequestDoesNotMatchAnyEndpoint(HttpMethod method, String bestMatchPattern) {
		String errMsg = method + " : " + bestMatchPattern + " (No Endpoint Found)";
		throw new RuntimeException(errMsg);
	}
}

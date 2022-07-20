package com.webrest.common.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.webrest.common.annotation.Authorization;
import com.webrest.common.enums.authorization.AuthorizedAction;
import com.webrest.common.enums.authorization.AuthorizedFeature;
import com.webrest.web.constants.WebEndpoint;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthorizationService {

	@Getter
	@AllArgsConstructor
	private class Endpoint {
		private final AuthorizedFeature feature;
		private final AuthorizedAction action;
		private final boolean isPublic;
		private final boolean isPublicForAuthorizedUser;
		private final String path;
		private final HttpMethod[] methods;
	}

	// TODO: Needed to be stored into the redis
	private Map<HttpMethod, Map<String, Endpoint>> endpointByVerbAndPath = new HashMap<>();
	private Map<AuthorizedFeature, List<AuthorizedAction>> featuresWithActions = new HashMap<>();

	@PostConstruct
	public void postConstruct() {
		collectEndpoints();
	}

	private void collectEndpoints() {
		Field[] fields = WebEndpoint.class.getDeclaredFields();
		for (int index = 0; index < fields.length; index++) {
			Field field = fields[index];

			Annotation[] annotations = field.getAnnotations();
			for(int index2 = 0; index2 < annotations.length; index2++) {
				Annotation annotation = annotations[index2];
				if(annotation instanceof Authorization) {
					Authorization authorization = (Authorization) annotation;
					try {
						Object value = field.get(WebEndpoint.class);
						if(value instanceof String) {
							String fieldValue = (String) value;
							Endpoint endpoint = new Endpoint(authorization.feature(), authorization.action(), authorization.isPublic(), authorization.isPublicForAuthorizedUser(), fieldValue, authorization.httpMethods());
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
		List<AuthorizedAction> actions = featuresWithActions.get(endpoint.getFeature());

		if(actions == null) {
			actions = new ArrayList<AuthorizedAction>();
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
}

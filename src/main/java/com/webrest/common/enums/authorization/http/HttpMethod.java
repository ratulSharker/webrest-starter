package com.webrest.common.enums.authorization.http;

public enum HttpMethod {
	GET,
	HEAD,
	POST,
	PUT,
	PATCH,
	DELETE,
	OPTIONS,
	TRACE;

	public org.springframework.http.HttpMethod getSpringHttpMethod() {
		switch (this) {
			case DELETE:
				return org.springframework.http.HttpMethod.DELETE;
			case GET:
				return org.springframework.http.HttpMethod.GET;
			case HEAD:
				return org.springframework.http.HttpMethod.HEAD;
			case OPTIONS:
				return org.springframework.http.HttpMethod.OPTIONS;
			case PATCH:
				return org.springframework.http.HttpMethod.PATCH;
			case POST:
				return org.springframework.http.HttpMethod.POST;
			case PUT:
				return org.springframework.http.HttpMethod.PUT;
			case TRACE:
				return org.springframework.http.HttpMethod.TRACE;
			default:
				return null;

		}
	}
}


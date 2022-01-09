package com.webrest.common.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.webrest.common.interceptor.AuthorizationInterceptor;
import com.webrest.rest.constants.RestEndpoint;
import com.webrest.web.constants.WebEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private AuthorizationInterceptor authorizationInterceptor;

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper mapper = converter.getObjectMapper();
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		mapper.registerModule(hibernate5Module);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
		return converter;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> publicRoutePatterns = new ArrayList<String>();
		publicRoutePatterns.addAll(RestEndpoint.PUBLIC_ROUTE_PATTERNS);
		publicRoutePatterns.addAll(WebEndpoint.PUBLIC_ROUTE_PATTERNS);

		registry.addInterceptor(authorizationInterceptor).excludePathPatterns(publicRoutePatterns);
	}
}

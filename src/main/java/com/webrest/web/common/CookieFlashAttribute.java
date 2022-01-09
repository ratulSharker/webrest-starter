package com.webrest.web.common;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

@Component
public class CookieFlashAttribute {

	private final String COOKIE_FLASH_ATTRIBUTE_KEY = "cookie.flash.attribute";
	private final String ENCODING = "utf-8";

	private final ObjectMapper objectMapper;

	public CookieFlashAttribute() {
		objectMapper = new ObjectMapper();
	}

	public void setValues(Map<String, Object> values, HttpServletResponse response) throws Exception {
		String valuesJSON = objectMapper.writeValueAsString(values);

		Cookie cookie = new Cookie(COOKIE_FLASH_ATTRIBUTE_KEY, URLEncoder.encode(valuesJSON, ENCODING));
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public Optional<Map<String, Object>> getValues(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Optional<Cookie> optionalCookie = Arrays.stream(request.getCookies())
				.filter(c -> c.getName().equals(COOKIE_FLASH_ATTRIBUTE_KEY)).findAny();

		if (optionalCookie.isEmpty()) {
			return Optional.empty();
		}

		Cookie cookie = optionalCookie.get();
		String valuesJSON = URLDecoder.decode(cookie.getValue(), ENCODING);

		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		Map<String, Object> values = objectMapper.readValue(valuesJSON, new TypeReference<Map<String, Object>>() {

		});

		return Optional.of(values);
	}
}

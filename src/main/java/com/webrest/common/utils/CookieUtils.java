package com.webrest.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {

	public static final String AUTHORIZATION = "authorization";
	public static final String LAST_SELECTED_MENU_KEY = "last-selected-menu-key";

	public static boolean setAuthorization(String token, HttpServletRequest request, HttpServletResponse response,
			int maxAge) {
		return set(AUTHORIZATION, token, response, request.getContextPath(), maxAge);
	}

	public static String getAuthorization(HttpServletRequest request) {
		Optional<Cookie> cookie = get(AUTHORIZATION, request);
		if (cookie.isPresent()) {
			return cookie.get().getValue();
		} else {
			return null;
		}
	}

	public static void clearAuthorization(HttpServletRequest request, HttpServletResponse response) {
		clearCookie(AUTHORIZATION, request, response);
	}

	public static boolean setLastSelectedMenu(String lastSelectedMenuPath, HttpServletRequest request,
			HttpServletResponse response, int maxAge) {

		String contextPath = request.getContextPath();
		String lastSelectedMenuPathWithContext = contextPath + lastSelectedMenuPath;

		return set(LAST_SELECTED_MENU_KEY, lastSelectedMenuPathWithContext, response, contextPath, maxAge);
	}

	public static String getLastSelectedMenu(HttpServletRequest request) {
		Optional<Cookie> cookie = get(LAST_SELECTED_MENU_KEY, request);
		if (cookie.isPresent()) {
			return cookie.get().getValue();
		} else {
			return null;
		}
	}

	public static void clearLastSelectedMenu(HttpServletRequest request, HttpServletResponse response) {
		clearCookie(LAST_SELECTED_MENU_KEY, request, response);
	}

	private static boolean set(String name, String value, HttpServletResponse response, String contextPath, int maxAge) {
		if (StringUtils.isNotEmpty(value)) {
			Cookie cookie = new Cookie(name, value);
			cookie.setPath(contextPath);
			cookie.setMaxAge(maxAge);
			response.addCookie(cookie);
			return true;
		} else {
			return false;
		}
	}

	private static Optional<Cookie> get(String cookieName, HttpServletRequest request) {
		if (request == null)
			return Optional.<Cookie>empty();

		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return Optional.<Cookie>empty();

		return Arrays.stream(cookies).filter(c -> cookieName.equals(c.getName())).findAny();
	}

	private static void clearCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
		Optional<Cookie> cookie = get(cookieName, request);
		if (cookie.isPresent()) {
			cookie.get().setMaxAge(0);
			cookie.get().setValue(null);
			cookie.get().setPath(request.getContextPath());

			response.addCookie(cookie.get());
		}
	}
}

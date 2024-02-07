package com.webrest.web.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class CookieFlashAttribute {

	private final String COOKIE_FLASH_ATTRIBUTE_KEY = "cookie.flash.attribute";
	private final String ENCODING = "utf-8";
	
	private static final String ALERT_SUCCESS_KEY = "alert-success-key";
	private static final String ALERT_TITLE_KEY = "alert-title-key";
	private static final String ALERT_DETAILS_KEY = "alert-details-key";

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

	public void setAlertValues(boolean success, String title, String details,
			HttpServletResponse response) throws Exception {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(ALERT_SUCCESS_KEY, success);
		values.put(ALERT_TITLE_KEY, title);
		values.put(ALERT_DETAILS_KEY, details);
		setValues(values, response);
	}

	public void getValuesAndAddAlertModel(Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			Optional<Map<String, Object>> optionalValues = getValues(request, response);

			if (optionalValues.isPresent()) {
				boolean isSuccess = (boolean) optionalValues.get().get(ALERT_SUCCESS_KEY);
				String title = (String) optionalValues.get().get(ALERT_TITLE_KEY);
				String details = (String) optionalValues.get().get(ALERT_DETAILS_KEY);

				if(isSuccess) {
					Alert.addSuccessAlertAttributeToModel(title, details, model);
				} else {
					Alert.addFailureAlertAttributeToModel(title, details, model);
				}
			}

		} catch (Exception ex) {
			log.error("Cookie flash attr de serialization error", ex);
		}
	}
}

package com.webrest.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class HashUtils {
	public static String hashPassword(String rawPassword) {
		return DigestUtils.sha256Hex(rawPassword);
	}
}

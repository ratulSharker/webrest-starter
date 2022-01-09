package com.webrest.common.exception;

public class AppUserAlreadyExistsException extends RuntimeException {

	public AppUserAlreadyExistsException(String message) {
		super(message);
	}
}

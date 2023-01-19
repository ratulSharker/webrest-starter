package com.webrest.web.common;

import javax.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

@ControllerAdvice(basePackages = "com.webrest.web")
public class WebExceptionHandler {
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ModelAndView handleNotFoundException() throws ModelAndViewDefiningException {
		ModelAndView modelAndView = new ModelAndView("features/misc/access-denied");
		throw new ModelAndViewDefiningException(modelAndView);
	}
}

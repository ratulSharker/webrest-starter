package com.webrest.rest;

import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.webrest.common.annotation.ValidJson;
import com.webrest.common.dto.response.ErrorResponse;
import com.webrest.common.dto.response.Metadata;
import com.webrest.common.dto.response.Response;
import com.webrest.common.exception.AppUserAlreadyExistsException;
import com.webrest.common.exception.FileNotFoundException;
import com.webrest.common.exception.FileUploadOrMoveException;
import com.webrest.common.exception.InvalidCredentialsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "com.webrest.rest")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public Response<Void> error(EntityNotFoundException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public Response<Void> error(InvalidCredentialsException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(AppUserAlreadyExistsException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public Response<Void> error(AppUserAlreadyExistsException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(FileUploadOrMoveException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public Response<Void> fileUploadOrMoveError(FileUploadOrMoveException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public Response<Void> fileNotFoundError(FileNotFoundException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public Response<Void> methodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(EntityExistsException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public Response<Void> entityExists(EntityExistsException ex) {
		return buildErrorResponse(ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public Response<Void> genericRuntimeException(RuntimeException ex) {

		logger.error("Unexpected runtime error", ex);

		if (ex.getMessage() != null) {
			return buildErrorResponse(ex.getMessage());
		} else if (ex.getLocalizedMessage() != null) {
			return buildErrorResponse(ex.getLocalizedMessage());
		} else {
			return buildErrorResponse(ex.toString());
		}
	}

	@Override
	protected ResponseEntity<java.lang.Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, org.springframework.http.HttpStatus status,
			org.springframework.web.context.request.WebRequest request) {

		String errorMsg = null;

		if (ex.getMostSpecificCause() instanceof InvalidFormatException) {
			InvalidFormatException ifex = (InvalidFormatException) ex.getMostSpecificCause();
			if (CollectionUtils.isEmpty(ifex.getPath()) == false) {
				Class<?> targetClass = ifex.getPath().get(0).getFrom().getClass();
				String fieldName = ifex.getPath().get(0).getFieldName();
				Optional<ValidJson> validJson = com.webrest.common.utils.AnnotationProcessor.readAnnotation(targetClass,
						fieldName, ValidJson.class);

				if (validJson.isPresent()) {
					errorMsg = validJson.get().message();
				}
			}
		}

		if (errorMsg == null) {
			errorMsg = ex.getMessage();
		}

		return new ResponseEntity<Object>(buildErrorResponse(errorMsg), HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String message = ex.getBindingResult().getAllErrors().stream().map(error -> error.getDefaultMessage())
				.collect(Collectors.joining(", "));
		return new ResponseEntity<>(buildErrorResponse(message), status);
	}

	private Response<Void> buildErrorResponse(String errMsg) {
		ErrorResponse error = ErrorResponse.builder().message(errMsg).build();
		Metadata meta = Metadata.builder().error(error).build();
		return Response.<Void>builder().metadata(meta).build();
	}

}

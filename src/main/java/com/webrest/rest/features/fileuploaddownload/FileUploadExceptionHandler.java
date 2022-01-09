package com.webrest.rest.features.fileuploaddownload;

import com.webrest.common.dto.response.ErrorResponse;
import com.webrest.common.dto.response.Metadata;
import com.webrest.common.dto.response.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionHandler {

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Response<Void>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
		return new ResponseEntity<Response<Void>>(buildErrorResponse("File too big!"), HttpStatus.BAD_REQUEST);
	}

	private Response<Void> buildErrorResponse(String errMsg) {
		ErrorResponse error = ErrorResponse.builder().message(errMsg).build();
		Metadata meta = Metadata.builder().error(error).build();
		return Response.<Void>builder().metadata(meta).build();
	}
}

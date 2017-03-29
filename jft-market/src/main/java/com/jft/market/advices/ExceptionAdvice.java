package com.jft.market.advices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jft.market.api.ws.ErrorWS;
import com.jft.market.bindings.ErrorResource;
import com.jft.market.bindings.FieldErrorResource;
import com.jft.market.exceptions.EntityNotFoundException;
import com.jft.market.exceptions.InvalidRequestException;

@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler({InvalidRequestException.class})
	protected ResponseEntity<Object> handleInvalidRequest(RuntimeException e, WebRequest request) {
		InvalidRequestException invalidRequestException = (InvalidRequestException) e;
		List<FieldErrorResource> fieldErrorResources = new ArrayList<>();
		ErrorResource errorResource = new ErrorResource("Invalid Request");
		List<FieldError> fieldErrors = invalidRequestException.getErrors().getFieldErrors();
		fieldErrors.forEach(fieldError -> {
			FieldErrorResource fieldErrorResource = new FieldErrorResource();
			fieldErrorResource.setCode(fieldError.getCode());
			fieldErrorResource.setField(fieldError.getField());
			fieldErrorResource.setMessage(fieldError.getDefaultMessage());
			fieldErrorResources.add(fieldErrorResource);
			errorResource.setFieldErrors(fieldErrorResources);
		});
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return handleExceptionInternal(e, errorResource, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	@ExceptionHandler({EntityNotFoundException.class})
	protected ResponseEntity<Object> entityNotFound(RuntimeException e, WebRequest request) {
		EntityNotFoundException entityNotFoundException = (EntityNotFoundException) e;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity(new ErrorWS(entityNotFoundException.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}
}

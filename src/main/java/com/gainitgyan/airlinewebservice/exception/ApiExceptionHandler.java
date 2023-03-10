package com.gainitgyan.airlinewebservice.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "com.gainitgyan.airlinewebservice")
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<String> errors = ex.getBindingResult().getAllErrors().stream().map(e->e.getDefaultMessage()).collect(Collectors.toList());
		
		HttpServletRequest req = ((ServletWebRequest)request).getRequest();
		ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
				.withErrorId("Airline-"+ThreadContext.get("requestid"))
				.forPath(req.getRequestURI())
				.withErrors(errors)
				.withMessage(ex.getMessage())
				.withStatus(status.value())
				.build();
				
		
		return new ResponseEntity<Object>(apiError,headers,status);
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request){
		
		List<String> errors = Arrays.asList(ex.getReason());
		
		ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
				.withErrorId("Airline-"+ThreadContext.get("requestid"))
				.forPath(request.getRequestURI())
				.withErrors(errors)
				.withMessage(ex.getMessage())
				.withStatus(ex.getStatus().value())
				.build();
		
		return new ResponseEntity<ApiErrorResponse>(apiError,ex.getStatus());
	}
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request){
		
		List<String> errors = new ArrayList<>();
		for(ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getMessage());
		}
		
		ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
				.withErrorId("Airline-"+ThreadContext.get("requestid"))
				.forPath(request.getRequestURI())
				.withErrors(errors)
				.withMessage(ex.getMessage())
				.withStatus(HttpStatus.BAD_REQUEST.value())
				.build();
		
		return new ResponseEntity<ApiErrorResponse>(apiError,HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleException(Exception ex , HttpServletRequest request){
		
		List<String> errors = Arrays.asList(ex.getMessage());
		
		
		ApiErrorResponse  apiError = ApiErrorResponseBuilder.getInstance()
				.withErrorId("Airline-"+ThreadContext.get("requestid"))
				.forPath(request.getRequestURI())
				.withErrors(errors)
				.withMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.build();
		
		return new ResponseEntity<ApiErrorResponse>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest req) {
		HttpServletRequest request = ((ServletWebRequest) req).getRequest();
		List<String> errors = Arrays.asList(ex.getMessage());

		ApiErrorResponse apiError = ApiErrorResponseBuilder.getInstance()
				.withErrorId("Airline-" + ThreadContext.get("requestid"))
				.forPath(request.getRequestURI())
				.withErrors(errors)
				.withMessage(ex.getMessage())
				.withStatus(status.value())
				.build();

		return new ResponseEntity<Object>(apiError, status);
	}
	
}

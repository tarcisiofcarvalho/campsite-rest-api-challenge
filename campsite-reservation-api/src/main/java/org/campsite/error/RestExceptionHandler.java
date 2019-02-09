package org.campsite.error;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{
    public RestExceptionHandler() {
        super();
    }

    
    /**
     * This is a custom handler related to max reservation days exception
     */
    @ExceptionHandler(value = MaxReservationDaysException.class)
    public ResponseEntity<Object> handleMaxReservationDays(MaxReservationDaysException ex) {
    	ErrorMessage err = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "You are request exceed the limit max 3 days of reservation. Please adjust your request", "Max Reservation Days Exception");
    	return new ResponseEntity<>(err, HttpStatus.NOT_ACCEPTABLE);
    }     
    
    /**
     * This is a custom handler related to when the request is out of time limit permitted 
     */
    @ExceptionHandler(value = RequestReservationTimeException.class)
    public ResponseEntity<Object> handleRequestReservationTime(RequestReservationTimeException ex) {
    	ErrorMessage err = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance. Please adjust your request", "Reservation Request Time Limited Exception");
    	return new ResponseEntity<>(err, HttpStatus.NOT_ACCEPTABLE);
    }  

    /**
    * This is a custom handler related to when the request is out of time limit permitted 
    */
   @ExceptionHandler(value = ReservationDatesNotAvailableException.class)
   public ResponseEntity<Object> handleReservationDaysNotAvailable(ReservationDatesNotAvailableException ex) {
   	ErrorMessage err = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "The date(s) : " + ex.getMessage() + " are not available for your reservation. Please adjust your request", "Reservation Dates not Available Exception");
   	return new ResponseEntity<>(err, HttpStatus.NOT_ACCEPTABLE);
   }      
    
    // API

    // 400

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
        final String bodyOfResponse = "Data Integrity Error. Please check if all JSON field were filled correctly";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    
/*    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<Object> handleNotValidArgument(final MethodArgumentNotValidException ex, final WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
    	
        ErrorMessage apiError = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    } */   

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String bodyOfResponse = "There is something wrong with the structure of JSON passed, please review it.";
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String bodyOfResponse = "It is missing some json field or the field name is incorrect";
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

/*    // 403
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request) {
        System.out.println("request" + request.getUserPrincipal());
        return new ResponseEntity<Object>("Access denied message here", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }*/

    // 409

    @ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 412

    // 500

    @ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
    /*500*/public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}

package com.ij.polizario.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    private static final String ERROR_EXCEPTION_TEMPLATE = "Error [{}]";
    private final YpExceptionMessage exceptionMessage;

    public GlobalDefaultExceptionHandler(YpExceptionMessage exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * Handle general uncaught exceptions
     *
     * @param ex - Exception
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<YpExceptionResponse> defaultErrorHandler(Exception ex) {
        log.error("An unhandled exception has occurred", ex);

        YpExceptionResponse exceptionResponse = new YpExceptionResponse(BusinessExceptionEnum.SERVER_ERROR.getValue(), "");
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<YpExceptionResponse> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException ex) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ex.getMessage());

        String message = exceptionMessage.getMessage(BusinessExceptionEnum.HTTP_MESSAGE_NOT_READABLE.getValue());

        YpExceptionResponse exceptionResponse = new YpExceptionResponse(BusinessExceptionEnum.REQUEST_INPUT_ERROR.getValue(),
            null, message);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle ServletRequestBindingException
     *
     * @param ex - Exception
     */
    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<YpExceptionResponse> handleServletRequestBindingException(ServletRequestBindingException ex) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ex.getMessage());

        return new ResponseEntity<>(new YpExceptionResponse(BusinessExceptionEnum.REQUEST_INPUT_ERROR.getValue(), null,
            ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle HttpMediaTypeNotSupportedExceptions
     *
     * @param ex - Exception
     */
    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<YpExceptionResponse> handleHttpMediaTypeNotSupportedException(
        HttpMediaTypeNotSupportedException ex) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ex.getMessage());

        String unsupported = "Unsupported content type: " + ex.getContentType();

        return new ResponseEntity<>(new YpExceptionResponse(BusinessExceptionEnum.REQUEST_INPUT_ERROR.getValue(), null,
            unsupported), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Handle ConstraintViolationExceptions
     *
     * @param ce - Exception
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<YpExceptionResponse> ypConstraintViolationHandler(ConstraintViolationException ce) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ce.getMessage());
        YpExceptionResponse exceptionResponse = new YpExceptionResponse(BusinessExceptionEnum.REQUEST_INPUT_ERROR.getValue(),
            ce.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MethodArgumentNotValidException
     *
     * @param ex - Exception
     */
    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<YpExceptionResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ex.getMessage());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        String error;
        for (FieldError fieldError : fieldErrors) {
            error = fieldError.getField() + ", " + fieldError.getDefaultMessage();
            errors.add(error);
        }
        for (ObjectError objectError : globalErrors) {
            error = objectError.getObjectName() + ", " + objectError.getDefaultMessage();
            errors.add(error);
        }

        return new ResponseEntity<>(
            new YpExceptionResponse(BusinessExceptionEnum.REQUEST_INPUT_ERROR.getValue(), null, errors),
            HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle YpExceptions
     *
     * @param ype The YP exception {@link BusinessException}
     */
    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<YpExceptionResponse> ypExceptionHandler(BusinessException ype) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ype.getMessage());

        String message = exceptionMessage.getMessage(ype.getErrorCode());
        YpExceptionResponse exceptionResponse = new YpExceptionResponse(ype.getErrorCode(), null, message);

        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle YpNotFoundException
     *
     * @param ype The YP exception {@link YpNotFoundException}
     */
    @ExceptionHandler(YpNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<YpExceptionResponse> ypNotFoundExceptionHandler(YpNotFoundException ype) {
        log.error(ERROR_EXCEPTION_TEMPLATE, ype.getMessage());

        String message = exceptionMessage.getMessage(ype.getErrorCode());
        YpExceptionResponse exceptionResponse = new YpExceptionResponse(ype.getErrorCode(), null, message);

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

}
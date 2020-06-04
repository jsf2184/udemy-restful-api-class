package com.jefff.app.restful.web.exceptions;

import com.jefff.app.restful.web.ui.controller.UserController;
import com.jefff.app.restful.web.ui.model.response.ErrorMessage;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

// Create a class that gets control when an exception is thrown.
@ControllerAdvice
public class AppExceptionsHandler {
    public static Logger _log = LoggerFactory.getLogger(AppExceptionsHandler.class) ;

    // Annotate the method indicating what exceptions the class handles.
    @ExceptionHandler(value = {UserServiceException.class})
    public ResponseEntity<ErrorMessage> handleUserServiceException(UserServiceException ex,
                                                             WebRequest webRequest
                                                             )
    {
        String message = ex.getMessage();
        _log.warn(String.format("AppExceptionsHandler.handleUserServiceException(): caught Exception: %s", message));

        ErrorMessage errorMessage = new ErrorMessage(new Date(), message);
        // build the response that will be sent back to the client.
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MalformedJwtException.class})
    public ResponseEntity<ErrorMessage> handleJwtException(MalformedJwtException ex,
                                                          WebRequest webRequest
    )
    {
        String message = ex.getMessage();
        _log.warn(String.format("AppExceptionsHandler.handleJwtException(): caught Exception: %s", message));

        ErrorMessage errorMessage = new ErrorMessage(new Date(), message);
        // build the response that will be sent back to the client.
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    // Catch all other exceptions here.
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> handleOtherExceptions(Exception ex,
                                                              WebRequest webRequest
    )
    {
        String message = ex.getMessage();
        _log.warn(String.format("AppExceptionsHandler.handleOtherExceptions(): caught Exception: %s", message));

        ErrorMessage errorMessage = new ErrorMessage(new Date(), message);
        // build the response that will be sent back to the client.
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

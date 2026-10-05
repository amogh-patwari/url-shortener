package com.Amogh.url_shortener.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


//The class annotated with @ContollerAdvice: "this class handles exceptions thrown from any controller in the app, in one centralized place
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidShortCodeException.class)
    public ResponseEntity<?> handleInvalidShortCode(InvalidShortCodeException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<?> handleUrlNotFound(UrlNotFoundException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

}

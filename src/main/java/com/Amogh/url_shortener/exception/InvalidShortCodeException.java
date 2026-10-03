package com.Amogh.url_shortener.exception;

public class InvalidShortCodeException extends RuntimeException{
    public InvalidShortCodeException(String message){
        super(message);
    }
}

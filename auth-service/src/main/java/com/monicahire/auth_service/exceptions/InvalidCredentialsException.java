package com.monicahire.auth_service.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException{
    public InvalidCredentialsException(String message){
        super (message,HttpStatus.UNAUTHORIZED);
    }

}

package com.substring.auth.auth_app.exception;

import jakarta.annotation.Resource;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Super;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String string){
        super(string);
    }

    public ResourceNotFoundException(){
        super("Resource not found");
    }
}

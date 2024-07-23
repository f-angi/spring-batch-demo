package org.example.springbatchdemo.exception;

public class CustomRuntimeException extends RuntimeException {

//    private String message;

    public CustomRuntimeException() {
    }

    public CustomRuntimeException(String message) {
        super(message);
//        this.message = message;
    }

}

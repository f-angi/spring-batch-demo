package org.example.springbatchdemo.exception;

public class TimeExpiredException extends Exception {

    public TimeExpiredException() {
    }

    public TimeExpiredException(String message) {
        super(message);
    }

}

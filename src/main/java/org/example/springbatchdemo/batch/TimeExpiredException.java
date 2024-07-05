package org.example.springbatchdemo.batch;

public class TimeExpiredException extends Exception {

    public TimeExpiredException() {
    }

    public TimeExpiredException(String message) {
        super(message);
    }

}

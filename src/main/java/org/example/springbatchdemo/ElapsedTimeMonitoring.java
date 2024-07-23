package org.example.springbatchdemo;

import org.example.springbatchdemo.exception.TimeExpiredException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ElapsedTimeMonitoring {

    public static final long MAX_ALLOWED_TIME_MS = 30_000;

    private long t;

    public ElapsedTimeMonitoring() {
        t = Instant.now().toEpochMilli();
    }

    public void increaseTime() throws TimeExpiredException {
        var t1 = Instant.now().toEpochMilli() - t;
        if (t1 > MAX_ALLOWED_TIME_MS) {
            throw new TimeExpiredException();
        }
    }

}

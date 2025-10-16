package org.example.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public class UserWindow {
    final AtomicInteger count = new AtomicInteger(0);
    long windowStart;

    public UserWindow(long windowStart) {
        this.windowStart = windowStart;
    }
}

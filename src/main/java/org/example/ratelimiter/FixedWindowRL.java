package org.example.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRL implements RateLimiter {
    private final long maxRequests; // Bucket size
    private final long windowSizeMillis; // Window size

    private Map<String, UserWindow> userWindows = new ConcurrentHashMap<>();

    public FixedWindowRL(long maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();

        UserWindow userWindow = userWindows.computeIfAbsent(userId, k -> new UserWindow(currentTime));

        synchronized (userWindow) {
            if(currentTime - userWindow.windowStart >= windowSizeMillis) {
                userWindow.count.set(1);
                userWindow.windowStart = currentTime;
                return true;
            }else{
                int currentCount = userWindow.count.incrementAndGet();
                return currentCount <= maxRequests;
            }
        }
    }
}

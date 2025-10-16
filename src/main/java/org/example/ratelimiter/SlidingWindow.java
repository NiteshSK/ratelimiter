package org.example.ratelimiter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindow implements RateLimiter {
    private final long maxRequests;
    private final long windowSizeMillis;
    private Map<String, Deque<Long>> requestLogs = new ConcurrentHashMap<>();

    public SlidingWindow(long maxRequests,  long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();

        // Get or create the user's log
        Deque<Long> timestamps = requestLogs.computeIfAbsent(userId, k -> new LinkedList<>());


        // synchronized only the USER's specific log objects
        synchronized (timestamps) {

            while (!timestamps.isEmpty() && currentTime - timestamps.peekFirst() >= windowSizeMillis) {
                //remove the expired timestamps
                timestamps.pollFirst();
            }

            if(timestamps.size() < maxRequests) {
                timestamps.addLast(currentTime);
                //accepted
                return  true;
            }
        }

        //Deny
        return false;
    }
}

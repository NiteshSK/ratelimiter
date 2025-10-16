package org.example.ratelimiter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LeakyBucket implements RateLimiter {

    private final long capacity;              // Max requests allowed in the queue (Bucket size)
    private final long leakIntervalMs;        // The fixed time interval between allowed requests (IAT/LI)
    private final Map<String, Deque<Long>> userBuckets = new ConcurrentHashMap<>();

    public LeakyBucket(int maxRatePerSecond, int burstCapacity) {
        this.capacity = burstCapacity;

        if (maxRatePerSecond <= 0) {
            throw new IllegalArgumentException("Rate must be greater than zero.");
        }
        // LI = 1000ms / Rate (e.g., 5 req/sec -> 200ms Leak Interval)
        this.leakIntervalMs = TimeUnit.SECONDS.toMillis(1) / maxRatePerSecond;
    }


    @Override
    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();

        // Get or create the user's bucket (log of timestamps)
        Deque<Long> bucket = userBuckets.computeIfAbsent(userId, k -> new LinkedList<>());

        // Synchronize only the USER's specific bucket
        synchronized (bucket) {

            // 1. Pruning (Simulate Leak): Remove requests older than the Leak Interval
            // The concept here is: if the time since the oldest request is greater than
            // the time needed to process one request (LI), that request has "leaked out".
            while (!bucket.isEmpty() && (currentTime - bucket.peekFirst() >= leakIntervalMs)) {
                bucket.pollFirst();
            }

            // 2. Capacity Check (Overflow): Check if the queue/bucket is full
            if(bucket.size() < capacity) {
                bucket.addLast(currentTime);
                // accepted
                return  true;
            }
        }

        // Deny (Bucket Overflow)
        return false;
    }
}

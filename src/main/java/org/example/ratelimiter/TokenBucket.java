package org.example.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucket implements RateLimiter {
    private final int capacity;
    private final double refillRate;
    private Map<String, UserBucket> userBuckets = new ConcurrentHashMap<>();

    public TokenBucket(int capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }


    @Override
    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();

        UserBucket userBucket = userBuckets.computeIfAbsent(userId, k -> new UserBucket(capacity, currentTime));

        synchronized (userBucket) {
            refill(userBucket, currentTime);

            if(userBucket.currentTokens >= 1.0){
                userBucket.currentTokens -= 1.0;
                return true;
            }

        }

        return false;
    }

    private void refill (UserBucket userBucket, long currentTime) {

        long elapsedTime = currentTime - userBucket.lastFillTimestamps;

        if(elapsedTime > 0){
            double tokensToAdd = elapsedTime * (refillRate / 1000.0);
            userBucket.currentTokens = Math.min(this.capacity, userBucket.currentTokens + tokensToAdd);
            userBucket.lastFillTimestamps = currentTime;
        }


    }
}

package org.example.ratelimiter;

public class UserBucket {
    double currentTokens;
    long lastFillTimestamps;

    public UserBucket(int intialCapacity, long currentTime) {
        this.currentTokens = intialCapacity;
        this.lastFillTimestamps =  currentTime;
    }


}

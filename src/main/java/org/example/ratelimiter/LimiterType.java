package org.example.ratelimiter;

public enum LimiterType {

    FIXED_WINDOW,
    SLIDING_LOG,
    TOKEN_BUCKET,
    LEAKY_BUCKET_TIMESTAMP,
    LEAKY_BUCKET_QUEUE

}

package org.example.ratelimiter;

class RateLimiterFactory {
    public static RateLimiter createLimiter(LimiterType type, long param1, long param2) {
        switch (type) {
            case FIXED_WINDOW:
                // param1: maxRequests, param2: windowSizeMillis
                return new FixedWindowRL(param1, param2);
            case SLIDING_LOG:
                // param1: maxRequests, param2: windowSizeMillis
                return new SlidingWindow(param1, param2);
            case TOKEN_BUCKET:
                // param1: capacity (int), param2: refillRate (double tokens/sec)
                // Casting the long parameters to required types for the constructors
                return new TokenBucket((int) param1, (double) param2);
            case LEAKY_BUCKET_QUEUE:
                // param1: burstCapacity (int), param2: maxRatePerSecond (int)
                return new LeakyBucket((int) param2, (int) param1);
            default:
                throw new IllegalArgumentException("Unknown limiter type: " + type);
        }
    }
}

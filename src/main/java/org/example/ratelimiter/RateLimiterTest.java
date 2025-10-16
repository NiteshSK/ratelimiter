package org.example.ratelimiter;


public class RateLimiterTest {
    private static final String USER_ID = "testUser123";

    private static void printResult(String limiterName, int reqNum, boolean allowed, long currentTime) {
        System.out.printf("[%s] Req %02d @ %dms: %s\n",
                limiterName, reqNum, currentTime % 10000, allowed ? "✅ ALLOWED" : "❌ DENIED");
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=================================================");
        System.out.println("  Rate Limiter Algorithm Validation");
        System.out.println("=================================================");

        // --- Test 1: Fixed Window Rate Limiter (5 requests per 1000ms) ---
        testFixedWindow();

        // --- Test 2: Sliding Log Rate Limiter (5 requests per 1000ms) ---
        testSlidingLog();

        // --- Test 3: Token Bucket Rate Limiter (5 tokens/sec, capacity 5) ---
        testTokenBucket();

        testLeakyBucketQueue();
    }

    // Test Scenario for Fixed Window
    private static void testFixedWindow() throws InterruptedException {
        System.out.println("\n--- 1. Fixed Window RL (5 reqs / 1000ms) ---");
        final long MAX_REQS = 5;
        final long WINDOW_MS = 1000;
        RateLimiter fixedLimiter = new FixedWindowRL(MAX_REQS, WINDOW_MS);

        // A. Test Burst (5 allowed, 6th denied)
        System.out.println("  -> Testing Burst (Window 1)");
        for (int i = 1; i <= 6; i++) {
            boolean allowed = fixedLimiter.allowRequest(USER_ID);
            printResult("Fixed", i, allowed, System.currentTimeMillis());
            // Expect: 1-5 allowed, 6 denied
        }

        // B. Wait half a window (500ms) - Should still be denied
        System.out.println("  -> Waiting 500ms (Still denied)");
        Thread.sleep(500);
        boolean allowedAfterHalfWindow = fixedLimiter.allowRequest(USER_ID);
        printResult("Fixed", 7, allowedAfterHalfWindow, System.currentTimeMillis());
        // Expect: 7 denied

        // C. Wait past the window (another 501ms) - Should reset and be allowed
        System.out.println("  -> Waiting 501ms (Window reset)");
        Thread.sleep(501);
        boolean allowedAfterReset = fixedLimiter.allowRequest(USER_ID);
        printResult("Fixed", 8, allowedAfterReset, System.currentTimeMillis());
        // Expect: 8 allowed (New window started)
    }

    // Test Scenario for Sliding Log
    private static void testSlidingLog() throws InterruptedException {
        System.out.println("\n--- 2. Sliding Log RL (5 reqs / 1000ms) ---");
        final long MAX_REQS = 5;
        final long WINDOW_MS = 1000;
        RateLimiter slidingLimiter = new SlidingWindow(MAX_REQS, WINDOW_MS);

        // A. Initial 4 requests (T=0)
        System.out.println("  -> Initial 4 requests");
        for (int i = 1; i <= 4; i++) {
            boolean allowed = slidingLimiter.allowRequest(USER_ID);
            printResult("Sliding", i, allowed, System.currentTimeMillis());
            // Expect: 1-4 allowed
        }

        // B. Wait 800ms
        System.out.println("  -> Waiting 800ms (Logs: 0-800)");
        Thread.sleep(800);

        // C. Request 5 (T=800) - Allowed, Log size is 5
        boolean allowedReq5 = slidingLimiter.allowRequest(USER_ID);
        printResult("Sliding", 5, allowedReq5, System.currentTimeMillis());
        // Expect: 5 allowed

        // D. Request 6 (T=800) - Denied, Log size is 5
        boolean deniedReq6 = slidingLimiter.allowRequest(USER_ID);
        printResult("Sliding", 6, deniedReq6, System.currentTimeMillis());
        // Expect: 6 denied

        // E. Wait 201ms (Oldest log (T=0) is now expired at T=1001)
        System.out.println("  -> Waiting 201ms (Oldest log should expire)");
        Thread.sleep(201);

        // F. Request 7 (T=1001) - Allowed, as T=0 log is cleaned up
        boolean allowedReq7 = slidingLimiter.allowRequest(USER_ID);
        printResult("Sliding", 7, allowedReq7, System.currentTimeMillis());
        // Expect: 7 allowed
    }

    // Test Scenario for Token Bucket
    private static void testTokenBucket() throws InterruptedException {
        System.out.println("\n--- 3. Token Bucket RL (5 tokens/sec, Cap 5) ---");
        final int CAPACITY = 5;
        final double REFILL_RATE = 5.0; // 5 tokens per second
        TokenBucket tokenLimiter = new TokenBucket(CAPACITY, REFILL_RATE);

        // A. Test Burst (5 allowed, 6th denied)
        System.out.println("  -> Testing Initial Burst");
        for (int i = 1; i <= 6; i++) {
            boolean allowed = tokenLimiter.allowRequest(USER_ID);
            printResult("Token", i, allowed, System.currentTimeMillis());
            // Expect: 1-5 allowed, 6 denied
        }

        // B. Wait for partial refill (100ms) - Should generate 0.5 tokens. Still denied.
        System.out.println("  -> Waiting 100ms (0.5 tokens added)");
        Thread.sleep(100);
        boolean deniedReq7 = tokenLimiter.allowRequest(USER_ID);
        printResult("Token", 7, deniedReq7, System.currentTimeMillis());
        // Expect: 7 denied (needs 1.0 tokens)

        // C. Wait for full refill (another 100ms) - Total 200ms elapsed. 1.0 tokens generated.
        System.out.println("  -> Waiting another 100ms (Total 200ms elapsed)");
        Thread.sleep(100);
        boolean allowedReq8 = tokenLimiter.allowRequest(USER_ID);
        printResult("Token", 8, allowedReq8, System.currentTimeMillis());
        // Expect: 8 allowed (1 token consumed)

        // D. Wait 1 second (1000ms) - Full bucket refill
        System.out.println("  -> Waiting 1000ms (Full bucket refill)");
        Thread.sleep(1000);

        // E. Burst allowed again
        System.out.println("  -> Testing Burst again (Full bucket)");
        for (int i = 9; i <= 14; i++) {
            boolean allowed = tokenLimiter.allowRequest(USER_ID);
            printResult("Token", i, allowed, System.currentTimeMillis());
            // Expect: 9-13 allowed, 14 denied
        }
    }

    // Test Scenario for Queue-based Leaky Bucket (Bare-Bones)
    private static void testLeakyBucketQueue() throws InterruptedException {
        System.out.println("\n--- 5. Queue-based Leaky Bucket RL (5 reqs/sec, Burst 5) ---");
        final long RATE_PER_SEC = 5;
        final long BURST_CAPACITY = 5;

        // Instantiate using Factory (Note: param1=Capacity, param2=Rate)
        RateLimiter leakyLimiter = RateLimiterFactory.createLimiter(LimiterType.LEAKY_BUCKET_QUEUE, BURST_CAPACITY, RATE_PER_SEC);
        long LI_MS = 1000 / RATE_PER_SEC; // Leak Interval = 200ms

        // A. Initial Burst (5 allowed, 6th denied)
        System.out.println("  -> Testing Initial Burst (Capacity = 5)");
        for (int i = 1; i <= 6; i++) {
            boolean allowed = leakyLimiter.allowRequest(USER_ID);
            printResult("Queue Leaky", i, allowed, System.currentTimeMillis());
            // Expect: 1-5 allowed, 6 denied
        }

        // B. Wait less than LI (e.g., 150ms) - Still denied
        System.out.println("  -> Waiting 150ms (No request leaks yet)");
        Thread.sleep(150);
        boolean deniedReq7 = leakyLimiter.allowRequest(USER_ID);
        printResult("Queue Leaky", 7, deniedReq7, System.currentTimeMillis());
        // Expect: 7 denied

        // C. Wait 51ms more (Total 201ms elapsed) - Should allow 1 request
        System.out.println("  -> Waiting 51ms (Total 201ms elapsed, 1 request leaks)");
        Thread.sleep(51);
        boolean allowedReq8 = leakyLimiter.allowRequest(USER_ID);
        printResult("Queue Leaky", 8, allowedReq8, System.currentTimeMillis());
        // Expect: 8 allowed (Queue size 5 -> Pruned 1 -> Size 4 -> Added 1 -> Size 5)

        // D. Subsequent burst attempt is denied
        System.out.println("  -> Testing new burst (Should be denied again)");
        boolean deniedReq9 = leakyLimiter.allowRequest(USER_ID);
        printResult("Queue Leaky", 9, deniedReq9, System.currentTimeMillis());
        // Expect: 9 denied
    }
}

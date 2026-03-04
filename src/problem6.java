import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class problem6 {

    static class TokenBucket {
        private final long maxTokens;
        private final long refillRatePerSecond;

        private AtomicLong tokens;
        private AtomicLong lastRefillTime;

        public TokenBucket(long maxTokens, long refillRatePerSecond) {
            this.maxTokens = maxTokens;
            this.refillRatePerSecond = refillRatePerSecond;
            this.tokens = new AtomicLong(maxTokens);
            this.lastRefillTime = new AtomicLong(System.currentTimeMillis());
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long lastTime = lastRefillTime.get();
            long secondsPassed = (now - lastTime) / 1000;

            if (secondsPassed > 0) {
                long newTokens = secondsPassed * refillRatePerSecond;
                long updatedTokens = Math.min(maxTokens, tokens.get() + newTokens);

                tokens.set(updatedTokens);
                lastRefillTime.set(now);
            }
        }

        public synchronized String tryConsume() {
            refill();

            if (tokens.get() > 0) {
                long remaining = tokens.decrementAndGet();
                return "Allowed (" + remaining + " requests remaining)";
            } else {
                long now = System.currentTimeMillis();
                long retryAfter = 3600 - ((now - lastRefillTime.get()) / 1000);
                return "Denied (0 requests remaining,untarRetry after " + retryAfter + "s)";
            }
        }

        public String getStatus() {
            long used = maxTokens - tokens.get();
            long resetTime = (lastRefillTime.get() + 3600 * 1000) / 1000;
            return "{used: " + used + ", limit: " + maxTokens +
                    ", reset: " + resetTime + "}";
        }
    }

    private ConcurrentHashMap<String, TokenBucket> clientBuckets;

    public problem6() {
        clientBuckets = new ConcurrentHashMap<>();
    }

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(
                clientId,
                k -> new TokenBucket(1000, 1000 / 3600)
        );

        return bucket.tryConsume();
    }

    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) {
            return "Client not found";
        }
        return bucket.getStatus();
    }

    public static void main(String[] args) {

        problem6 limiter = new problem6();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));

        for (int i = 0; i < 998; i++) {
            limiter.checkRateLimit("abc123");
        }

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}
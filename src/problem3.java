import java.util.*;
import java.util.concurrent.*;

public class problem3 {

    private static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int maxSize;
    private final Map<String, DNSEntry> cache;

    private long hits = 0;
    private long misses = 0;
    private long totalLookupTime = 0;

    public problem3(int maxSize) {
        this.maxSize = maxSize;

        this.cache = Collections.synchronizedMap(
                new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
                    protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                        return size() > problem3.this.maxSize;
                    }
                });

        startCleanupThread();
    }

    public String resolve(String domain) {
        long startTime = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null) {
            if (!entry.isExpired()) {
                hits++;
                long duration = System.nanoTime() - startTime;
                totalLookupTime += duration;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
            }
        }

        misses++;
        String newIP = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, newIP, 5));

        long duration = System.nanoTime() - startTime;
        totalLookupTime += duration;

        return "Cache MISS → Query upstream → " + newIP;
    }

    private String queryUpstreamDNS(String domain) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "172.217.14." + new Random().nextInt(255);
    }

    private void startCleanupThread() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (cache) {
                Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, DNSEntry> entry = iterator.next();
                    if (entry.getValue().isExpired()) {
                        iterator.remove();
                    }
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public String getCacheStats() {
        long totalRequests = hits + misses;
        double hitRate = totalRequests == 0 ? 0 : ((double) hits / totalRequests) * 100;
        double avgLookupTime = totalRequests == 0 ? 0 : (totalLookupTime / totalRequests) / 1_000_000.0;

        return "Hit Rate: " + String.format("%.2f", hitRate) + "%, Avg Lookup Time: "
                + String.format("%.2f", avgLookupTime) + "ms";
    }

    public static void main(String[] args) throws InterruptedException {
        problem3 dnsCache = new problem3(3);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));

        Thread.sleep(6000);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.getCacheStats());
    }
}
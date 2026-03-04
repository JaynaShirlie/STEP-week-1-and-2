import java.util.*;

public class problem10 {

    static class Video {
        String videoId;
        String content;
        int version;

        Video(String id, String content, int version) {
            this.videoId = id;
            this.content = content;
            this.version = version;
        }
    }

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private LRUCache<String, Video> L1;
    private LRUCache<String, Video> L2;
    private Map<String, Video> L3; // Database

    private Map<String, Integer> accessCount;

    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;

    private final int PROMOTION_THRESHOLD = 5;

    public problem10() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();
        accessCount = new HashMap<>();
    }

    // Simulate DB load
    public void loadDatabase(List<Video> videos) {
        for (Video v : videos) {
            L3.put(v.videoId, v);
        }
    }

    public Video getVideo(String videoId) {

        long start = System.nanoTime();

        // L1
        if (L1.containsKey(videoId)) {
            l1Hits++;
            simulateLatency(0.5);
            return L1.get(videoId);
        }

        // L2
        if (L2.containsKey(videoId)) {
            l2Hits++;
            simulateLatency(5);

            Video v = L2.get(videoId);
            incrementAccess(videoId);

            if (accessCount.get(videoId) >= PROMOTION_THRESHOLD) {
                L1.put(videoId, v); // Promote to L1
            }

            return v;
        }

        // L3
        if (L3.containsKey(videoId)) {
            l3Hits++;
            simulateLatency(150);

            Video v = L3.get(videoId);
            incrementAccess(videoId);

            L2.put(videoId, v); // Add to L2
            return v;
        }

        return null;
    }

    private void incrementAccess(String videoId) {
        accessCount.put(videoId,
                accessCount.getOrDefault(videoId, 0) + 1);
    }

    public void invalidate(String videoId) {
        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);
    }

    public void updateVideo(String videoId, String newContent) {
        Video updated = new Video(videoId, newContent, 2);
        L3.put(videoId, updated);
        invalidate(videoId);
    }

    public void getStatistics() {
        int total = l1Hits + l2Hits + l3Hits;

        System.out.println("L1 Hit Rate: " + percentage(l1Hits, total));
        System.out.println("L2 Hit Rate: " + percentage(l2Hits, total));
        System.out.println("L3 Hit(VAR): " + percentage(l3Hits, total));
        System.out.println("Overall Requests: " + total);
    }

    private String percentage(int hits, int total) {
        if (total == 0) return "0%";
        return String.format("%.2f%%",
                (hits * 100.0) / total);
    }

    private void simulateLatency(double millis) {
        try {
            Thread.sleep((long) millis);
        } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {

        problem10 cacheSystem = new problem10();

        List<Video> videos = new ArrayList<>();
        for (int i = 1; i <= 100000; i++) {
            videos.add(new Video("video_" + i,
                    "Content " + i, 1));
        }

        cacheSystem.loadDatabase(videos);

        cacheSystem.getVideo("video_123");
        cacheSystem.getVideo("video_123");
        cacheSystem.getVideo("video_999");
        cacheSystem.getVideo("video_123");

        cacheSystem.getStatistics();
    }
}
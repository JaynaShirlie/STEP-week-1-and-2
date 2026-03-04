import java.util.*;
import java.util.concurrent.*;

public class problem5 {

    private ConcurrentHashMap<String, Integer> pageVisitCount;
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors;
    private ConcurrentHashMap<String, Integer> trafficSourceCount;

    public problem5() {
        pageVisitCount = new ConcurrentHashMap<>();
        uniqueVisitors = new ConcurrentHashMap<>();
        trafficSourceCount = new ConcurrentHashMap<>();
        startDashboardUpdater();
    }

    public void processEvent(String url, String userId, String source) {

        pageVisitCount.merge(url, 1, Integer::sum);

        uniqueVisitors
                .computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet())
                .add(userId);

        trafficSourceCount.merge(source, 1, Integer::sum);
    }

    public void getDashboard() {

        System.out.println("\nTop Pages:");

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageVisitCount.entrySet());

        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();

            System.out.println(rank + ". " + url + " - "
                    + views + " views (" + unique + " unique)");
            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int totalTraffic = trafficSourceCount.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> entry : trafficSourceCount.entrySet()) {
            double percentage = totalTraffic == 0 ? 0 :
                    (double) entry.getValue() / totalTraffic * 100;

            System.out.println(entry.getKey() + ": "
                    + String.format("%.0f", percentage) + "%");
        }
    }

    private void startDashboardUpdater() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            getDashboard();
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {

        problem5 analytics = new problem5();

        analytics.processEvent("/article/breaking-news", "user_123", "google");
        analytics.processEvent("/article/breaking-news", "user_456", "facebook");
        analytics.processEvent("/sports/championship", "user_789", "google");
        analytics.processEvent("/sports/championship", "user_111", "direct");
        analytics.processEvent("/article/breaking-news", "user_123", "google");

        Thread.sleep(15000);
    }
}
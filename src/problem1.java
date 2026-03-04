import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class problem1 {

    private ConcurrentHashMap<String, Integer> userMap;

    private ConcurrentHashMap<String, Integer> attemptMap;

    private AtomicInteger userIdCounter;

    public problem1() {
        userMap = new ConcurrentHashMap<>();
        attemptMap = new ConcurrentHashMap<>();
        userIdCounter = new AtomicInteger(1);
    }

    public boolean register(String username) {
        if (userMap.containsKey(username)) {
            return false;
        }
        userMap.put(username, userIdCounter.getAndIncrement());
        return true;
    }

    public boolean checkAvailability(String username) {
        attemptMap.merge(username, 1, Integer::sum);

        return !userMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!userMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        if (username.contains("_")) {
            String dotVersion = username.replace("_", ".");
            if (!userMap.containsKey(dotVersion)) {
                suggestions.add(dotVersion);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptMap.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted;
    }

    public static void main(String[] args) {
        problem1 system = new problem1();

        system.register("john_doe");
        system.register("admin");

        System.out.println(system.checkAvailability("john_doe"));  // false
        System.out.println(system.checkAvailability("jane_smith")); // true

        System.out.println(system.suggestAlternatives("john_doe"));

        for (int i = 0; i < 100; i++) {
            system.checkAvailability("admin");
        }

        System.out.println("Most Attempted: " + system.getMostAttempted());
    }
}
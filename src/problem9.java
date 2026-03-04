import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class problem9 {

    static class Transaction {
        int id;
        double amount;
        String merchant;
        String account;
        LocalTime time;

        Transaction(int id, double amount, String merchant,
                    String account, String timeStr) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = LocalTime.parse(timeStr,
                    DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    private List<Transaction> transactions;

    public problem9(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<String> findTwoSum(double target) {
        Map<Double, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction other = map.get(complement);
                result.add("(id:" + other.id + ", id:" + t.id + ")");
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public List<String> findTwoSumWithinOneHour(double target) {
        Map<Double, List<Transaction>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;

            if (map.containsKey(complement)) {
                for (Transaction other : map.get(complement)) {
                    long minutes = Duration.between(other.time, t.time).toMinutes();
                    if (Math.abs(minutes) <= 60) {
                        result.add("(id:" + other.id + ", id:" + t.id + ")");
                    }
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        return result;
    }

    public List<List<Integer>> findKSum(int k, double target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int k, double target,
                           List<Integer> current,
                           List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k <= 0) return;

        for (int i = start; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);

            current.add(t.id);
            backtrack(i + 1, k - 1, target - t.amount,
                    current, result);
            current.remove(current.size() - 1);
        }
    }

    public List<String> detectDuplicates() {
        Map<String, Set<String>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;
            map.computeIfAbsent(key, k -> new HashSet<>()).add(t.account);
        }

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                String[] parts = entry.getKey().split("\\|");
                result.add("{amount:" + parts[0] +
                        ", merchant:" + parts[1] +
                        ", accounts:" + entry.getValue() + "}");
            }
        }

        return result;
    }

    public static void main(String[] args) {

        List<Transaction> list = Arrays.asList(
                new Transaction(1, 500, "Store A", "acc1", "10:00"),
                new Transaction(2, 300, "Store B", "acc2", "10:15"),
                new Transaction(3, 200, "Store C", "acc3", "10:30"),
                new Transaction(4, 500, "Store A", "acc4", "10:40")
        );

        problem9 analyzer = new problem9(list);

        System.out.println(analyzer.findTwoSum(500));
        System.out.println(analyzer.findTwoSumWithinOneHour(500));
        System.out.println(analyzer.findKSum(3, 1000));
        System.out.println(analyzer.detectDuplicates());
    }
}
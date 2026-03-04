import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

public class problem2 {

    private ConcurrentHashMap<String, AtomicInteger> stockMap;
    private ConcurrentHashMap<String, LinkedHashMap<Integer, Integer>> waitingListMap;

    public problem2() {
        stockMap = new ConcurrentHashMap<>();
        waitingListMap = new ConcurrentHashMap<>();
    }

    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingListMap.put(productId, new LinkedHashMap<>());
    }

    public String checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) return "Product not found";
        return stock.get() + " units available";
    }

    public synchronized String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) return "Product not found";

        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success, " + remaining + " units remaining";
        } else {
            LinkedHashMap<Integer, Integer> waitingList = waitingListMap.get(productId);
            waitingList.put(userId, waitingList.size() + 1);
            return "Added to waiting list, position #" + waitingList.size();
        }
    }

    public static void main(String[] args) {
        problem2 system = new problem2();

        system.addProduct("IPHONE15_256GB", 100);

        System.out.println(system.checkStock("IPHONE15_256GB"));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 1; i <= 98; i++) {
            system.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(system.purchaseItem("IPHONE15_256GB", 99999));
    }
}
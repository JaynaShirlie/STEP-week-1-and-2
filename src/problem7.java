import java.util.*;

public class problem7 {

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        String fullQuery;
        int frequency;
    }

    private TrieNode root;
    private HashMap<String, Integer> globalFrequency;

    public problem7() {
        root = new TrieNode();
        globalFrequency = new HashMap<>();
    }

    public void addQuery(String query) {
        globalFrequency.put(query,
                globalFrequency.getOrDefault(query, 0) + 1);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
        node.fullQuery = query;
        node.frequency = globalFrequency.get(query);
    }

    public List<String> search(String prefix) {
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return getTypoSuggestions(prefix);
            }
            node = node.children.get(c);
        }

        PriorityQueue<TrieNode> minHeap =
                new PriorityQueue<>((a, b) -> a.frequency - b.frequency);

        dfs(node, minHeap);

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(0, minHeap.poll().fullQuery +
                    " (" + globalFrequency.get(minHeap.peek() == null ? "" : minHeap.peek().fullQuery) + ")");
        }

        return result;
    }

    private void dfs(TrieNode node, PriorityQueue<TrieNode> heap) {
        if (node.isEnd) {
            heap.offer(node);
            if (heap.size() > 10) {
                heap.poll();
            }
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, heap);
        }
    }

    public void updateFrequency(String query) {
        addQuery(query);
    }

    private List<String> getTypoSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();

        for (String query : globalFrequency.keySet()) {
            if (editDistance(word, query) <= 1) {
                suggestions.add(query + " (" + globalFrequency.get(query) + ")");
            }
        }

        return suggestions;
    }

    private int editDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++)
            for (int j = 0; j <= b.length(); j++)
                if (i == 0)
                    dp[i][j] = j;
                else if (j == 0)
                    dp[i][j] = i;
                else if (a.charAt(i - 1) == b.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));

        return dp[a.length()][b.length()];
    }

    public static void main(String[] args) {

        problem7 autocomplete = new problem7();

        autocomplete.addQuery("java tutorial");
        autocomplete.addQuery("javascript");
        autocomplete.addQuery("java download");
        autocomplete.addQuery("java tutorial");
        autocomplete.addQuery("java 21 features");
        autocomplete.addQuery("java 21 features");
        autocomplete.addQuery("java 21 features");

        List<String> results = autocomplete.search("jav");

        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }

        autocomplete.updateFrequency("java 21 features");
    }
}
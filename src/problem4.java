import java.util.*;

public class problem4 {

    private static final int N = 5;

    private HashMap<String, Set<String>> ngramIndex;
    private HashMap<String, Integer> documentNgramCount;

    public problem4() {
        ngramIndex = new HashMap<>();
        documentNgramCount = new HashMap<>();
    }

    public void addDocument(String docId, String content) {
        List<String> ngrams = extractNgrams(content);
        documentNgramCount.put(docId, ngrams.size());

        for (String ngram : ngrams) {
            ngramIndex
                    .computeIfAbsent(ngram, k -> new HashSet<>())
                    .add(docId);
        }
    }

    public void analyzeDocument(String docId, String content) {
        List<String> ngrams = extractNgrams(content);
        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String ngram : ngrams) {
            if (ngramIndex.containsKey(ngram)) {
                for (String existingDoc : ngramIndex.get(ngram)) {
                    if (!existingDoc.equals(docId)) {
                        matchCounts.put(existingDoc,
                                matchCounts.getOrDefault(existingDoc, 0) + 1);
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String comparedDoc = entry.getKey();
            int matches = entry.getValue();

            int totalNgrams = ngrams.size();
            double similarity = (double) matches / totalNgrams * 100;

            System.out.println("Found " + matches + " matching n-grams with \""
                    + comparedDoc + "\"");
            System.out.println("Similarity: "
                    + String.format("%.2f", similarity) + "%");

            if (similarity >= 60) {
                System.out.println("PLAGIARISM DETECTED");
            } else if (similarity >= 15) {
                System.out.println("Suspicious");
            }
        }
    }

    private List<String> extractNgrams(String content) {
        List<String> result = new ArrayList<>();
        String[] words = content.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }
            result.add(sb.toString().trim());
        }

        return result;
    }

    public static void main(String[] args) {

        problem4 detector = new problem4();

        String essay1 = "machine learning is a subset of artificial intelligence that focuses on data driven models";
        String essay2 = "machine learning is a subset of artificial intelligence that focuses on data driven models and predictive systems";
        String essay3 = "the quick brown fox jumps over the lazy dog in the park";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        detector.analyzeDocument("essay_123.txt", essay1);
    }
}
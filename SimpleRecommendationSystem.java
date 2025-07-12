// File: SimpleRecommendationSystem.java
import java.util.*;

public class SimpleRecommendationSystem {

    // Sample data: user ratings (user -> (item -> rating))
    static Map<String, Map<String, Integer>> userRatings = new HashMap<>();

    // Add sample ratings
    static {
        userRatings.put("Alice", Map.of("Laptop", 5, "Phone", 3, "Camera", 4));
        userRatings.put("Bob", Map.of("Laptop", 4, "Phone", 2, "Headphones", 5));
        userRatings.put("Charlie", Map.of("Camera", 5, "Phone", 3, "Headphones", 4));
    }

    // Recommend top items to a target user
    public static List<String> recommendItems(String targetUser) {
        Map<String, Integer> targetRatings = userRatings.get(targetUser);
        if (targetRatings == null) {
            System.out.println("❌ User not found.");
            return List.of();
        }

        Map<String, Double> scoreMap = new HashMap<>();

        // Compare with every other user
        for (String otherUser : userRatings.keySet()) {
            if (otherUser.equals(targetUser)) continue;
            double similarity = computeSimilarity(targetRatings, userRatings.get(otherUser));
            for (String item : userRatings.get(otherUser).keySet()) {
                if (!targetRatings.containsKey(item)) {
                    scoreMap.put(item, scoreMap.getOrDefault(item, 0.0) + similarity * userRatings.get(otherUser).get(item));
                }
            }
        }

        // Sort recommendations
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(scoreMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<String> recommendations = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sorted) {
            recommendations.add(entry.getKey() + " (score: " + String.format("%.2f", entry.getValue()) + ")");
        }
        return recommendations;
    }

    // Compute cosine similarity between two users
    private static double computeSimilarity(Map<String, Integer> userA, Map<String, Integer> userB) {
        Set<String> commonItems = new HashSet<>(userA.keySet());
        commonItems.retainAll(userB.keySet());

        double dotProduct = 0, normA = 0, normB = 0;
        for (String item : commonItems) {
            dotProduct += userA.get(item) * userB.get(item);
        }

        for (int val : userA.values()) normA += val * val;
        for (int val : userB.values()) normB += val * val;

        if (normA == 0 || normB == 0) return 0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter user name (Alice/Bob/Charlie): ");
        String user = sc.nextLine();

        List<String> recommendations = recommendItems(user);
        if (!recommendations.isEmpty()) {
            System.out.println("🎯 Recommended items for " + user + ":");
            for (String rec : recommendations) {
                System.out.println("🔹 " + rec);
            }
        }

        sc.close();
    }
}
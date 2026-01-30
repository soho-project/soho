package work.soho.game.biz.wordmatch.service;

import java.util.HashMap;
import java.util.Map;

public class EloRatingService {
    private final int kFactor;

    public EloRatingService(int kFactor) {
        this.kFactor = kFactor;
    }

    public Map<String, Integer> calculate(Map<String, Integer> scores, Map<String, Integer> currentRatings) {
        Map<String, Integer> newRatings = new HashMap<>(currentRatings);
        if (scores == null || currentRatings == null || scores.size() < 2) {
            return newRatings;
        }
        String[] players = scores.keySet().toArray(new String[0]);
        for (int i = 0; i < players.length; i++) {
            for (int j = i + 1; j < players.length; j++) {
                String a = players[i];
                String b = players[j];
                int ratingA = newRatings.getOrDefault(a, 1000);
                int ratingB = newRatings.getOrDefault(b, 1000);
                double expectedA = expectedScore(ratingA, ratingB);
                double expectedB = expectedScore(ratingB, ratingA);
                double scoreA = score(scores.get(a), scores.get(b));
                double scoreB = 1.0 - scoreA;
                int updatedA = (int) Math.round(ratingA + kFactor * (scoreA - expectedA));
                int updatedB = (int) Math.round(ratingB + kFactor * (scoreB - expectedB));
                newRatings.put(a, updatedA);
                newRatings.put(b, updatedB);
            }
        }
        return newRatings;
    }

    private double expectedScore(int ratingA, int ratingB) {
        return 1.0 / (1.0 + Math.pow(10, (ratingB - ratingA) / 400.0));
    }

    private double score(Integer scoreA, Integer scoreB) {
        int a = scoreA == null ? 0 : scoreA;
        int b = scoreB == null ? 0 : scoreB;
        if (a == b) {
            return 0.5;
        }
        return a > b ? 1.0 : 0.0;
    }
}

package work.soho.game.biz.wordmatch.service;

import work.soho.game.biz.wordmatch.domain.WordMatchRankProfile;

import java.util.Map;

public interface WordMatchRankService {
    WordMatchRankProfile getOrCreate(String playerId, int defaultScore);

    Map<String, Integer> getRankScores(Iterable<String> playerIds, int defaultScore);

    void applyEloResult(Map<String, Integer> newRatings, Map<String, ResultStat> resultStats);

    class ResultStat {
        public int wins;
        public int losses;
    }
}

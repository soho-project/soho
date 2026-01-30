package work.soho.game.biz.wordmatch.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.game.biz.mapper.WordMatchRankProfileMapper;
import work.soho.game.biz.wordmatch.domain.WordMatchRankProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DS("game")
@RequiredArgsConstructor
@Service
public class WordMatchRankServiceImpl extends ServiceImpl<WordMatchRankProfileMapper, WordMatchRankProfile>
        implements WordMatchRankService {

    @Override
    public WordMatchRankProfile getOrCreate(String playerId, int defaultScore) {
        if (playerId == null) {
            return null;
        }
        WordMatchRankProfile profile = getOne(new LambdaQueryWrapper<WordMatchRankProfile>()
                .eq(WordMatchRankProfile::getPlayerId, playerId)
                .orderByDesc(WordMatchRankProfile::getId), false);
        if (profile != null) {
            return profile;
        }
        profile = new WordMatchRankProfile();
        profile.setPlayerId(playerId);
        profile.setRankScore(defaultScore);
        profile.setWins(0);
        profile.setLosses(0);
        save(profile);
        return profile;
    }

    @Override
    public Map<String, Integer> getRankScores(Iterable<String> playerIds, int defaultScore) {
        Map<String, Integer> result = new HashMap<>();
        if (playerIds == null) {
            return result;
        }
        for (String playerId : playerIds) {
            WordMatchRankProfile profile = getOrCreate(playerId, defaultScore);
            result.put(playerId, profile != null && profile.getRankScore() != null ? profile.getRankScore() : defaultScore);
        }
        return result;
    }

    @Override
    public void applyEloResult(Map<String, Integer> newRatings, Map<String, ResultStat> resultStats) {
        if (newRatings == null) {
            return;
        }
        for (Map.Entry<String, Integer> entry : newRatings.entrySet()) {
            String playerId = entry.getKey();
            Integer rating = entry.getValue();
            WordMatchRankProfile profile = getOne(new LambdaQueryWrapper<WordMatchRankProfile>()
                    .eq(WordMatchRankProfile::getPlayerId, playerId)
                    .orderByDesc(WordMatchRankProfile::getId), false);
            if (profile == null) {
                profile = new WordMatchRankProfile();
                profile.setPlayerId(playerId);
                profile.setWins(0);
                profile.setLosses(0);
            }
            profile.setRankScore(rating);
            ResultStat stat = resultStats != null ? resultStats.get(playerId) : null;
            if (stat != null) {
                profile.setWins(profile.getWins() + stat.wins);
                profile.setLosses(profile.getLosses() + stat.losses);
            }
            if (profile.getId() == null) {
                save(profile);
            } else {
                updateById(profile);
            }
        }
    }
}

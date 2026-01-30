package work.soho.game.biz.wordmatch.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.JacksonUtils;
import work.soho.game.biz.mapper.WordMatchBattleMapper;
import work.soho.game.biz.wordmatch.domain.WordMatchBattle;
import work.soho.game.biz.wordmatch.model.WordMatchRoomMode;

import java.time.LocalDateTime;
import java.util.Map;

@DS("game")
@RequiredArgsConstructor
@Service
public class WordMatchBattleServiceImpl extends ServiceImpl<WordMatchBattleMapper, WordMatchBattle>
        implements WordMatchBattleService {

    @Override
    public WordMatchBattle createBattle(String roomId, WordMatchRoomMode mode) {
        WordMatchBattle battle = new WordMatchBattle();
        battle.setRoomId(roomId);
        battle.setMode(mode != null ? mode.name() : null);
        battle.setStatus("WAITING");
        battle.setStartedAt(null);
        battle.setEndedAt(null);
        save(battle);
        return battle;
    }

    @Override
    public WordMatchBattle getByRoomId(String roomId) {
        if (roomId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<WordMatchBattle>()
                .eq(WordMatchBattle::getRoomId, roomId)
                .orderByDesc(WordMatchBattle::getId), false);
    }

    @Override
    public void updateRunning(Long battleId, LocalDateTime startedAt) {
        if (battleId == null) {
            return;
        }
        WordMatchBattle battle = getById(battleId);
        if (battle == null) {
            return;
        }
        battle.setStatus("RUNNING");
        battle.setStartedAt(startedAt);
        updateById(battle);
    }

    @Override
    public void updateFinished(Long battleId, String status, String winnerId, String endReason, Map<String, Integer> scores, LocalDateTime endedAt) {
        if (battleId == null) {
            return;
        }
        WordMatchBattle battle = getById(battleId);
        if (battle == null) {
            return;
        }
        battle.setStatus(status != null ? status : "GAME_OVER");
        battle.setWinnerId(winnerId);
        battle.setEndReason(endReason);
        battle.setScoresJson(scores != null ? JacksonUtils.toJson(scores) : null);
        battle.setEndedAt(endedAt);
        updateById(battle);
    }
}

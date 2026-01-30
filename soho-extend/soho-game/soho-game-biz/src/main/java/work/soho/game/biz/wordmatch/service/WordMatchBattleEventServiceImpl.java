package work.soho.game.biz.wordmatch.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.JacksonUtils;
import work.soho.game.biz.mapper.WordMatchBattleEventMapper;
import work.soho.game.biz.wordmatch.domain.WordMatchBattleEvent;

@DS("game")
@RequiredArgsConstructor
@Service
public class WordMatchBattleEventServiceImpl extends ServiceImpl<WordMatchBattleEventMapper, WordMatchBattleEvent>
        implements WordMatchBattleEventService {

    @Override
    public void appendEvent(Long battleId, String roomId, long seq, String type, Object payload) {
        WordMatchBattleEvent event = new WordMatchBattleEvent();
        event.setBattleId(battleId);
        event.setRoomId(roomId);
        event.setSeq(seq);
        event.setType(type);
        event.setPayloadJson(payload != null ? JacksonUtils.toJson(payload) : null);
        save(event);
    }
}

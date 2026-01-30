package work.soho.game.biz.wordmatch.service;

public interface WordMatchBattleEventService {
    void appendEvent(Long battleId, String roomId, long seq, String type, Object payload);
}

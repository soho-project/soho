package work.soho.game.biz.wordmatch.board;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WordMatchBoardDelta {
    /** 本次消除的格子（坐标列表） */
    private List<Cell> cleared = new ArrayList<>();
    /** 本次新填充的格子（坐标 + 值） */
    private List<Cell> filled = new ArrayList<>();
    /** 消除+下落+补齐后的完整棋盘快照 */
    private List<List<String>> board;

    @Data
    public static class Cell {
        /** 列坐标（x，从 0 开始） */
        private int x;
        /** 行坐标（y，从 0 开始） */
        private int y;
        /** 格子内容（新填充时携带） */
        private String value;
    }
}

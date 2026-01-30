package work.soho.game.biz.wordmatch.board;

import lombok.Data;

/**
 * 单词消消消乐棋盘数据结构。
 * - cells[y][x] 存放单字母字符串
 * - width/height 为棋盘尺寸
 */
@Data
public class WordMatchBoard {
    private int width;
    private int height;
    private String[][] cells;
}

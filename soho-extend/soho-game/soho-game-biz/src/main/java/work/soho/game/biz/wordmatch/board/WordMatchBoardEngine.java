package work.soho.game.biz.wordmatch.board;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 单词消消消乐棋盘引擎。
 * - 规则：单词按字母在棋盘上 4 邻接（上下左右）连线匹配
 * - 成功匹配后清空路径，按列下落补齐
 */
public class WordMatchBoardEngine {
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final SecureRandom random = new SecureRandom();

    /** 生成新棋盘，随机填充字母 */
    public WordMatchBoard createBoard(int width, int height) {
        WordMatchBoard board = new WordMatchBoard();
        board.setWidth(width);
        board.setHeight(height);
        String[][] cells = new String[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = randomLetter();
            }
        }
        board.setCells(cells);
        return board;
    }

    /**
     * 生成棋盘并尝试嵌入指定单词（保证至少有词可匹配时使用）。
     * - 单词按上下左右相邻嵌入
     * - 嵌入失败的单词会被跳过
     */
    public WordMatchBoard createBoardWithWords(int width, int height, List<String> words) {
        WordMatchBoard board = new WordMatchBoard();
        board.setWidth(width);
        board.setHeight(height);
        String[][] cells = new String[height][width];
        if (words != null) {
            for (String raw : words) {
                if (raw == null) {
                    continue;
                }
                String word = raw.trim().toUpperCase();
                if (word.isEmpty() || word.length() > width * height) {
                    continue;
                }
                placeWord(cells, width, height, word);
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cells[y][x] == null) {
                    cells[y][x] = randomLetter();
                }
            }
        }
        board.setCells(cells);
        return board;
    }

    /**
     * 在棋盘中尝试匹配单词。
     * @return 命中则返回 delta，否则返回 null
     */
    public WordMatchBoardDelta applyWord(WordMatchBoard board, String word) {
        if (board == null || board.getCells() == null || word == null || word.isBlank()) {
            return null;
        }
        String normalized = word.trim().toUpperCase();
        String[][] cells = board.getCells();
        boolean[][] visited = new boolean[board.getHeight()][board.getWidth()];
        List<int[]> path = new ArrayList<>();
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (matches(cells[y][x], normalized.charAt(0))) {
                    if (dfs(cells, visited, normalized, 0, x, y, path)) {
                        return clearAndFill(board, path);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断棋盘中是否存在该单词路径（不修改棋盘）。
     */
    public boolean containsWord(WordMatchBoard board, String word) {
        if (board == null || board.getCells() == null || word == null || word.isBlank()) {
            return false;
        }
        String normalized = word.trim().toUpperCase();
        String[][] cells = board.getCells();
        boolean[][] visited = new boolean[board.getHeight()][board.getWidth()];
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (matches(cells[y][x], normalized.charAt(0))) {
                    if (dfs(cells, visited, normalized, 0, x, y, new ArrayList<>())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** 返回棋盘快照（二维数组 -> List 形式，便于序列化） */
    public List<List<String>> snapshot(WordMatchBoard board) {
        List<List<String>> rows = new ArrayList<>();
        if (board == null || board.getCells() == null) {
            return rows;
        }
        for (int y = 0; y < board.getHeight(); y++) {
            List<String> row = new ArrayList<>();
            for (int x = 0; x < board.getWidth(); x++) {
                row.add(board.getCells()[y][x]);
            }
            rows.add(row);
        }
        return rows;
    }

    /**
     * DFS 找路径，按上下左右搜索。
     * path 保存匹配路径坐标。
     */
    private boolean dfs(String[][] cells, boolean[][] visited, String word, int index, int x, int y, List<int[]> path) {
        if (index >= word.length()) {
            return true;
        }
        if (x < 0 || y < 0 || y >= cells.length || x >= cells[0].length) {
            return false;
        }
        if (visited[y][x]) {
            return false;
        }
        if (!matches(cells[y][x], word.charAt(index))) {
            return false;
        }
        visited[y][x] = true;
        path.add(new int[]{x, y});
        if (index == word.length() - 1) {
            return true;
        }
        if (dfs(cells, visited, word, index + 1, x + 1, y, path)
                || dfs(cells, visited, word, index + 1, x - 1, y, path)
                || dfs(cells, visited, word, index + 1, x, y + 1, path)
                || dfs(cells, visited, word, index + 1, x, y - 1, path)) {
            return true;
        }
        visited[y][x] = false;
        path.remove(path.size() - 1);
        return false;
    }

    /**
     * 清除匹配路径并进行下落与补齐。
     */
    private WordMatchBoardDelta clearAndFill(WordMatchBoard board, List<int[]> path) {
        String[][] cells = board.getCells();
        WordMatchBoardDelta delta = new WordMatchBoardDelta();
        for (int[] p : path) {
            int x = p[0];
            int y = p[1];
            cells[y][x] = null;
            WordMatchBoardDelta.Cell cell = new WordMatchBoardDelta.Cell();
            cell.setX(x);
            cell.setY(y);
            delta.getCleared().add(cell);
        }
        for (int x = 0; x < board.getWidth(); x++) {
            int write = board.getHeight() - 1;
            for (int y = board.getHeight() - 1; y >= 0; y--) {
                if (cells[y][x] != null) {
                    cells[write][x] = cells[y][x];
                    if (write != y) {
                        cells[y][x] = null;
                    }
                    write--;
                }
            }
            while (write >= 0) {
                cells[write][x] = randomLetter();
                WordMatchBoardDelta.Cell cell = new WordMatchBoardDelta.Cell();
                cell.setX(x);
                cell.setY(write);
                cell.setValue(cells[write][x]);
                delta.getFilled().add(cell);
                write--;
            }
        }
        delta.setBoard(snapshot(board));
        return delta;
    }

    /** 格子字符匹配（不区分大小写） */
    private boolean matches(String value, char expected) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return Character.toUpperCase(value.charAt(0)) == expected;
    }

    /**
     * 尝试在棋盘中放置单词（上下左右连线）。
     * @return 是否放置成功
     */
    private boolean placeWord(String[][] cells, int width, int height, String word) {
        int attempts = Math.max(10, width * height);
        for (int i = 0; i < attempts; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (cells[y][x] != null && !matches(cells[y][x], word.charAt(0))) {
                continue;
            }
            if (tryPlaceFrom(cells, width, height, word, 0, x, y, new boolean[height][width])) {
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceFrom(String[][] cells, int width, int height, String word, int index, int x, int y, boolean[][] used) {
        if (index >= word.length()) {
            return true;
        }
        if (x < 0 || y < 0 || x >= width || y >= height || used[y][x]) {
            return false;
        }
        char ch = word.charAt(index);
        if (cells[y][x] != null && !matches(cells[y][x], ch)) {
            return false;
        }
        used[y][x] = true;
        String prev = cells[y][x];
        cells[y][x] = String.valueOf(ch);
        int[][] dirs = new int[][] {{1,0},{-1,0},{0,1},{0,-1}};
        // 随机方向以减少固定走向
        for (int i = dirs.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }
        if (index == word.length() - 1) {
            return true;
        }
        for (int[] d : dirs) {
            if (tryPlaceFrom(cells, width, height, word, index + 1, x + d[0], y + d[1], used)) {
                return true;
            }
        }
        // 回滚
        cells[y][x] = prev;
        used[y][x] = false;
        return false;
    }

    /** 随机生成单个字母字符串 */
    private String randomLetter() {
        return String.valueOf(LETTERS[random.nextInt(LETTERS.length)]);
    }
}

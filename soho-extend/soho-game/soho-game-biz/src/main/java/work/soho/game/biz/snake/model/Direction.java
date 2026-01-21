package work.soho.game.biz.snake.model;

/**
 * 方向枚举，包含位移向量。
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public boolean isOpposite(Direction other) {
        if (other == null) {
            return false;
        }
        return dx + other.dx == 0 && dy + other.dy == 0;
    }
}

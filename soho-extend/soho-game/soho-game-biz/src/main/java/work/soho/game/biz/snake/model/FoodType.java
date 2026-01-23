package work.soho.game.biz.snake.model;

/**
 * 食物类型与积分值。
 */
public enum FoodType {
    APPLE(50),
    BANANA(80),
    PIZZA(150),
    GRAPE(30),
    MAGNET(0);

    /** 食物积分 */
    private final int score;

    FoodType(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}

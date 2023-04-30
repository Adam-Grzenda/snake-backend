package pl.put.snake.game.model.snake;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}

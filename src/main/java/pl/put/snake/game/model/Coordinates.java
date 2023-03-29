package pl.put.snake.game.model;

public record Coordinates(
        int x,
        int y
) {
    public Coordinates next(Direction direction) {
        return switch (direction) {
            case UP -> new Coordinates(x, y + 1);
            case DOWN -> new Coordinates(x, y - 1);
            case LEFT -> new Coordinates(x - 1, y);
            case RIGHT -> new Coordinates(x + 1, y);
        };
    }
}

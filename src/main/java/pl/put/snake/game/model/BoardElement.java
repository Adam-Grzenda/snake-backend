package pl.put.snake.game.model;

public record BoardElement(
        ElementType type,
        Coordinates coordinates
) {
    public enum ElementType {
        SNAKE,
        APPLE
    }

}

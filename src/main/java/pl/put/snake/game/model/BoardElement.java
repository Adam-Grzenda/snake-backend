package pl.put.snake.game.model;

public record BoardElement(
        Integer id,
        ElementType type,
        Coordinates coordinates
) {

    public enum ElementType {
        SNAKE,
        APPLE
    }

}

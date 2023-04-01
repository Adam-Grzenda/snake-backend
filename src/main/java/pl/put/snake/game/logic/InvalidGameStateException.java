package pl.put.snake.game.logic;

public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException(Throwable cause) {
        super(cause);
    }
}

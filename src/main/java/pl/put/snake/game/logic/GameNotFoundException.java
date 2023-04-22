package pl.put.snake.game.logic;

public class GameNotFoundException extends EntityNotFoundException {
    public GameNotFoundException(String id) {
        super("Game with id=" + id + " not found");
    }
}

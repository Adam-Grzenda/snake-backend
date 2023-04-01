package pl.put.snake.game.model;

import pl.put.snake.game.logic.Game;

import java.util.Set;

public record Board(
        Set<Snake> snakes,
        Set<Coordinates> apples,
        Integer boardSize

) {
    public static Board fromGame(Game game) {
        return new Board(game.getSnakes(), game.getApples(), game.getBoardSize());
    }
}

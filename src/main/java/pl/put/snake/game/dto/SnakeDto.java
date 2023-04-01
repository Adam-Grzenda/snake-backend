package pl.put.snake.game.dto;

import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.Snake;

import java.util.Set;

public record SnakeDto(
        String playerId,
        Set<Coordinates> parts
) {
    public static SnakeDto from(Snake snake) {
        return new SnakeDto(
                snake.getPlayerId().toString(),
                snake.getParts()
        );
    }
}

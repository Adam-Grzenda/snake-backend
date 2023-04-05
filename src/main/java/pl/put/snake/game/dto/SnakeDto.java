package pl.put.snake.game.dto;

import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.Snake;

import java.util.Set;

public record SnakeDto(
        Player player,
        Set<Coordinates> parts
) {
    public static SnakeDto from(Snake snake) {
        return new SnakeDto(
                snake.getPlayer(),
                snake.getParts()
        );
    }
}

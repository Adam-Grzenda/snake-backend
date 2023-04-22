package pl.put.snake.game.dto;

import pl.put.snake.game.model.Color;
import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.Snake;

import java.util.Set;

public record SnakeDto(
        int id,
        Player player,
        Set<Coordinates> parts,
        Color color
) {
    public static SnakeDto from(Snake snake) {
        return new SnakeDto(
                snake.getId(),
                snake.getPlayer(),
                snake.getParts(),
                snake.getColor()
        );
    }
}

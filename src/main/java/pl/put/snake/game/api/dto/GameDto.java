package pl.put.snake.game.api.dto;

import pl.put.snake.game.logic.Game;
import pl.put.snake.game.model.Coordinates;

import java.util.Set;
import java.util.stream.Collectors;

public record GameDto(
        int gameId,
        Game.GameStatus status,
        Set<SnakeDto> snakes,
        Set<Coordinates> apples,
        Integer boardSize,
        Set<PlayerDto> players
) {
    public static GameDto from(Game game) {
        return new GameDto(
                game.getId(),
                game.getStatus(),
                game.getSnakes().stream().map(SnakeDto::from).collect(Collectors.toSet()),
                game.getApples(),
                game.getBoardSize(),
                game.getPlayers().stream().map(PlayerDto::from).collect(Collectors.toSet())
        );
    }
}

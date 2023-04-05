package pl.put.snake.game.repository;

import pl.put.snake.game.logic.Game;

import java.util.Optional;

public interface GameRepository {
    Optional<Game> findGameById(String gameId);

    Game save(Game game);

    Game getGameById(String gameId);

    Optional<Game> findGameByPlayerId(String playerId);
}

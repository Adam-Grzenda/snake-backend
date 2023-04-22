package pl.put.snake.game.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.logic.GameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static pl.put.snake.game.logic.Game.GameStatus.RUNNING;

@Component
@ConditionalOnProperty(value = "game.repository", havingValue = "memory")
public class GameMemoryRepository implements GameRepository {

    private final Map<String, Game> games = new HashMap<>();

    @Override
    public Optional<Game> findGameById(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    @Override
    public Game save(Game game) {
        games.put(game.getId().toString(), game);
        return game;
    }

    @Override
    public Game getGameById(String gameId) {
        return findGameById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Override
    public Optional<Game> findGameByPlayerId(String playerId) {
        return games.values()
                .stream()
                .filter(game -> game.getStatus() == RUNNING)
                .filter(game -> game.getPlayers()
                        .stream()
                        .anyMatch(player -> Objects.equals(player.id().toString(), playerId)))
                .findFirst();

    }
}

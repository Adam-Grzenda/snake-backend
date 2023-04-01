package pl.put.snake.game;

import org.springframework.stereotype.Service;
import pl.put.snake.game.dto.GameDto;
import pl.put.snake.game.dto.SnakeDto;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.logic.board.CollisionDetector;
import pl.put.snake.game.logic.board.RandomGenerator;
import pl.put.snake.player.PlayerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GameService {
    private final Map<String, Game> games = new HashMap<>();
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final PlayerService playerService;
    private final GameRunner gameRunner;

    public GameService(CollisionDetector collisionDetector, RandomGenerator randomGenerator,
                       PlayerService playerService, GameRunner gameRunner) {
        this.collisionDetector = collisionDetector;
        this.randomGenerator = randomGenerator;
        this.playerService = playerService;
        this.gameRunner = gameRunner;
    }

    public GameDto createGame(GameDto gameRequest) {
        var game = new Game(gameRequest.boardSize(), randomGenerator, collisionDetector);
        games.put(game.getId().toString(), game);
        return GameDto.from(game);
    }

    public SnakeDto joinGame(String gameId, String playerId) {
        var game = getGameById(gameId);
        var player = playerService.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalStateException("Player with id: " + playerId + " does not exist"));

        return SnakeDto.from(game.join(player));
    }

    public void startGame(String gameId) {
        gameRunner.submit(getGameById(gameId), 1000);
    }


    private Game getGameById(String gameId) {
        return findGameById(gameId).orElseThrow(() -> new IllegalStateException("Game with id: " + gameId + " does not exist"));
    }

    private Optional<Game> findGameById(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }


}

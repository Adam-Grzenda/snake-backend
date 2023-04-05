package pl.put.snake.game.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.put.snake.game.dto.GameDto;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.logic.InvalidGameStateException;
import pl.put.snake.game.logic.board.CollisionDetector;
import pl.put.snake.game.logic.board.GameDeltaListener;
import pl.put.snake.game.logic.board.RandomGenerator;
import pl.put.snake.game.model.GameDelta;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.PlayerInput;
import pl.put.snake.game.repository.GameRepository;
import pl.put.snake.game.utils.GameRunner;
import pl.put.snake.player.PlayerService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
public class GameService {
    private final Set<GameDeltaListener> deltaListeners;
    private final GameRepository gameRepository;
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final PlayerService playerService;
    private final GameRunner gameRunner;

    public GameService(GameRepository gameRepository,
                       CollisionDetector collisionDetector,
                       RandomGenerator randomGenerator,
                       PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.collisionDetector = collisionDetector;
        this.randomGenerator = randomGenerator;
        this.playerService = playerService;
        this.gameRunner = new GameRunner(this);
        this.deltaListeners = new HashSet<>();
    }

    public GameDto createGame(GameDto gameRequest) {
        var game = new Game(gameRequest.boardSize(), randomGenerator, collisionDetector);
        return GameDto.from(gameRepository.save(game));
    }

    public GameDto joinGame(String gameId, String playerId) {
        var game = gameRepository.getGameById(gameId);
        var player = getPlayerOrThrow(playerId);
        var boardDelta = game.join(player);
        updateDelta(game, boardDelta);
        return GameDto.from(game);
    }

    public void handlePlayerInput(String playerId, PlayerInput input) {
        var game = gameRepository.findGameByPlayerId(playerId);
        if (game.isPresent()) {
            var player = getPlayerOrThrow(playerId);
            game.get().handleInput(player, input);
        } else {
            log.error("Input from player with id: {} without running game", playerId);
        }
    }

    public void startGame(String gameId) {
        var game = gameRepository.getGameById(gameId);
        gameRunner.submit(game, 100);
    }

    private Player getPlayerOrThrow(String playerId) {
        return playerService.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalStateException("Player with id: " + playerId + " does not exist"));
    }

    public void registerDeltaListener(GameDeltaListener deltaListener) {
        deltaListeners.add(deltaListener);
    }

    public void updateDelta(Game game, GameDelta gameDelta) {
        for (var deltaListener : deltaListeners) {
            try {
                deltaListener.update(game.getPlayers(), gameDelta);
            } catch (IOException e) {
                log.error("Failed to notify delta observers");
                throw new InvalidGameStateException(e);
            }
        }
    }

    public Optional<GameDto> findGameById(String gameId) {
        return gameRepository.findGameById(gameId).map(GameDto::from);
    }
}

package pl.put.snake.game.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.put.snake.game.api.dto.GameDto;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.logic.GameNotFoundException;
import pl.put.snake.game.logic.InvalidGameStateException;
import pl.put.snake.game.logic.board.CollisionDetector;
import pl.put.snake.game.logic.board.RandomGenerator;
import pl.put.snake.game.logic.board.StateDeltaListener;
import pl.put.snake.game.model.PlayerInput;
import pl.put.snake.game.model.state.PauseGameDelta;
import pl.put.snake.game.model.state.StateDelta;
import pl.put.snake.game.repository.GameRepository;
import pl.put.snake.game.utils.GameRunner;
import pl.put.snake.player.PlayerService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
public class GameService {
    private final Set<StateDeltaListener> deltaListeners;
    private final GameRepository gameRepository;
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final PlayerService playerService;
    private final GameRunner gameRunner;

    private final AtomicInteger gameCounter = new AtomicInteger();

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
        var game = new Game(gameCounter.getAndIncrement(), gameRequest.boardSize(), randomGenerator, collisionDetector);
        return GameDto.from(gameRepository.save(game));
    }

    public GameDto joinGame(String gameId, String playerId) {
        var game = gameRepository.getGameById(gameId);
        var player = playerService.getPlayerById(playerId);
        var playerDelta = game.join(player);
        broadcastDelta(game, playerDelta);
        return GameDto.from(game);
    }

    public void startGame(String gameId, Integer stepMillis) {
        var game = gameRepository.getGameById(gameId);
        gameRunner.submit(game, stepMillis);
    }

    public void pauseGame(String gameId, String playerId) {
        var game = gameRepository.findGameById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        pauseGame(game, playerId);
    }

    public void pauseGame(Game game, String playerId) {
        var playerSnake = game.getSnakes()
                .stream()
                .filter(snake -> Objects.equals(snake.getPlayer().stringId(), playerId))
                .findFirst();
        playerSnake.ifPresent(snake -> {
            game.pause();
            broadcastDelta(game, new PauseGameDelta(game, snake));
        });
    }

    public void handleDisconnectedPlayer(String playerId) {
        var game = gameRepository.findGameByPlayerId(playerId);
        game.ifPresent(g -> pauseGame(g, playerId));
    }

    public void handleInput(String playerId, PlayerInput input) {
        var game = gameRepository.findGameByPlayerId(playerId);
        if (game.isPresent()) {
            var player = playerService.getPlayerById(playerId);
            game.get().handleInput(player, input);
        } else {
            log.error("Input from player with id: {} without running game", playerId);
        }
    }


    public void broadcastDelta(Game game, StateDelta delta) {
        for (var deltaListener : deltaListeners) {
            try {
                deltaListener.update(game.getPlayers(), delta);
            } catch (IOException e) {
                log.error("Failed to notify delta observers");
                throw new InvalidGameStateException(e);
            }
        }
    }

    public Optional<GameDto> findGameById(String gameId) {
        return gameRepository.findGameById(gameId).map(GameDto::from);
    }

    public void registerDeltaListener(StateDeltaListener deltaListener) {
        deltaListeners.add(deltaListener);
    }

    public void resumeGame(String gameId, Integer stepMillis) {
        gameRepository.findGameById(gameId).ifPresent(game -> gameRunner.submit(game, stepMillis));
    }
}

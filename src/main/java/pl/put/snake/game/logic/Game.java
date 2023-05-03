package pl.put.snake.game.logic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.put.snake.game.logic.board.CollisionDetector;
import pl.put.snake.game.logic.board.RandomGenerator;
import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.PlayerInput;
import pl.put.snake.game.model.snake.Direction;
import pl.put.snake.game.model.snake.Snake;
import pl.put.snake.game.model.state.GameDelta;
import pl.put.snake.game.model.state.PlayerDelta;

import java.util.*;
import java.util.stream.Collectors;

import static pl.put.snake.game.logic.Game.GameStatus.*;


@Slf4j
@Getter
public class Game {

    public enum GameStatus {
        NEW,
        RUNNING,
        FINISHED,
        PAUSED
    }

    private final int id;

    private final Set<Coordinates> apples;
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final int boardSize;
    private final Map<Player, Snake> playerSnakes;
    private final Set<Player> players;
    private GameStatus status = NEW;

    public Game(int id, int boardSize, RandomGenerator randomGenerator, CollisionDetector collisionDetector) {
        this.randomGenerator = randomGenerator;
        this.apples = new HashSet<>();
        this.boardSize = boardSize;
        this.collisionDetector = collisionDetector;
        this.id = id;
        this.playerSnakes = new HashMap<>();
        this.players = new HashSet<>();
    }

    public GameDelta step() {
        if (status != RUNNING) {
            throw new IllegalStateException("Game is not in running state");
        }
        var delta = new GameDelta(this);

        for (var snake : playerSnakes.values()) {
            delta.addSnakePart(snake.getId(), snake.moveHead());
        }

        var appleEatingSnakes = getAppleEatingSnakes(playerSnakes.values());
        removeExtraTailsExcluding(appleEatingSnakes, delta);

        if (!appleEatingSnakes.isEmpty() || apples.isEmpty()) {
            appleEatingSnakes.stream().map(Snake::getHead).toList().forEach(apple -> {
                apples.remove(apple);
                delta.removeApple(apple);
            });

            var newApple = randomGenerator.freeCoordinate(boardSize, getAllTakenCoordinates());
            apples.add(newApple);
            delta.addApple(newApple);
        }

        var collidedSnakes = collisionDetector.detectCollisions(playerSnakes.values(), boardSize);
        playerSnakes.values().removeAll(collidedSnakes);
        collidedSnakes.forEach(delta::removeSnake);

        if (playerSnakes.isEmpty()) {
            status = FINISHED;
        }

        delta.setStatus(status);
        return delta;
    }

    public PlayerDelta join(Player player) {
        var snake = playerSnakes.computeIfAbsent(player, p -> createNewSnake(player));
        players.add(player);
        return new PlayerDelta(this, player, snake);
    }

    private Snake createNewSnake(Player player) {
        return new Snake(
                (short) playerSnakes.size(),
                randomGenerator.freeCoordinate(boardSize, getAllTakenCoordinates()),
                Direction.RIGHT,
                player,
                randomGenerator.randomColor()
        );
    }

    private Set<Coordinates> getAllTakenCoordinates() {
        var takenCoordinates = new HashSet<>(apples);
        playerSnakes.values().stream().map(Snake::getParts).forEach(takenCoordinates::addAll);
        return takenCoordinates;
    }

    private void removeExtraTailsExcluding(Set<Snake> excludedSnakes, GameDelta delta) {
        var tailRemovalSnakes = new HashSet<>(playerSnakes.values());
        tailRemovalSnakes.removeAll(excludedSnakes);
        for (var snake : tailRemovalSnakes) {
            delta.removeSnakePart(snake.getId(), snake.removeTail());
        }
    }


    private Set<Snake> getAppleEatingSnakes(Collection<Snake> snakes) {
        return snakes.stream().filter(e -> apples.contains(e.getHead())).collect(Collectors.toSet());
    }

    public void handleInput(Player player, PlayerInput input) {
        var snake = playerSnakes.get(player);
        if (snake != null) {
            snake.changeDirection(input.direction());
        }
    }

    public Collection<Snake> getSnakes() {
        return playerSnakes.values();
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void start() {
        status = RUNNING;
    }


    public void pause() {
        status = PAUSED;
    }
}

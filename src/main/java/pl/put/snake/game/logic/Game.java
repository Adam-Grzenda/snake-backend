package pl.put.snake.game.logic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.put.snake.game.logic.board.CollisionDetector;
import pl.put.snake.game.logic.board.RandomGenerator;
import pl.put.snake.game.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static pl.put.snake.game.logic.Game.GameStatus.*;


@Slf4j
@Getter
public class Game {

    public enum GameStatus {
        NEW,
        RUNNING,
        FINISHED
    }

    private final UUID id;

    private final Set<Coordinates> apples;
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final int boardSize;
    private final Map<Player, Snake> playerSnakes;
    private final Set<Player> players;
    private GameStatus status = NEW;

    public Game(int boardSize, RandomGenerator randomGenerator, CollisionDetector collisionDetector) {
        this.randomGenerator = randomGenerator;
        this.apples = new HashSet<>();
        this.boardSize = boardSize;
        this.collisionDetector = collisionDetector;
        this.id = UUID.randomUUID();
        this.playerSnakes = new HashMap<>();
        this.players = new HashSet<>();
    }

    public StepResult step() {
        if (status != RUNNING) {
            throw new IllegalStateException("Game is not in running state");
        }
        var delta = new GameDelta();

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
        delta.removeSnakes(collidedSnakes);


        if (playerSnakes.isEmpty()) {
            status = FINISHED;
        }

        delta.setStatus(status);
        return StepResult.of(collidedSnakes, delta);
    }

    public GameDelta join(Player player) {
        var delta = new GameDelta();
        var snake = playerSnakes.computeIfAbsent(player, p -> createNewSnake(player));
        players.add(player);
        delta.addSnake(snake);
        delta.addSnakePart(snake.getId(), snake.getHead());
        delta.setStatus(status);
        return delta;
    }

    private Snake createNewSnake(Player player) {
        return new Snake(
                playerSnakes.size(),
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
        if (snake == null) {
            throw new IllegalStateException("Snake for player: " + player.id() + " does not exist");
        }
        snake.changeDirection(input.direction());
    }

    public Collection<Snake> getSnakes() {
        return playerSnakes.values();
    }

    public Collection<Player> getPlayers() {
        return playerSnakes.keySet();
    }

    public void start() {
        status = RUNNING;
    }

    public void end() {
        status = FINISHED;
    }
}

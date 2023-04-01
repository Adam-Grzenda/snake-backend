package pl.put.snake.game.logic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.put.snake.game.logic.board.CollisionDetector;
import pl.put.snake.game.logic.board.RandomGenerator;
import pl.put.snake.game.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Getter
public class Game {

    private final UUID id;
    private final Set<Snake> snakes;
    private final Set<Coordinates> apples;
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final int boardSize;
    private final Set<Player> players;

    public Game(int boardSize, RandomGenerator randomGenerator, CollisionDetector collisionDetector) {
        this.randomGenerator = randomGenerator;
        this.snakes = new HashSet<>();
        this.apples = new HashSet<>();
        this.players = new HashSet<>();
        this.boardSize = boardSize;
        this.collisionDetector = collisionDetector;
        this.id = UUID.randomUUID();
    }

    public StepResult step() {
        var delta = new BoardDelta();

        for (var snake : snakes) {
            delta.addSnakePart(snake.moveHead());
        }

        var appleEatingSnakes = getAppleEatingSnakes(snakes);
        removeExtraTailsExcluding(appleEatingSnakes, delta);

        if (!appleEatingSnakes.isEmpty()) {
            appleEatingSnakes.stream().map(Snake::getHead).toList().forEach(apple -> {
                apples.remove(apple);
                delta.removeApple(apple);
            });

            var newApple = randomGenerator.generateFreeCoordinate(boardSize, getAllTakenCoordinates());
            apples.add(newApple);
            delta.addApple(newApple);
        }

        var collidedSnakes = collisionDetector.detectCollisions(snakes, boardSize);
        if (!collidedSnakes.isEmpty()) {
            return StepResult.endGame(collidedSnakes, delta);
        }

        return StepResult.ok(delta);
    }

    public Snake join(Player player) {
        var oldSnake = snakes.stream().filter(snake -> snake.getPlayerId().equals(player.id())).findFirst();
        players.add(player);
        return oldSnake.orElseGet(() -> createNewSnake(player.id()));
    }

    private Snake createNewSnake(UUID id) {
        var snake = new Snake(
                randomGenerator.generateFreeCoordinate(boardSize, getAllTakenCoordinates()),
                Direction.RIGHT,
                id
        );
        snakes.add(snake);
        return snake;
    }

    private Set<Coordinates> getAllTakenCoordinates() {
        var takenCoordinates = new HashSet<>(apples);
        snakes.stream().map(Snake::getParts).forEach(takenCoordinates::addAll);
        return takenCoordinates;
    }

    private void removeExtraTailsExcluding(Set<Snake> excludedSnakes, BoardDelta delta) {
        var tailRemovalSnakes = new HashSet<>(snakes);
        tailRemovalSnakes.removeAll(excludedSnakes);
        for (var snake : tailRemovalSnakes) {
            delta.removeSnakePart(snake.removeTail());
        }
    }


    private Set<Snake> getAppleEatingSnakes(Set<Snake> snakes) {
        return snakes.stream().filter(e -> apples.contains(e.getHead())).collect(Collectors.toSet());
    }

    public Board getBoard() {
        return Board.fromGame(this);
    }

}

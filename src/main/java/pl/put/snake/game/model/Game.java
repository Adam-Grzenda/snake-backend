package pl.put.snake.game.model;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import pl.put.snake.game.utils.CollisionDetector;
import pl.put.snake.game.utils.RandomGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.put.snake.game.model.StepResult.ResultType.SNAKE_COLLISION;


@Log4j2
@Getter
public class Game {

    private final UUID id;
    private final Set<Snake> snakes;
    private final Set<Coordinates> apples;
    private final CollisionDetector collisionDetector;
    private final RandomGenerator randomGenerator;
    private final int boardSize;
    private int players;

    public Game(int boardSize, RandomGenerator randomGenerator, CollisionDetector collisionDetector) {
        this.randomGenerator = randomGenerator;
        this.snakes = new HashSet<>();
        this.apples = new HashSet<>();
        this.boardSize = boardSize;
        this.collisionDetector = collisionDetector;
        this.players = 0;
        this.id = UUID.randomUUID();
    }

    public StepResult step() {
        for (var snake : snakes) {
            snake.moveHead();
        }

        var appleEatingSnakes = getAppleEatingSnakes(snakes);
        removeExtraTailsExcluding(appleEatingSnakes);

        if (!appleEatingSnakes.isEmpty()) {
            removeEatenApples(appleEatingSnakes);
            var newApple = randomGenerator.generateFreeCoordinate(boardSize, getAllTakenCoordinates());
            apples.add(newApple);
        }

        var collidedSnakes = collisionDetector.detectCollisions(snakes, boardSize);
        if (!collidedSnakes.isEmpty()) {
            return new StepResult(SNAKE_COLLISION, collidedSnakes);
        }

        return StepResult.ok();
    }

    public Snake join(Player player) {
        var oldSnake = snakes.stream().filter(snake -> snake.getPlayerId().equals(player.id())).findFirst();
        return oldSnake.orElseGet(() -> createNewSnake(player.id()));
    }

    private Snake createNewSnake(UUID id) {
        var snake = new Snake(
                players++,
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

    private void removeEatenApples(Set<Snake> appleEatingSnakes) {
        appleEatingSnakes.stream().map(Snake::getHead).toList().forEach(apples::remove);
    }

    private void removeExtraTailsExcluding(Set<Snake> excludedSnakes) {
        var tailRemovalSnakes = new HashSet<>(snakes);
        tailRemovalSnakes.removeAll(excludedSnakes);
        for (var snake : tailRemovalSnakes) {
            snake.removeTail();
        }
    }


    private Set<Snake> getAppleEatingSnakes(Set<Snake> snakes) {
        return snakes.stream().filter(e -> apples.contains(e.getHead())).collect(Collectors.toSet());
    }


}

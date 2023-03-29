package pl.put.snake.game.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.Snake;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Component
public class CollisionDetector {

    public Set<Snake> detectCollisions(Set<Snake> snakes, int boardSize) {
        var collidedWallSnakes = getCollidedWithWallSnakes(snakes, boardSize);
        var collidedSnakes = getCollidedWithEachOtherSnakes(snakes);

        var allCollidedSnakes = new HashSet<>(collidedSnakes);
        allCollidedSnakes.addAll(collidedWallSnakes);
        return allCollidedSnakes;
    }

    private Set<Snake> getCollidedWithWallSnakes(Set<Snake> snakes, int boardSize) {
        return snakes.stream()
                .filter(snake -> isCollidedWithWall(snake.getHead(), boardSize))
                .collect(Collectors.toSet());
    }

    private boolean isCollidedWithWall(Coordinates coordinates, int boardSize) {
        return (coordinates.x() >= boardSize || coordinates.y() >= boardSize) || (coordinates.x() <= 0 || coordinates.y() <= 0);
    }

    private Set<Snake> getCollidedWithEachOtherSnakes(Set<Snake> snakes) {
        var collided = new HashSet<Snake>();
        for (var snake : snakes) {
            for (var otherSnake : snakes) {
                if (hasCollision(snake, otherSnake)) {
                    collided.add(snake);
                }
            }
        }
        log.info("Collided: {}", String.join(" ", collided.stream().map(Object::toString).toList()));
        return collided;
    }

    private boolean hasCollision(Snake snake, Snake otherSnake) {
        return otherSnake.getParts().contains(snake.getHead());
    }
}

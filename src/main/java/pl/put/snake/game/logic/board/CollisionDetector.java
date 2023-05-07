package pl.put.snake.game.logic.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.snake.Snake;
import pl.put.snake.game.utils.LoggingUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CollisionDetector {

    public Set<Snake> detectCollisions(Collection<Snake> snakes, int boardSize) {
        var collidedWallSnakes = getCollidedWithWallSnakes(snakes, boardSize);
        var collidedSnakes = getCollidedWithEachOtherSnakes(snakes);

        var allCollidedSnakes = new HashSet<>(collidedSnakes);
        allCollidedSnakes.addAll(collidedWallSnakes);
        return allCollidedSnakes;
    }

    private Set<Snake> getCollidedWithWallSnakes(Collection<Snake> snakes, int boardSize) {
        var collidedSnakes = snakes.stream()
                .filter(snake -> isCollidedWithWall(snake.getHead(), boardSize))
                .collect(Collectors.toSet());

        if (!collidedSnakes.isEmpty()) {
            log.info("Collided: {} with wall", LoggingUtils.setToString(collidedSnakes));
        }
        return collidedSnakes;
    }

    private boolean isCollidedWithWall(Coordinates coordinates, int boardSize) {
        return (coordinates.x() >= boardSize || coordinates.y() >= boardSize) || (coordinates.x() < 0 || coordinates.y() < 0);
    }

    private Set<Snake> getCollidedWithEachOtherSnakes(Collection<Snake> snakes) {
        var collided = new HashSet<Snake>();
        for (var snake : snakes) {
            for (var otherSnake : snakes) {
                if (hasCollision(snake, otherSnake)) {
                    collided.add(snake);
                }
            }
        }

        if (!collided.isEmpty()) {
            log.info("Collided: {}", LoggingUtils.setToString(collided));
        }

        return collided;
    }

    private boolean hasCollision(Snake snake, Snake otherSnake) {
        if (snake.getId() == otherSnake.getId()) {
            //Snake is collided with itself when more than one part has same coordinates as its head
            var collidedParts = otherSnake
                    .getParts()
                    .stream()
                    .filter(part -> Objects.equals(snake.getHead(), part))
                    .toList();
            return collidedParts.size() > 1;
        }

        return otherSnake.getParts().contains(snake.getHead());
    }
}

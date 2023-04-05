package pl.put.snake.game.model;

import java.util.Set;

import static pl.put.snake.game.utils.LoggingUtils.setToString;

public record StepResult(
        Set<Snake> collidedSnakes,
        GameDelta gameDelta
) {
    @Override
    public String toString() {
        return "StepResult{" +
               "collidedSnakes=" + setToString(collidedSnakes) +
               ", boardDelta=" + gameDelta +
               '}';
    }

    public static StepResult of(Set<Snake> collidedSnakes, GameDelta delta) {
        return new StepResult(collidedSnakes, delta);
    }

}

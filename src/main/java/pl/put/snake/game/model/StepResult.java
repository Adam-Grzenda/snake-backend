package pl.put.snake.game.model;

import java.util.Set;

public record StepResult(
        Set<Snake> collidedSnakes,
        GameDelta gameDelta
) {

    public static StepResult of(Set<Snake> collidedSnakes, GameDelta delta) {
        return new StepResult(collidedSnakes, delta);
    }

}

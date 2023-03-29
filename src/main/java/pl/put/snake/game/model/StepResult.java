package pl.put.snake.game.model;

import java.util.Collections;
import java.util.Set;

public record StepResult(
        ResultType type,
        Set<Snake> collidedSnakes
) {
    public enum ResultType {
        SNAKE_COLLISION, OK
    }

    private static final StepResult OK = new StepResult(ResultType.OK, Collections.emptySet());

    public static StepResult ok() {
        return OK;
    }

}

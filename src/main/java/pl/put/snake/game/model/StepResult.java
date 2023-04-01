package pl.put.snake.game.model;

import java.util.Collections;
import java.util.Set;

import static pl.put.snake.game.utils.LoggingUtils.setToString;

public record StepResult(
        ResultType type,
        Set<Snake> collidedSnakes,
        BoardDelta boardDelta
) {
    @Override
    public String toString() {
        return "StepResult{" +
               "type=" + type +
               ", collidedSnakes=" + setToString(collidedSnakes) +
               ", boardDelta=" + boardDelta +
               '}';
    }

    public enum ResultType {
        END_GAME, OK
    }

    public static StepResult endGame(Set<Snake> collidedSnakes, BoardDelta delta) {
        return new StepResult(ResultType.END_GAME, collidedSnakes, delta);
    }

    public static StepResult ok(BoardDelta delta) {
        return new StepResult(ResultType.OK, Collections.emptySet(), delta);
    }


}

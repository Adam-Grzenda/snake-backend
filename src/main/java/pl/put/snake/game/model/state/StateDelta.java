package pl.put.snake.game.model.state;

import lombok.Getter;
import pl.put.snake.game.logic.Game;

@Getter
public abstract class StateDelta {
    private final int gameId;

    protected StateDelta(Game game) {
        this.gameId = game.getId();
    }
}

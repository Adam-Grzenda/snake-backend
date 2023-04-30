package pl.put.snake.game.model.state;

import lombok.Getter;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.model.snake.Snake;

@Getter
public class PauseGameDelta extends StateDelta {
    private final Snake snake;

    public PauseGameDelta(Game game, Snake snake) {
        super(game);
        this.snake = snake;
    }
}

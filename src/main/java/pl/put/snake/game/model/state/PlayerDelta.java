package pl.put.snake.game.model.state;

import lombok.Getter;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.snake.Snake;


@Getter
public class PlayerDelta extends StateDelta {

    private final Player player;
    private final Snake snake;

    public PlayerDelta(Game game, Player player, Snake snake) {
        super(game);
        this.player = player;
        this.snake = snake;
    }


}

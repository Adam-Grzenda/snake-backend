package pl.put.snake.game.logic.board;

import pl.put.snake.game.model.GameDelta;
import pl.put.snake.game.model.Player;

import java.io.IOException;
import java.util.Collection;

public interface GameDeltaListener {
    void update(Collection<Player> players, GameDelta delta) throws IOException;
}

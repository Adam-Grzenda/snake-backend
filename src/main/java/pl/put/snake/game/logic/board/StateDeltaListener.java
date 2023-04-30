package pl.put.snake.game.logic.board;

import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.state.StateDelta;

import java.io.IOException;
import java.util.Collection;

public interface StateDeltaListener {
    void update(Collection<Player> players, StateDelta delta) throws IOException;
}

package pl.put.snake.game.logic.board;

import pl.put.snake.game.model.BoardDelta;
import pl.put.snake.game.model.Player;

import java.io.IOException;
import java.util.Set;

public interface BoardDeltaObserver {

    void update(Set<Player> players, BoardDelta delta) throws IOException;
}

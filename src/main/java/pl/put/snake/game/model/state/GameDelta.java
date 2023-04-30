package pl.put.snake.game.model.state;

import lombok.Getter;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.logic.Game.GameStatus;
import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.snake.Snake;

import java.util.HashSet;
import java.util.Set;

import static pl.put.snake.game.model.state.GameDelta.BoardElement.Operation.*;

@Getter
public class GameDelta extends StateDelta {
    private static final int APPLE_ELEMENT_ID = 255;

    private GameStatus status;
    private final Set<BoardElement> changedElements = new HashSet<>();

    public GameDelta(Game game) {
        super(game);
    }

    public void addSnakePart(int snakeId, Coordinates coordinates) {
        changedElements.add(new BoardElement(snakeId, ADD, coordinates));
    }

    public void removeSnakePart(int snakeId, Coordinates coordinates) {
        changedElements.add(new BoardElement(snakeId, REMOVE, coordinates));
    }

    public void addApple(Coordinates coordinates) {
        changedElements.add(new BoardElement(APPLE_ELEMENT_ID, ADD, coordinates));
    }

    public void removeApple(Coordinates coordinates) {
        changedElements.add(new BoardElement(APPLE_ELEMENT_ID, REMOVE, coordinates));
    }

    public void removeSnake(Snake snake) {
        snake.getParts()
                .stream()
                .map(coordinates -> new BoardElement(snake.getId(), LOST, coordinates))
                .forEach(changedElements::add);
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public record BoardElement(
            int id,
            Operation operation,
            Coordinates coordinates
    ) {
        public enum Operation {
            ADD,
            REMOVE,
            LOST
        }

    }
}

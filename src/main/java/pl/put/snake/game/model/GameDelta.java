package pl.put.snake.game.model;

import pl.put.snake.game.logic.Game.GameStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static pl.put.snake.game.model.BoardElement.ElementType.APPLE;
import static pl.put.snake.game.model.BoardElement.ElementType.SNAKE;
import static pl.put.snake.game.utils.LoggingUtils.setToString;

public class GameDelta {
    private GameStatus status;
    private final Set<BoardElement> removeParts = new HashSet<>();
    private final Set<BoardElement> addParts = new HashSet<>();

    public void addSnakePart(Coordinates coordinates) {
        addParts.add(new BoardElement(SNAKE, coordinates));
    }

    public void removeSnakePart(Coordinates coordinates) {
        removeParts.add(new BoardElement(SNAKE, coordinates));
    }

    public void addApple(Coordinates coordinates) {
        addParts.add(new BoardElement(APPLE, coordinates));
    }

    public void removeApple(Coordinates coordinates) {
        removeParts.add(new BoardElement(APPLE, coordinates));
    }

    public Set<BoardElement> getAddParts() {
        return Collections.unmodifiableSet(addParts);
    }

    public Set<BoardElement> getRemoveParts() {
        return Collections.unmodifiableSet(removeParts);
    }


    @Override
    public String toString() {
        return "BoardDelta{" + "removeParts={" + setToString(removeParts) + "}, addParts=" + setToString(addParts) + '}';
    }

    public GameStatus setStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }


}

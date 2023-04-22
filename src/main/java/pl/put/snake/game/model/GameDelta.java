package pl.put.snake.game.model;

import lombok.Getter;
import pl.put.snake.game.dto.SnakeDto;
import pl.put.snake.game.logic.Game.GameStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.put.snake.game.model.BoardElement.ElementType.APPLE;
import static pl.put.snake.game.model.BoardElement.ElementType.SNAKE;

@Getter
public class GameDelta {
    private GameStatus status;
    private final Set<BoardElement> removeParts = new HashSet<>();
    private final Set<BoardElement> addParts = new HashSet<>();
    private final Set<SnakeDto> addSnakes = new HashSet<>();
    private final Set<SnakeDto> removeSnakes = new HashSet<>();

    public void addSnakePart(int snakeId, Coordinates coordinates) {
        addParts.add(new BoardElement(snakeId, SNAKE, coordinates));
    }

    public void removeSnakePart(int snakeId, Coordinates coordinates) {
        removeParts.add(new BoardElement(snakeId, SNAKE, coordinates));
    }

    public void addApple(Coordinates coordinates) {
        addParts.add(new BoardElement(null, APPLE, coordinates));
    }

    public void removeApple(Coordinates coordinates) {
        removeParts.add(new BoardElement(null, APPLE, coordinates));
    }

    public void addSnake(Snake snake) {
        addSnakes.add(SnakeDto.from(snake));
    }

    public void removeSnakes(Collection<Snake> snakes) {
        removeSnakes.addAll(snakes.stream().map(SnakeDto::from).collect(Collectors.toSet()));

        for (var snake : snakes) {
            snake.getParts().forEach(part -> removeSnakePart(snake.getId(), part));
        }
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}

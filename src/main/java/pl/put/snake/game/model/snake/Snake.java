package pl.put.snake.game.model.snake;

import pl.put.snake.game.model.Coordinates;
import pl.put.snake.game.model.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Snake {
    private final Player player;
    private final short id;
    private final ArrayDeque<Coordinates> parts;
    private final Color color;
    private Direction direction;

    public Snake(short id, Coordinates head, Direction initialDirection, Player player, Color color) {
        this.id = id;
        this.player = player;
        this.color = color;
        this.parts = new ArrayDeque<>();
        parts.push(head);
        this.direction = initialDirection;
    }

    public Coordinates moveHead() {
        var nextHead = getHead().next(direction);
        parts.push(nextHead);
        return nextHead;
    }

    public Coordinates removeTail() {
        return parts.removeLast();
    }

    public void changeDirection(Direction newDirection) {
        if (direction == newDirection.opposite()) {
            return;
        }
        this.direction = newDirection;
    }

    public Coordinates getHead() {
        return parts.getFirst();
    }

    public List<Coordinates> getParts() {
        return new ArrayList<>(parts);
    }

    public Player getPlayer() {
        return player;
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

}

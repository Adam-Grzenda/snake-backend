package pl.put.snake.game.model;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class Snake {
    private final Player player;
    private final ArrayDeque<Coordinates> parts;
    private Direction direction;

    public Snake(Coordinates head, Direction initialDirection, Player player) {
        this.player = player;
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

    public void changeDirection(Direction direction) {
        this.direction = direction;
    }

    public Coordinates getHead() {
        return parts.getFirst();
    }

    public Set<Coordinates> getParts() {
        return new HashSet<>(parts);
    }

    public Player getPlayer() {
        return player;
    }
}

package pl.put.snake.game.model;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class Snake {
    private final Player player;
    private final int id;
    private final ArrayDeque<Coordinates> parts;
    private final Color color;
    private Direction direction;

    public Snake(int id, Coordinates head, Direction initialDirection, Player player, Color color) {
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

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
}

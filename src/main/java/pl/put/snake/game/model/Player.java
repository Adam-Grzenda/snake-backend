package pl.put.snake.game.model;

import java.util.UUID;

public record Player(
        UUID id,
        String name
) {
    public String stringId() {
        return id.toString();
    }
}

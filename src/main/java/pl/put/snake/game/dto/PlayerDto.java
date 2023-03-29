package pl.put.snake.game.dto;

import pl.put.snake.game.model.Player;

public record PlayerDto(
        String id,
        String name
) {
    public static PlayerDto from(Player player) {
        return new PlayerDto(player.id().toString(), player.name());
    }
}

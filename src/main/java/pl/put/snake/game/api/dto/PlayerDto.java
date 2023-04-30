package pl.put.snake.game.api.dto;

import pl.put.snake.game.model.Player;

public record PlayerDto(
        String id,
        String name
) {
    public static PlayerDto from(Player player) {
        return new PlayerDto(player.stringId(), player.name());
    }
}

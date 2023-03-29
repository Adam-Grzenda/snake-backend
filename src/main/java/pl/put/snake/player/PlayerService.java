package pl.put.snake.player;

import org.springframework.stereotype.Service;
import pl.put.snake.game.dto.PlayerDto;
import pl.put.snake.game.model.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    private final Map<String, Player> players = new HashMap<>();

    public PlayerDto createPlayer(PlayerDto playerRequest) {
        var player = new Player(UUID.randomUUID(), playerRequest.name());
        players.put(player.id().toString(), player);
        return PlayerDto.from(player);
    }

    public Optional<Player> findPlayerById(String playerId) {
        return Optional.ofNullable(players.get(playerId));
    }

}

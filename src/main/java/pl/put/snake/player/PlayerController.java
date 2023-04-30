package pl.put.snake.player;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.snake.game.api.dto.PlayerDto;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public PlayerDto createPlayer(@RequestBody PlayerDto playerRequest) {
        return playerService.createPlayer(playerRequest);
    }
}

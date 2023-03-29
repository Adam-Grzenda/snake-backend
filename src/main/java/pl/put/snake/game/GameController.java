package pl.put.snake.game;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.put.snake.game.dto.GameDto;
import pl.put.snake.game.dto.SnakeDto;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public GameDto createGame(@RequestBody GameDto gameRequest) {
        return gameService.createGame(gameRequest);
    }

    @PostMapping("/{gameId}/players/{playerId}")
    public SnakeDto joinGame(@PathVariable String gameId, @PathVariable String playerId) {
        return gameService.joinGame(gameId, playerId);
    }

    @PostMapping("/{gameId}")
    public void startGame(@PathVariable String gameId) {
        gameService.startGame(gameId);
    }

}

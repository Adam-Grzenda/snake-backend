package pl.put.snake.game.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.put.snake.game.api.dto.GameDto;

import java.util.Optional;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto createGame(@RequestBody GameDto gameRequest) {
        return gameService.createGame(gameRequest);
    }

    @GetMapping("/{gameId}")
    public Optional<GameDto> findGameById(@PathVariable String gameId) {
        return gameService.findGameById(gameId);
    }

    @PostMapping("/{gameId}/players/{playerId}")
    public GameDto joinGame(@PathVariable String gameId, @PathVariable String playerId) {
        return gameService.joinGame(gameId, playerId);
    }

    @PostMapping("/{gameId}")
    public void startGame(@PathVariable String gameId) {
        gameService.startGame(gameId);
    }

}

package pl.put.snake.game.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.put.snake.game.api.dto.GameDto;
import pl.put.snake.game.api.dto.PlayerDto;

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

    @PostMapping("/{gameId}/start")
    public void startGame(@PathVariable String gameId,
                          @RequestParam(required = false, defaultValue = "500") Integer stepMillis) {
        gameService.startGame(gameId, stepMillis);
    }

    @PostMapping("/{gameId}/pause")
    public void pauseGame(@PathVariable String gameId, @RequestBody PlayerDto playerDto) {
        gameService.pauseGame(gameId, playerDto.id());
    }

    @PostMapping("/{gameId}/resume")
    public void resumeGame(@PathVariable String gameId,
                           @RequestParam(required = false, defaultValue = "500") Integer stepMillis) {
        gameService.resumeGame(gameId, stepMillis);
    }


}

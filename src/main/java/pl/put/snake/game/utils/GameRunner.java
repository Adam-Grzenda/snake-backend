package pl.put.snake.game.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pl.put.snake.game.api.GameService;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.model.state.FinishedGameDelta;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static pl.put.snake.game.logic.Game.GameStatus.FINISHED;
import static pl.put.snake.game.logic.Game.GameStatus.PAUSED;

@Slf4j
@RequiredArgsConstructor
public class GameRunner {

    private final GameService gameService;

    public void submit(Game game, int stepIntervalMillis) {
        Thread.ofVirtual()
                .name("Game " + game.getId())
                .start(() -> run(game, stepIntervalMillis));
    }


    @SneakyThrows
    private void run(Game game, int stepIntervalMillis) {
        game.start();

        while (true) {
            var start = Instant.now();
            var expectedEnd = start.plusMillis(stepIntervalMillis);

            if (game.getStatus() != PAUSED) {
                var delta = game.step();
                gameService.broadcastDelta(game, delta);
                if (delta.getStatus() == FINISHED) {
                    gameService.broadcastDelta(game, new FinishedGameDelta(game));
                    return;
                }
            }

            Instant end = Instant.now();
            if (end.isBefore(expectedEnd)) {
                Thread.sleep(end.until(expectedEnd, ChronoUnit.MILLIS));
            } else {
                log.error("Step interval exceeded, end: {}, expectedEnd: {}", end, expectedEnd);
            }


        }
    }
}


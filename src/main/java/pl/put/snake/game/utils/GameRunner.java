package pl.put.snake.game.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pl.put.snake.game.api.GameService;
import pl.put.snake.game.logic.Game;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static pl.put.snake.game.logic.Game.GameStatus.FINISHED;

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
            var result = game.step();
            log.info("{}", result);

            Instant end = Instant.now();
            log.info("Game iteration finished in: {}", Duration.between(start, end).toMillis());
            if (end.isBefore(expectedEnd)) {
                Thread.sleep(end.until(expectedEnd, ChronoUnit.MILLIS));
            } else {
                log.error("Step interval exceeded, end: {}, expectedEnd: {}", end, expectedEnd);
            }

            gameService.updateDelta(game, result.gameDelta());

            if (result.gameDelta().getStatus() == FINISHED) {
                log.info("Game finished: {}", result);
                game.end();
                return;
            }
        }
    }
}


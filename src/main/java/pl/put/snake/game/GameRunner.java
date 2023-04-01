package pl.put.snake.game;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.put.snake.game.logic.Game;
import pl.put.snake.game.logic.InvalidGameStateException;
import pl.put.snake.game.logic.board.BoardDeltaObserver;
import pl.put.snake.game.model.StepResult;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static pl.put.snake.game.model.StepResult.ResultType.END_GAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRunner {

    private final List<BoardDeltaObserver> deltaObservers;

    public void submit(Game game, int stepIntervalMillis) {
        Thread.ofVirtual()
                .name("Game " + game.getId())
                .start(() -> run(game, stepIntervalMillis));
    }


    @SneakyThrows
    private void run(Game game, int stepIntervalMillis) {
        while (true) {
            Instant start = Instant.now();
            Instant expectedEnd = start.plusMillis(stepIntervalMillis);
            StepResult result = game.step();
            log.info("{}", result);

            if (result.type() == END_GAME) {
                log.info("Somebody crashed: {}", result);
                return;
            }

            Instant end = Instant.now();
            log.info("Game iteration finished in: {}", Duration.between(start, end).toMillis());
            if (end.isBefore(expectedEnd)) {
                Thread.sleep(end.until(expectedEnd, ChronoUnit.MILLIS));
            } else {
                log.error("Step interval exceeded, end: {}, expectedEnd: {}", end, expectedEnd);
            }

            deltaObservers.forEach(deltaObserver -> {
                try {
                    deltaObserver.update(game.getPlayers(), result.boardDelta());
                } catch (IOException e) {
                    log.error("Failed to notify delta observers");
                    throw new InvalidGameStateException(e);
                }
            });
        }
    }

}

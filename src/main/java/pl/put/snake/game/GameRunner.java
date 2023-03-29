package pl.put.snake.game;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.put.snake.game.model.Game;
import pl.put.snake.game.model.StepResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static pl.put.snake.game.model.StepResult.ResultType.SNAKE_COLLISION;

@Log4j2
@Service
public class GameRunner {

    private boolean shouldRun = true;

    public void submit(Game game, int stepIntervalMillis) {
        Thread.ofVirtual()
                .name("Game " + game.getId())
                .start(() -> run(game, stepIntervalMillis));
    }


    @SneakyThrows
    private void run(Game game, int stepIntervalMillis) {
        while (shouldRun) {
            Instant expectedEnd = Instant.now().plusMillis(stepIntervalMillis);
            StepResult result = game.step();

            if (result.type() == SNAKE_COLLISION) {
                log.info("Somebody crashed: {}", result);
                return;
            }

            Instant end = Instant.now();
            if (end.isBefore(expectedEnd)) {
                Thread.sleep(end.until(expectedEnd, ChronoUnit.MILLIS));
            } else {
                log.error("Step interval exceeded, end: {}, expectedEnd: {}", end, expectedEnd);
                shouldRun = false;
            }
        }
    }

}

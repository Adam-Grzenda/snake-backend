package pl.put.snake.game.utils;

import org.springframework.stereotype.Component;
import pl.put.snake.game.model.Coordinates;

import java.util.Random;
import java.util.Set;

@Component
public class RandomGenerator {
    private final Random random = new Random();

    public Coordinates generateFreeCoordinate(int boardSize, Set<Coordinates> takenCoordinates) {
        var result = generateRandomCoordinates(boardSize);
        while (takenCoordinates.contains(result)) {
            result = generateRandomCoordinates(boardSize);
        }
        return result;
    }

    private Coordinates generateRandomCoordinates(int range) {
        return new Coordinates(random.nextInt(0, range), random.nextInt(0, range));
    }

}

package pl.put.snake.game.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtils {
    public static String setToString(Set<?> elements) {
        return elements.stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}

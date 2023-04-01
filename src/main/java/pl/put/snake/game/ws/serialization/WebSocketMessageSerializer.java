package pl.put.snake.game.ws.serialization;

@FunctionalInterface
public interface WebSocketMessageSerializer {
    byte[] serialize(Object payload);
}

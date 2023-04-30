package pl.put.snake.game.ws.serialization;

import org.springframework.web.socket.WebSocketMessage;
import pl.put.snake.game.model.PlayerInput;

public interface WebSocketMessageMapper<T> {
    WebSocketMessage<T> serialize(Object payload);

    PlayerInput deserialize(WebSocketMessage<?> message);
}

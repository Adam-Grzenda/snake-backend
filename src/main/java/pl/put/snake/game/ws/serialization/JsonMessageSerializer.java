package pl.put.snake.game.ws.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import pl.put.snake.game.model.PlayerInput;

@Component
@ConditionalOnProperty(value = "websocket.serializer", havingValue = "json")
@RequiredArgsConstructor
public class JsonMessageSerializer implements WebSocketMessageMapper<String> {

    private final ObjectMapper mapper;

    @Override
    public WebSocketMessage<String> serialize(Object payload) {
        try {
            return new TextMessage(mapper.writeValueAsBytes(payload));
        } catch (JsonProcessingException e) {
            throw new MessageMappingException(e);
        }
    }

    @Override
    public PlayerInput deserialize(WebSocketMessage<?> message) {
        try {
            return mapper.readValue(((String) message.getPayload()), PlayerInput.class);
        } catch (JsonProcessingException e) {
            throw new MessageMappingException(e);
        }
    }

}

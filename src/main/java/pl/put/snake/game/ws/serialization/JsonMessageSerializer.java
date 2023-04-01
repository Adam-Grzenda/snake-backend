package pl.put.snake.game.ws.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "websocket.serializer", havingValue = "json")
@RequiredArgsConstructor
public class JsonMessageSerializer implements WebSocketMessageSerializer {

    private final ObjectMapper mapper;

    @Override
    public byte[] serialize(Object payload) {
        try {
            return mapper.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }
}

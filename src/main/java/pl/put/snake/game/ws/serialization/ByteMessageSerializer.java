package pl.put.snake.game.ws.serialization;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketMessage;
import pl.put.snake.game.model.PlayerInput;
import pl.put.snake.game.model.snake.Direction;
import pl.put.snake.game.model.state.FinishedGameDelta;
import pl.put.snake.game.model.state.GameDelta;
import pl.put.snake.game.model.state.PauseGameDelta;
import pl.put.snake.game.model.state.PlayerDelta;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnProperty(value = "websocket.serializer", havingValue = "byte")
public class ByteMessageSerializer implements WebSocketMessageMapper<ByteBuffer> {

    @Override
    public WebSocketMessage<ByteBuffer> serialize(Object payload) {
        var bytes = switch (payload) {
            case PlayerDelta playerDelta -> mapPlayerDelta(playerDelta);
            case GameDelta gameDelta -> mapGameDelta(gameDelta);
            case PauseGameDelta pauseGameDelta -> mapPauseGameDelta(pauseGameDelta);
            case FinishedGameDelta finishedGameDelta -> mapFinishedGameDelta(finishedGameDelta);
            default -> throw new IllegalStateException("No binary payload mapping available for: " + payload);
        };

        return new BinaryMessage(bytes.array());
    }

    private ByteBuffer mapFinishedGameDelta(FinishedGameDelta delta) {
        var buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 3);
        return buffer;
    }

    private ByteBuffer mapPauseGameDelta(PauseGameDelta delta) {
        var buffer = ByteBuffer.allocate(2);
        buffer.put((byte) 2);
        buffer.put((byte) delta.getSnake().getId());
        return buffer;
    }

    private ByteBuffer mapGameDelta(GameDelta delta) {
        var changedElements = delta.getChangedElements();
        var buffer = ByteBuffer.allocate(1 + changedElements.size() * 4);

        buffer.put((byte) 1); //message type

        for (var element : changedElements) {
            buffer.put(getOperationByte(element));
            buffer.put((byte) element.id());
            buffer.put((byte) element.coordinates().x());
            buffer.put((byte) element.coordinates().y());
        }
        return buffer;
    }

    private byte getOperationByte(GameDelta.BoardElement element) {
        return switch (element.operation()) {
            case REMOVE -> (byte) 0;
            case ADD -> (byte) 1;
            case LOST -> (byte) 2;
        };
    }

    private ByteBuffer mapPlayerDelta(PlayerDelta delta) {
        byte[] playerNameASCII = delta.getPlayer().name().getBytes(StandardCharsets.US_ASCII);
        var buffer = ByteBuffer.allocate(1 + 1 + playerNameASCII.length + 6); // type + n-bytes for name + n-bytes of name + 3*color + 2 * coords

        buffer.put((byte) 0);
        buffer.put((byte) playerNameASCII.length);
        buffer.put(playerNameASCII);

        var snake = delta.getSnake();

        buffer.put((byte) snake.getId());
        buffer.put((byte) snake.getColor().r());
        buffer.put((byte) snake.getColor().g());
        buffer.put((byte) snake.getColor().b());
        buffer.put((byte) snake.getHead().x());
        buffer.put((byte) snake.getHead().y());

        return buffer;
    }

    @Override
    public PlayerInput deserialize(WebSocketMessage<?> message) {
        if (!(message.getPayload() instanceof ByteBuffer && message.getPayloadLength() == 1)) {
            throw new IllegalStateException("Invalid payload from client");
        }

        var direction = switch (((ByteBuffer) message.getPayload()).get()) {
            case (byte) 0 -> Direction.UP;
            case (byte) 1 -> Direction.DOWN;
            case (byte) 2 -> Direction.LEFT;
            case (byte) 3 -> Direction.RIGHT;
            default -> throw new IllegalStateException("Invalid payload from client");
        };

        return new PlayerInput(direction);
    }

}

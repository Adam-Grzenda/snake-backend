package pl.put.snake.game.ws;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.put.snake.game.api.GameService;
import pl.put.snake.game.logic.board.GameDeltaListener;
import pl.put.snake.game.model.GameDelta;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.ws.serialization.WebSocketMessageMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler implements GameDeltaListener {

    private final Map<String, WebSocketSession> socketSessions = new HashMap<>();
    private final WebSocketMessageMapper<String> mapper;
    private final GameService gameService;

    @PostConstruct
    public void registerDeltaListener() {
        gameService.registerDeltaListener(this);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        gameService.handlePlayerInput(getPlayerId(session), mapper.deserialize(message));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WS connection established: {}", session);
        socketSessions.put(getPlayerId(session), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WS connection closed: {}, status: {}", session, status);
        socketSessions.values().remove(session);
    }

    private String getPlayerId(WebSocketSession session) {
        var uri = session.getUri();
        if (uri == null) {
            throw new IllegalStateException("Invalid WebSocket session");
        }

        var path = uri.getPath().split("/");
        if (path.length != 3) {
            throw new IllegalStateException("Invalid WebSocket path");
        }
        return path[path.length - 1];
    }

    public void sendMessage(String sessionId, GameDelta delta) throws IOException {
        var session = socketSessions.get(sessionId);
        if (session == null) {
            throw new IOException("Cannot send update to session with id=" + sessionId + " WebSocket session not found");
        }
        session.sendMessage(mapper.serialize(delta));
    }

    @Override
    public void update(Collection<Player> players, GameDelta delta) throws IOException {
        for (var player : players) {
            sendMessage(player.stringId(), delta);
        }
    }
}

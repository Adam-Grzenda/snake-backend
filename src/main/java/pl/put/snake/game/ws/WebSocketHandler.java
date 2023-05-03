package pl.put.snake.game.ws;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import pl.put.snake.game.api.GameService;
import pl.put.snake.game.logic.board.StateDeltaListener;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.model.state.StateDelta;
import pl.put.snake.game.ws.serialization.WebSocketMessageMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler implements StateDeltaListener, org.springframework.web.socket.WebSocketHandler {
    private final Map<String, WebSocketSession> socketSessions = Collections.synchronizedMap(new HashMap<>());
    private final WebSocketMessageMapper<?> mapper;
    private final GameService gameService;

    @PostConstruct
    public void registerDeltaListener() {
        gameService.registerDeltaListener(this);
    }

    @Override
    public void update(Collection<Player> players, StateDelta delta) throws IOException {
        for (var player : players) {
            sendMessage(player.stringId(), delta);
        }
    }

    public void sendMessage(String sessionId, StateDelta delta) throws IOException {
        var session = socketSessions.get(sessionId);
        if (session != null && session.isOpen()) {
            session.sendMessage(mapper.serialize(delta));
        } else {
            log.warn("No session found for sessionId: {}", sessionId);
        }
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


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WS connection established: {}", session);
        socketSessions.put(getPlayerId(session), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        gameService.handleInput(getPlayerId(session), mapper.deserialize(message));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("WS connection closed: {}, status: {}", session, closeStatus);
        gameService.handleDisconnectedPlayer(getPlayerId(session));
        socketSessions.values().remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

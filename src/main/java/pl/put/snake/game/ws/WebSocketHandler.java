package pl.put.snake.game.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.put.snake.game.logic.board.BoardDeltaObserver;
import pl.put.snake.game.model.BoardDelta;
import pl.put.snake.game.model.Player;
import pl.put.snake.game.ws.serialization.WebSocketMessageSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler implements BoardDeltaObserver {

    private final Map<String, WebSocketSession> socketSessions = new HashMap<>();
    private final WebSocketMessageSerializer serializer;

    @Override
    public void update(Set<Player> players, BoardDelta delta) throws IOException {
        for (var player : players) {
            var playerSession = socketSessions.get(player.stringId());
            if (playerSession == null) {
                throw new IOException("Cannot send update to player with id=" + player.stringId() + " WebSocket session not found");
            }
            playerSession.sendMessage(new TextMessage(serializer.serialize(delta)));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (var socketSession : socketSessions.values()) {
            socketSession.sendMessage(new TextMessage("TEST"));
        }
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

}

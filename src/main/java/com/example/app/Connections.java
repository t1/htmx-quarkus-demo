package com.example.app;

import com.github.t1.bulmajava.basic.AbstractElement;
import com.github.t1.bulmajava.basic.Renderable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@ApplicationScoped
@ServerEndpoint("/connect/{httpSession}")
public class Connections {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    private record Connection(String httpSessionId, Session wsSession) {
        public void send(Renderable renderable) {
            wsSession.getAsyncRemote()
                    .sendObject(renderable.render(), result -> {
                        if (result.isOK()) {
                            log.debug("successfully broadcast to {}: {}", wsSession.getId(), renderable);
                        } else {
                            log.error("failed to broadcast to " + wsSession.getId() + ": " + renderable, result.getException());
                        }
                    });
        }
    }

    public boolean broadcast(String httpSessionId, Renderable renderable) {
        var foundAny = new AtomicBoolean(false);
        connections.values().stream()
                .filter(connection -> connection.httpSessionId.equals(httpSessionId))
                .peek(connection -> foundAny.set(true))
                .forEach(connection -> connection.send(renderable));
        return foundAny.get();
    }

    public void broadcast(AbstractElement<?> element) {
        connections.values().forEach(connection -> connection.send(element));
    }

    @OnOpen
    public void onOpen(Session wsSession, @PathParam("httpSession") String httpSession) {
        connections.put(wsSession.getId(), new Connection(httpSession, wsSession));
        log.info("ws-session {} started (now {}): {}", wsSession.getId(), connections.size(), httpSession);
    }

    @OnClose
    public void onClose(Session wsSession) {
        connections.remove(wsSession.getId());
        log.info("ws-session {} closed (now {})", wsSession.getId(), connections.size());
    }

    @OnError
    public void onError(Session wsSession, Throwable throwable) {
        connections.remove(wsSession.getId());
        log.error("ws-session " + wsSession.getId() + " left on error (now " + connections.size() + ")", throwable);
    }

    // this is only for debugging
    @OnMessage
    public void onMessage(Session wsSession, String message) {
        log.info("ws-session {} message: {}", wsSession.getId(), message);
    }
}

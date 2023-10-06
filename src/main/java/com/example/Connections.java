package com.example;

import com.github.t1.bulmajava.basic.AbstractElement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
@ServerEndpoint("/connect")
@SuppressWarnings("resource")
public class Connections {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void broadcast(AbstractElement<?> element) {
        sessions.values().forEach(session -> session.getAsyncRemote()
                .sendObject(element.render(), result -> {
                    if (result.isOK()) {
                        log.debug("successfully broadcast to {}: {}", session.getId(), element);
                    } else {
                        log.error("failed to broadcast to " + session.getId() + ": " + element, result.getException());
                    }
                }));
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        log.info("ws-session {} started (now {})", session.getId(), sessions.size());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        log.info("ws-session {} closed (now {})", session.getId(), sessions.size());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
        log.error("ws-session " + session.getId() + " left on error (now " + sessions.size() + ")", throwable);
    }
}

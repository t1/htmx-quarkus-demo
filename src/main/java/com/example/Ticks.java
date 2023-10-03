package com.example;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.t1.bulmajava.basic.Basic.code;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@ApplicationScoped
@ServerEndpoint("/tick")
@SuppressWarnings("resource")
public class Ticks {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Scheduled(every = "1s")
    public void tick(ScheduledExecution execution) {
        var t = LocalTime.ofInstant(execution.getFireTime().truncatedTo(SECONDS), ZoneId.systemDefault());
        var message = code(t.format(ISO_LOCAL_TIME)).id("ticker");
        sessions.values().forEach(session -> session.getAsyncRemote()
                .sendObject(message.render(), result -> {
                    if (result.isOK()) {
                        log.debug("successfully ticked " + t + " to " + session.getId());
                    } else {
                        log.error("failed to tick", result.getException());
                    }
                }));
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Session " + session.getId() + " started");
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        log.info("Session " + session.getId() + " closed");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
        log.error("Session " + session.getId() + " left on error", throwable);
    }
}

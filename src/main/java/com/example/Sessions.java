package com.example;

import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class Sessions {
    private final Set<UUID> sessions = new ConcurrentHashSet<>();

    public boolean isActive(UUID uuid) {return uuid != null && sessions.contains(uuid);}

    public UUID create() {
        var sessionId = UUID.randomUUID();
        log.info("create session {}", sessionId);
        sessions.add(sessionId);
        return sessionId;
    }

    public boolean remove(UUID uuid) {
        log.info("remove session {}", uuid);
        return uuid != null && sessions.remove(uuid);
    }
}

package com.example.app;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.time.ZoneId;

import static com.github.t1.bulmajava.basic.Basic.span;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Ticks {
    @Inject Connections connections;

    @Scheduled(every = "1s")
    public void tick(ScheduledExecution execution) {
        var t = LocalTime.ofInstant(execution.getFireTime().truncatedTo(SECONDS), ZoneId.systemDefault());
        connections.broadcast(span(t.format(ISO_LOCAL_TIME)).classes("is-family-monospace").id("ticker"));
    }
}

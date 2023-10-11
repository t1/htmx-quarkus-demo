package com.example.domain;

import static java.util.Locale.ROOT;

public enum FurnitureCategory {
    LIVING_ROOM, KITCHEN;

    @Override public String toString() {
        return name().toLowerCase(ROOT).replace("_", " ");
    }
}

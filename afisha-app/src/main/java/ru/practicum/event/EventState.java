package ru.practicum.event;

public enum EventState {

    PENDING("PENDING"),
    PUBLISHED("PUBLISHED"),
    CANCELED("CANCELED"),
    ;

    private final String name;

    EventState(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}

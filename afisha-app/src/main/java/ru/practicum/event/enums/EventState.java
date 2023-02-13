package ru.practicum.event.enums;

public enum EventState {

    PENDING("PENDING"),
    PUBLISHED("PUBLISHED"),
    CANCELED("CANCELED"),
    ;

    public final String name;

    EventState(String s) {
        name = s;
    }

}

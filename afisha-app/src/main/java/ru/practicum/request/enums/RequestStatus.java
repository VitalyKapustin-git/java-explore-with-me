package ru.practicum.request.enums;

public enum RequestStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");

    public final String name;

    RequestStatus(String s) {
        name = s;
    }

}

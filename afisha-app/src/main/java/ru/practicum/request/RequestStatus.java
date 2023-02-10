package ru.practicum.request;

public enum RequestStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");

    private final String name;

    RequestStatus(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

}

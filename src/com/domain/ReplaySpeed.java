package com.domain;

public enum ReplaySpeed {
    HALF(2000),
    NORMAL(1000),
    DOUBLE(500);

    private final int milliseconds;

    ReplaySpeed(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public int getMilliseconds() {
        return milliseconds;
    }
}

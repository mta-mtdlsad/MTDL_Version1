package com.mtms.domain;

// Creating enum class to choose speed
public enum ReplaySpeed {
    HALF, NORMAL, DOUBLE;

    // Assigning speed to each enum
    public int getReplaySpeed(){
        return switch (this) {
            case HALF -> 2000;
            case NORMAL -> 1000;
            case DOUBLE -> 500;
        };
    }
}

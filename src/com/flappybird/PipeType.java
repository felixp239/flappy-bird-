package com.flappybird;

public enum PipeType {
    DOWN(0),
    UP(1);

    private final int id;

    private PipeType(int id) {
        this.id = id;
    }
}

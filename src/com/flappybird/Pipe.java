package com.flappybird;

import java.awt.*;

public class Pipe extends Sprite {
    private final PipeType id;

    public Pipe(int y, PipeType id) {
        super(0, y);
        this.id = id;
        initPipe();
    }

    private void initPipe() {
        switch (id) {
            case DOWN -> loadImage("src/com/flappybird/lowerPipe.png");
            case UP -> loadImage("src/com/flappybird/upperPipe.png");
        }

    }
}

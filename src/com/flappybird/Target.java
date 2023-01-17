package com.flappybird;

import java.awt.*;

public class Target {
    private final static int width = 40;

    private int y;          //Midpoint between either two pipes or one pipe and screen border
    private int height;

    public Target(int y, int height) {
        this.y = y;
        this.height = height;
    }

    public Rectangle getCollider() {
        return new Rectangle(-width / 2, y - height / 2, width, height);
    }
}

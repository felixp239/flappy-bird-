package com.flappybird;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Player extends Sprite {
    private final int JUMP_SPEED = 200; // 200
    private final int DELAY = 100;
    private final float DOWN_ACCELERATION = 192; //192
    private final float COLLIDER_WIDTH_ALLOWANCE = 0.7f;
    private final float COLLIDER_HEIGHT_ALLOWANCE = 0.8f;

    private static int score = 0;

    private float v_x = 400; //100
    private float v_y = 0; //0
    private long time;
    private long prev_time;
    private long jump_time = 0;

    public Player(int x, int y) {
        super(x, y);

        initPlayer();
    }

    public static int getScore() {
        return score;
    }

    public static void addScore(int score) {
        Player.score += score;
    }

    public static void resetScore() {
        score = 0;
    }

    private void initPlayer() {
        prev_time = System.nanoTime() / 1000;
        loadImage("src/com/flappybird/bird.png");
    }

    public void move() {
        time = System.nanoTime() / 1000;
        y += (time - prev_time) * v_y / 2000000;
        v_y += (time - prev_time) * DOWN_ACCELERATION / 1000000;
        y += (time - prev_time) * v_y / 2000000;
        prev_time = time;
    }

    public Polygon getPlayerCollider() {
        double sin = v_y / Math.sqrt(v_x * v_x + v_y * v_y);
        double rads = Math.asin(sin);
        double cos = Math.cos(rads);
        int[] xpoints = new int[4];
        int[] ypoints = new int[4];
        float w = width * COLLIDER_WIDTH_ALLOWANCE;
        float h = height * COLLIDER_HEIGHT_ALLOWANCE;
        float dx = w / 2;
        float dy = h / 2;
        for (int i = 0; i < 4; i++) {
            xpoints[i] = (int) (x + dx * cos * ((i < 2) ? 1 : -1) - dy * sin * ((i % 3 == 0) ? 1 : -1));
            ypoints[i] = (int) (y + dx * sin * ((i < 2) ? 1 : -1) + dy * cos * ((i % 3 == 0) ? 1 : -1));
        }
        return new Polygon(xpoints, ypoints, 4);
    }

    public BufferedImage drawPlayer() {
        BufferedImage read = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = read.getGraphics();
        bg.drawImage(image, 0, 0, null);
        bg.dispose();
        double sin = v_y / Math.sqrt(v_x * v_x + v_y * v_y);
        double rads = Math.asin(sin);
        double cos = Math.cos(rads);
        BufferedImage bi = new BufferedImage
                ((int) Math.floor(width * cos + height * Math.abs(sin)),
                        (int) Math.floor(width * Math.abs(sin) + height * cos),
                        BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.translate(Math.max(0, height * sin), -Math.min(0, width * sin));
        at.rotate(rads, 0, 0);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(read, bi);
        return bi;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            jump();
        }
    }

    public void keyReleased(KeyEvent e) {
        //unfortunate
    }

    private void jump() {
        if (System.nanoTime() - jump_time > DELAY * 1000) {
            v_y = -JUMP_SPEED;
            jump_time = System.nanoTime();
        }
    }

    public float getV_x() {
        return v_x;
    }

    public float getDownAcceleration() {
        return DOWN_ACCELERATION;
    }

    public float getJumpSpeed() {
        return JUMP_SPEED;
    }
}

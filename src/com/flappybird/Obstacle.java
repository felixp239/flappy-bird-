package com.flappybird;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Obstacle {
    public static boolean COLLIDER_VISIBLE = false;

    private static int B_HEIGHT;

    private float x;
    private Pipe upperPipe;
    private Pipe lowerPipe;
    private boolean upperExists = false;
    private boolean isVisible = true;
    private boolean isCollected = false;
    private Target target;

    public Obstacle(int x, int yDown, int yUp, boolean upperExists) {
        this.x = x;
        this.upperExists = upperExists;

        initObstacle(yDown, yUp, upperExists);
    }

    public BufferedImage drawLowerPipe() {
        BufferedImage bi = new BufferedImage
                (lowerPipe.width, lowerPipe.height, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(lowerPipe.image, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public BufferedImage drawUpperPipe() {
        BufferedImage bi = new BufferedImage
                (upperPipe.width, upperPipe.height, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(upperPipe.image, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public void drawColliders(Graphics g) {
        g.setColor(Color.red);
        g.drawRect(getLowerPipeCollider().x, getLowerPipeCollider().y,
                getLowerPipeCollider().width, getLowerPipeCollider().height);
        g.drawRect(getTargetCollider().x, getTargetCollider().y,
                getTargetCollider().width, getTargetCollider().height);
        if (upperExists) {
            g.drawRect(getUpperPipeCollider().x, getUpperPipeCollider().y,
                    getUpperPipeCollider().width, getUpperPipeCollider().height);
        }
    }

    public static void setbHeight(int bHeight) {
        Obstacle.B_HEIGHT = bHeight;
    }

    public Pipe getUpperPipe() {
        return upperPipe;
    }

    public Pipe getLowerPipe() {
        return lowerPipe;
    }

    public void addToX(int dx) {
        x += dx;
    }

    public float getX() {
        return x;
    }

    public boolean doesUpperExists() {
        return upperExists;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private void initObstacle(int yDown, int yUp, boolean upperExists) {
        int t_y = (yDown + yUp) / 2;
        int t_h = yDown - yUp;
        if (upperExists) {
            upperPipe = new Pipe(yUp, PipeType.UP);
        }
        lowerPipe = new Pipe(yDown, PipeType.DOWN);
        target = new Target(t_y, t_h);
    }

    public Rectangle getUpperPipeCollider() {
        if (upperExists) {
            return new Rectangle((int) (x - upperPipe.width / 2), (int) (upperPipe.y - upperPipe.height),
                                        upperPipe.width, upperPipe.height);
        }
        else return new Rectangle();
    }

    public Rectangle getLowerPipeCollider() {
        return new Rectangle((int) (x - lowerPipe.width / 2), (int) (lowerPipe.y),
                lowerPipe.width, lowerPipe.height);
    }

    public Rectangle getTargetCollider() {
        Rectangle result = target.getCollider();
        result.translate((int) x, 0);
        return result;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
}

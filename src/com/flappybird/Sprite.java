package com.flappybird;

import javax.swing.*;
import java.awt.*;

public class Sprite {

    protected float x;
    protected float y;
    protected int width;
    protected int height;
    protected boolean visible;
    protected boolean colliderVisible = false;
    protected Image image;

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        visible = true;
    }

    protected void loadImage(String imageName) {
        ImageIcon ii = new ImageIcon(imageName);
        image = ii.getImage();
        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    protected void loadImage(String imageName, int scale) {
        ImageIcon ii = new ImageIcon(imageName);
        Image temp = ii.getImage();
        width = temp.getWidth(null) / scale;
        height =  temp.getHeight(null) / scale;
        image = temp.getScaledInstance(temp.getWidth(null) / scale, temp.getHeight(null) / scale, Image.SCALE_DEFAULT);

    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}
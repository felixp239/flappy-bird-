package com.flappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


public class Board extends JPanel implements ActionListener {
    private final static int EXTRA_HEIGHT = 200;

    private Timer timer;
    private Player player;
    private Spawner spawner;
    private boolean inGame;
    private final int B_WIDTH;
    private final int B_HEIGHT;
    private final int DELAY = 10;

    private Rectangle boundingBox;

    public Board(int b_WIDTH, int b_HEIGHT) {
        B_WIDTH = b_WIDTH;
        B_HEIGHT = b_HEIGHT;
        boundingBox = new Rectangle(0, -EXTRA_HEIGHT / 2, B_WIDTH, B_HEIGHT + EXTRA_HEIGHT);
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);
        inGame = true;
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        player = new Player(B_WIDTH / 2, B_HEIGHT / 2);

        initSpawner();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initNewBoard() {
        setFocusable(true);
        setBackground(Color.WHITE);
        inGame = true;

        player = new Player(B_WIDTH / 2, B_HEIGHT / 2);

        initSpawner();

        timer.restart();
    }

    private void initSpawner() {
        spawner = new Spawner(B_WIDTH, B_HEIGHT, player);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            drawObjects(g);
        } else {
            drawGameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics g) {
        if (player.isVisible()) {
            BufferedImage playerImage = player.drawPlayer();
            g.drawImage(playerImage, player.getX() - playerImage.getWidth() / 2, player.getY() - playerImage.getHeight() / 2, this);
            if (player.colliderVisible) {
                g.setColor(Color.red);
                g.drawPolygon(player.getPlayerCollider());
            }
        }

        for (Obstacle obstacle : spawner.getObstacles()) {
            Pipe lowerPipe = obstacle.getLowerPipe();
            BufferedImage lowerPipeImage = obstacle.drawLowerPipe();
            g.drawImage(lowerPipeImage, (int) (obstacle.getX() - lowerPipe.width / 2), (int) lowerPipe.y, this);
            if (obstacle.doesUpperExists()) {
                Pipe upperPipe = obstacle.getUpperPipe();
                BufferedImage upperPipeImage = obstacle.drawUpperPipe();
                g.drawImage(upperPipeImage, (int) (obstacle.getX() - upperPipe.width / 2), (int) upperPipe.y - upperPipe.height, this);
            }

            if (Obstacle.COLLIDER_VISIBLE) {
                obstacle.drawColliders(g);
            }
        }

        BufferedImage bi = drawText(String.valueOf(Player.getScore()), "Helvetica", 70, Color.BLACK);
        g.drawImage(bi, (B_WIDTH - bi.getWidth()) / 2,40 - bi.getHeight() / 2, this);
    }

    private BufferedImage drawText(String text, String fontName, int size, Color color) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font(fontName, Font.BOLD, size);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(color);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        return img;
    }

    private void drawGameOver(Graphics g) {
        drawObjects(g);
        BufferedImage bi = drawText("Game Over", "Helvetica", 80, Color.RED);
        g.drawImage(bi, (B_WIDTH - bi.getWidth()) / 2, (B_HEIGHT - bi.getHeight()) / 2, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        inGame();

        updatePlayer();
        updateSpawner();

        checkCollisions();

        repaint();
    }

    private void inGame() {
        if (!inGame) {
            timer.stop();
        }
    }

    private void updatePlayer() {
        if (player.isVisible()) {
            player.move();
        }
    }

    private void updateSpawner() {
        spawner.update((int) player.getV_x());
    }

    public void checkCollisions() {
        Polygon playerCollider = player.getPlayerCollider();
        //Rectangle r1 = player.getPlayerCollider();

        for (Obstacle obstacle : spawner.getObstacles()) {
            Rectangle lowerPipeCollider = obstacle.getLowerPipeCollider();
            Rectangle upperPipeCollider = new Rectangle();

            if (obstacle.doesUpperExists()) {
                upperPipeCollider = obstacle.getUpperPipeCollider();
            }
            if (playerCollider.intersects(lowerPipeCollider) || playerCollider.intersects(upperPipeCollider)) {
                inGame = false;
            }

            Rectangle targetCollider = obstacle.getTargetCollider();
            if (playerCollider.intersects(targetCollider) && !obstacle.isCollected()) {
                Player.addScore(1);
                obstacle.setCollected(true);
            }
        }

        if (!rectangleContainsPolygon(boundingBox, playerCollider)) {
            inGame = false;
        }
    }

    private boolean rectangleContainsPolygon(Rectangle rectangle, Polygon polygon) {
        boolean result = true;
        int[] xpoints = polygon.xpoints;
        int[] ypoints = polygon.ypoints;
        Point[] points = new Point[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            points[i] = new Point(xpoints[i], ypoints[i]);
        }
        for (Point point : points) {
            result = result && rectangle.contains(point);
        }
        return result;
    }

    private void restart() {
        initNewBoard();
        Player.resetScore();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restart();
            }
        }
    }
}
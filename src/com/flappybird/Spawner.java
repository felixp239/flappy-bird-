package com.flappybird;

import java.util.ArrayList;
import java.util.List;

public class Spawner {
    private final static int MIN_DELAY = 600;
    private final static int MAX_DELAY = 600;
    private final static int MIN_DELTA_Y = 200;
    private final static int MAX_DELTA_Y = 500;
    private final static int STARTING_X = 200;
    private final static float MIN_TWO_PROBABILITY = 0.1f;
    private final static int GRACE_PERIOD = 0;              //time with min probability
    private final static int TIME_TO_MAX_PROBABILITY = 0;
    private final static float MAX_TWO_PROBABILITY = 1.0f;
    private final static int MANDATORY_DISTANCE = 200;       //distance between upper and lower pipe
    private final static int DELAY_FIXING_PARAMETER = 0;

    private long gameStart;
    private int bHeight;
    private int bWidth;
    private List<Obstacle> obstacles = new ArrayList<>();

    private int v_x;
    private float downAcceleration;
    private float jumpSpeed;
    private float playerHeight;
    private float probability = MIN_TWO_PROBABILITY;
    private long time;
    private long prev_time;
    private float delay = 0f;                                 //Current x movement left till new pipe spawns

    public Spawner(int bWidth, int bHeight, Player player) {
        this.v_x = (int) player.getV_x();
        this.downAcceleration = player.getDownAcceleration();
        this.playerHeight = player.height;
        this.jumpSpeed = player.getJumpSpeed();
        this.bWidth = bWidth;
        this.bHeight = bHeight;
        Obstacle.setbHeight(bHeight);
        this.gameStart = System.nanoTime() / 1000;
        prev_time = gameStart;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void update(int v_xNew) {
        time = System.nanoTime() / 1000;
        float dx = (time - prev_time) * v_x / 2000000;
        v_x = v_xNew;
        dx += (time - prev_time) * v_x / 2000000;
        delay -= dx;
        for (int i = 0; i < obstacles.size(); i++) {
                Obstacle o = obstacles.get(i);
                if (o.isVisible()) {
                    moveObstacle(o, (int) dx);
                } else {
                    obstacles.remove(i);
                }
            }
        if (delay < 0) {
            delay = (float) (Math.random() * (MAX_DELAY - MIN_DELAY) + MIN_DELAY);
            initNewObstacle();
        }
        prev_time = time;
    }

    private void initNewObstacle() {
        float deltaTime = (delay - DELAY_FIXING_PARAMETER) / v_x;
        int maxDeltaYUp = MAX_DELTA_Y;
        int maxDeltaYDown = MAX_DELTA_Y;
        if (!obstacles.isEmpty()) {
            Obstacle obstacle = obstacles.get(obstacles.size() - 1);
            maxDeltaYUp = (int) Math.min(maxDeltaYUp, obstacle.getLowerPipe().getY() - jumpSpeed * deltaTime);
            if (obstacle.doesUpperExists()) {
                maxDeltaYDown = (int) Math.min(maxDeltaYDown, obstacle.getUpperPipe().getY() + downAcceleration * deltaTime * deltaTime / 2 - jumpSpeed * deltaTime);
            }
        }

        int yUp = 0;
        boolean upperExists = false;
        if (time - gameStart > GRACE_PERIOD * 1000000) {
            if ((time - gameStart - GRACE_PERIOD) *
                    (MAX_TWO_PROBABILITY - MIN_TWO_PROBABILITY) + MIN_TWO_PROBABILITY * 1000000 * TIME_TO_MAX_PROBABILITY > Math.random() * 1000000 * TIME_TO_MAX_PROBABILITY) {
                upperExists = true;
                yUp = (int) (Math.random() * (maxDeltaYUp - MIN_DELTA_Y) + MIN_DELTA_Y);
            }
        }
        int yDown = bHeight -
                (int) (Math.random() * (Math.min(maxDeltaYDown, bHeight - yUp - MANDATORY_DISTANCE) - MIN_DELTA_Y) + MIN_DELTA_Y);
        obstacles.add(new Obstacle(bWidth + STARTING_X, yDown, yUp, upperExists));
        delay = (float) (Math.random() * (MAX_DELAY - MIN_DELAY) + MIN_DELAY);
    }

    private void moveObstacle(Obstacle obstacle, int dx) {
        obstacle.addToX(-dx);
        if (obstacle.getX() + obstacle.getLowerPipeCollider().width / 2 < 0) {
            obstacle.setVisible(false);
        }
    }
}

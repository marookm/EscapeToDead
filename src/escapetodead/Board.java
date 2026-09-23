package escapetodead;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Board extends JPanel
        implements ActionListener, KeyListener {

    public static final int TILE_SIZE = 50;
    public static final int ROWS = 10;
    public static final int COLUMNS = 10;

    private static final int HUD_HEIGHT = 80;
    private static final int DELAY = 80;
    private static final int START_TIME = 45;
    private static final int MAX_TIME = 60;
    private static final int STARTING_LIVES = 3;

    /*
     * 0 = ช่องที่เดินได้
     * 1 = กำแพงหรืออุปสรรค
     */
    private static final int[][][] LEVEL_MAPS = {

        // Level 1: Hospital
        {
            {0,0,0,0,0,1,0,0,0,0},
            {0,1,1,1,0,1,0,1,1,0},
            {0,0,0,1,0,0,0,0,1,0},
            {1,1,0,1,1,1,0,1,1,0},
            {0,0,0,0,0,0,0,1,0,0},
            {0,1,1,1,0,1,0,1,0,1},
            {0,0,0,1,0,1,0,0,0,0},
            {0,1,0,1,0,1,1,1,1,0},
            {0,1,0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,0,1,1,0}
        },

        // Level 2: Laboratory
        {
            {0,0,0,1,0,0,1,1,0,0},
            {0,1,0,1,0,1,0,0,0,1},
            {0,1,0,0,0,1,0,1,0,0},
            {0,1,1,1,0,1,0,1,1,0},
            {0,0,0,0,0,0,0,0,1,0},
            {1,1,0,1,1,1,0,1,1,0},
            {0,0,0,1,0,0,0,1,0,0},
            {0,1,1,1,0,1,1,1,0,1},
            {0,0,0,0,0,0,0,0,0,0},
            {0,1,1,0,1,1,0,1,1,0}
        },

        // Level 3: Escape Zone
        {
            {0,0,1,0,0,0,1,0,0,0},
            {1,0,1,0,1,0,1,0,1,0},
            {0,0,0,0,1,0,0,0,1,0},
            {0,1,1,1,1,0,1,0,1,0},
            {0,0,0,0,0,0,1,0,0,0},
            {0,1,0,1,1,1,1,1,1,0},
            {0,1,0,0,0,0,0,0,0,0},
            {0,1,1,1,0,1,1,1,0,1},
            {0,0,0,1,0,0,0,1,0,0},
            {0,1,0,0,0,1,0,0,0,0}
        }
    };

    private final Timer timer;
    private final Random random;

    private final List<Enemy> enemies;
    private final List<Wall> walls;
    private final List<MissionSwitch> switches;
    private final Set<Point> wallPositions;

    private Player player;
    private Medical medical;
    private Fuel fuel;
    private Exit exit;
    private EscapeCar escapeCar;

    private int level;
    private int score;
    private int lives;
    private int collectedMedicine;
    private int timeLeft;

    private int secondCounter;
    private int enemyMoveCounter;
    private int enemySpawnCounter;

    private boolean running;
    private boolean paused;
    private boolean gameWon;

    public Board() {
        setPreferredSize(new Dimension(
                COLUMNS * TILE_SIZE,
                ROWS * TILE_SIZE + HUD_HEIGHT
        ));

        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(DELAY, this);
        random = new Random();

        enemies = new ArrayList<>();
        walls = new ArrayList<>();
        switches = new ArrayList<>();
        wallPositions = new HashSet<>();

        startNewGame();
    }

    private void startNewGame() {
        timer.stop();

        level = 1;
        score = 0;
        lives = STARTING_LIVES;

        running = true;
        paused = false;
        gameWon = false;

        setupLevel();

        timer.start();
        requestFocusInWindow();
        repaint();
    }

    private void setupLevel() {
        enemies.clear();
        walls.clear();
        switches.clear();
        wallPositions.clear();

        medical = null;
        fuel = null;
        exit = null;
        escapeCar = null;

        collectedMedicine = 0;
        timeLeft = START_TIME;

        secondCounter = 0;
        enemyMoveCounter = 0;
        enemySpawnCounter = 0;

        createWalls();

        Point start = getPlayerStart();
        player = new Player(start.x, start.y);

        if (level == 1) {
            // Hospital mission
            exit = new Exit(9, 9);
            createMedical();

        } else if (level == 2) {
            // Laboratory mission
            exit = new Exit(0, 9);
            createSwitches();

        } else {
            // Escape Zone mission
            escapeCar = new EscapeCar(9, 0);
            createFuel();
        }

        createStartingEnemies();

        showMission();
        requestFocusInWindow();
        repaint();
    }

    private Point getPlayerStart() {
        if (level == 1) {
            return new Point(0, 0);
        }

        if (level == 2) {
            return new Point(9, 0);
        }

        return new Point(0, 9);
    }

    private void showMission() {
        String message;

        if (level == 1) {
            message = "LEVEL 1: HOSPITAL"
                    + "\nCollect 3 medical items."
                    + "\nThen go to the green exit.";

        } else if (level == 2) {
            message = "LEVEL 2: LABORATORY"
                    + "\nActivate both switches."
                    + "\nThen go to the green exit.";

        } else {
            message = "LEVEL 3: ESCAPE ZONE"
                    + "\nFind one fuel can."
                    + "\nThen return to the escape car.";
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "Mission",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void createWalls() {
        int[][] currentMap = LEVEL_MAPS[level - 1];

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (currentMap[row][column] == 1) {
                    Wall wall = new Wall(column, row);

                    walls.add(wall);
                    wallPositions.add(wall.getPosition());
                }
            }
        }
    }

    private void createSwitches() {
        // Both positions must be open spaces in Level 2.
        switches.add(new MissionSwitch(0, 0));
        switches.add(new MissionSwitch(5, 8));
    }

    private void createMedical() {
        Point position = findFreePosition();

        if (position != null) {
            medical = new Medical(position.x, position.y);
        }
    }

    private void createFuel() {
        Point position = findFreePosition();

        if (position != null) {
            fuel = new Fuel(position.x, position.y);
        }
    }

    private Point findFreePosition() {
        List<Point> availablePositions = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Point position = new Point(column, row);

                boolean available =
                        !wallPositions.contains(position)
                        && !player.getPosition().equals(position)
                        && !enemyAt(position)
                        && !goalAt(position)
                        && !switchAt(position);

                if (available) {
                    availablePositions.add(position);
                }
            }
        }

        if (availablePositions.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(
                availablePositions.size()
        );

        return availablePositions.get(randomIndex);
    }

    private void createStartingEnemies() {
        int numberOfEnemies;

        if (level == 1) {
            numberOfEnemies = 1;
        } else {
            numberOfEnemies = 2;
        }

        for (int i = 0; i < numberOfEnemies; i++) {
            createEnemy();
        }
    }

    private void createEnemy() {
        List<Point> availablePositions = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Point position = new Point(column, row);

                int distance =
                        Math.abs(
                                position.x
                                - player.getPosition().x
                        )
                        + Math.abs(
                                position.y
                                - player.getPosition().y
                        );

                boolean available =
                        !wallPositions.contains(position)
                        && !player.getPosition().equals(position)
                        && !enemyAt(position)
                        && !goalAt(position)
                        && !switchAt(position)
                        && distance >= 6;

                if (medical != null) {
                    available = available
                            && !medical.getPosition().equals(
                                    position
                            );
                }

                if (fuel != null) {
                    available = available
                            && !fuel.getPosition().equals(
                                    position
                            );
                }

                if (available) {
                    availablePositions.add(position);
                }
            }
        }

        if (!availablePositions.isEmpty()) {
            int randomIndex = random.nextInt(
                    availablePositions.size()
            );

            Point position =
                    availablePositions.get(randomIndex);

            enemies.add(new Enemy(
                    position.x,
                    position.y
            ));
        }
    }

    private boolean enemyAt(Point position) {
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    private boolean switchAt(Point position) {
        for (MissionSwitch missionSwitch : switches) {
            if (missionSwitch.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    private boolean goalAt(Point position) {
        if (exit != null
                && exit.getPosition().equals(position)) {
            return true;
        }

        return escapeCar != null
                && escapeCar.getPosition().equals(position);
    }

    private void checkMission() {
        if (level == 1) {
            collectMedical();

        } else if (level == 2) {
            activateSwitch();

        } else {
            collectFuel();
        }
    }

    private void collectMedical() {
        if (medical == null) {
            return;
        }

        if (player.getPosition().equals(
                medical.getPosition()
        )) {
            collectedMedicine++;
            score += 100;
            addBonusTime(7);

            if (collectedMedicine >= 3) {
                medical = null;
                exit.setUnlocked(true);
            } else {
                createMedical();
            }
        }
    }

    private void activateSwitch() {
        for (MissionSwitch missionSwitch : switches) {
            boolean playerOnSwitch =
                    player.getPosition().equals(
                            missionSwitch.getPosition()
                    );

            if (playerOnSwitch
                    && !missionSwitch.isActivated()) {

                missionSwitch.activate();
                score += 150;
                addBonusTime(5);
            }
        }

        if (activatedSwitchCount() == switches.size()) {
            exit.setUnlocked(true);
        }
    }

    private int activatedSwitchCount() {
        int count = 0;

        for (MissionSwitch missionSwitch : switches) {
            if (missionSwitch.isActivated()) {
                count++;
            }
        }

        return count;
    }

    private void collectFuel() {
        if (fuel == null) {
            return;
        }

        if (player.getPosition().equals(fuel.getPosition())) {
            fuel = null;
            score += 300;
            addBonusTime(10);

            escapeCar.setReady(true);
        }
    }

    private void addBonusTime(int bonusTime) {
        timeLeft += bonusTime;

        if (timeLeft > MAX_TIME) {
            timeLeft = MAX_TIME;
        }
    }

    private void checkGoal() {
        if (exit != null
                && exit.isUnlocked()
                && player.getPosition().equals(
                        exit.getPosition()
                )) {

            nextLevel();
            return;
        }

        if (escapeCar != null
                && escapeCar.isReady()
                && player.getPosition().equals(
                        escapeCar.getPosition()
                )) {

            winGame();
        }
    }

    private void nextLevel() {
        timer.stop();

        score += 500;
        level++;

        JOptionPane.showMessageDialog(
                this,
                "Mission completed!"
                + "\nCurrent score: " + score,
                "Level Complete",
                JOptionPane.INFORMATION_MESSAGE
        );

        setupLevel();

        timer.start();
        requestFocusInWindow();
    }

    private void winGame() {
        running = false;
        gameWon = true;
        score += 1000;

        timer.stop();
        repaint();

        JOptionPane.showMessageDialog(
                this,
                "You repaired the car and escaped!"
                + "\nFinal score: " + score
                + "\nLives remaining: " + lives
                + "\nPress R to play again.",
                "You Win!",
                JOptionPane.INFORMATION_MESSAGE
        );

        requestFocusInWindow();
    }

    private void checkEnemyCollision() {
        if (!enemyAt(player.getPosition())) {
            return;
        }

        lives--;

        if (lives <= 0) {
            gameOver("The zombies caught you!");
            return;
        }

        timer.stop();

        JOptionPane.showMessageDialog(
                this,
                "A zombie attacked you!"
                + "\nLives remaining: " + lives,
                "Zombie Attack",
                JOptionPane.WARNING_MESSAGE
        );

        resetPlayerAndEnemies();

        timer.start();
        requestFocusInWindow();
    }

    private void resetPlayerAndEnemies() {
        Point start = getPlayerStart();

        player = new Player(start.x, start.y);
        enemies.clear();

        createStartingEnemies();

        enemyMoveCounter = 0;
        enemySpawnCounter = 0;
    }

    private void gameOver(String reason) {
        if (!running) {
            return;
        }

        running = false;
        gameWon = false;

        timer.stop();
        repaint();

        JOptionPane.showMessageDialog(
                this,
                reason
                + "\nLevel reached: " + level
                + "\nScore: " + score
                + "\nPress R to restart.",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );

        requestFocusInWindow();
    }

    /*
     * ค่ายิ่งสูง ซอมบี้ยิ่งเดินช้า
     *
     * Level 1: ทุก 0.8 วินาที
     * Level 2: ทุก 0.65 วินาที
     * Level 3: ทุก 0.5 วินาที
     */
    private int getEnemyMoveDelay() {
        if (level == 1) {
            return 800;
        }

        if (level == 2) {
            return 650;
        }

        return 500;
    }

    /*
     * Level 1: เกิดเพิ่มทุก 25 วินาที
     * Level 2: เกิดเพิ่มทุก 20 วินาที
     * Level 3: เกิดเพิ่มทุก 15 วินาที
     */
    private int getEnemySpawnDelay() {
        if (level == 1) {
            return 25000;
        }

        if (level == 2) {
            return 20000;
        }

        return 15000;
    }

    private int getMaximumEnemies() {
        if (level == 1) {
            return 2;
        }

        if (level == 2) {
            return 3;
        }

        return 4;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!running || paused) {
            return;
        }

        secondCounter += DELAY;
        enemyMoveCounter += DELAY;
        enemySpawnCounter += DELAY;

        updateTime();
        updateEnemies();

        checkMission();
        checkEnemyCollision();

        if (!running) {
            repaint();
            return;
        }

        checkGoal();

        if (timeLeft <= 0) {
            gameOver("Time is over!");
        }

        repaint();
    }

    private void updateTime() {
        if (secondCounter >= 1000) {
            secondCounter -= 1000;
            timeLeft--;
        }
    }

    private void updateEnemies() {
        if (enemyMoveCounter >= getEnemyMoveDelay()) {
            enemyMoveCounter = 0;

            List<Enemy> currentEnemies =
                    new ArrayList<>(enemies);

            for (Enemy enemy : currentEnemies) {
                enemy.move(
                        wallPositions,
                        player.getPosition()
                );
            }
        }

        if (enemySpawnCounter >= getEnemySpawnDelay()) {
            enemySpawnCounter = 0;

            if (enemies.size() < getMaximumEnemies()) {
                createEnemy();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        drawFloor(g);

        for (Wall wall : walls) {
            wall.draw(g);
        }

        if (exit != null) {
            exit.draw(g);
        }

        if (escapeCar != null) {
            escapeCar.draw(g);
        }

        if (medical != null) {
            medical.draw(g);
        }

        if (fuel != null) {
            fuel.draw(g);
        }

        for (MissionSwitch missionSwitch : switches) {
            missionSwitch.draw(g);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        if (player != null) {
            player.draw(g);
        }

        drawStatusBar(g);

        if (paused || !running) {
            drawOverlay(g);
        }

        g.dispose();
    }

    private void drawFloor(Graphics2D g) {
        Color firstColor;
        Color secondColor;

        if (level == 1) {
            firstColor = new Color(225, 235, 240);
            secondColor = new Color(200, 215, 225);

        } else if (level == 2) {
            firstColor = new Color(210, 230, 205);
            secondColor = new Color(180, 205, 175);

        } else {
            firstColor = new Color(205, 195, 185);
            secondColor = new Color(175, 165, 155);
        }

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if ((row + column) % 2 == 0) {
                    g.setColor(firstColor);
                } else {
                    g.setColor(secondColor);
                }

                g.fillRect(
                        column * TILE_SIZE,
                        row * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        }
    }

    private String getMissionText() {
        if (level == 1) {
            if (exit.isUnlocked()) {
                return "Medicine complete! Go to the exit.";
            }

            return "Medicine: "
                    + collectedMedicine
                    + "/3";
        }

        if (level == 2) {
            if (exit.isUnlocked()) {
                return "Switches complete! Go to the exit.";
            }

            return "Switches: "
                    + activatedSwitchCount()
                    + "/2";
        }

        if (escapeCar.isReady()) {
            return "Fuel collected! Return to the car.";
        }

        return "Find the fuel can.";
    }

    private void drawStatusBar(Graphics2D g) {
        int hudY = ROWS * TILE_SIZE;

        g.setColor(new Color(28, 32, 36));
        g.fillRect(
                0,
                hudY,
                getWidth(),
                HUD_HEIGHT
        );

        g.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                15
        ));

        g.setColor(Color.YELLOW);
        g.drawString(
                "LEVEL " + level,
                12,
                hudY + 24
        );

        g.setColor(new Color(70, 225, 155));
        g.drawString(
                "SCORE " + score,
                100,
                hudY + 24
        );

        if (timeLeft <= 10) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.WHITE);
        }

        g.drawString(
                "TIME " + timeLeft + "s",
                205,
                hudY + 24
        );

        g.setColor(new Color(255, 90, 100));
        g.drawString(
                "LIVES " + lives,
                315,
                hudY + 24
        );

        g.setColor(new Color(120, 195, 255));
        g.setFont(new Font(
                "SansSerif",
                Font.PLAIN,
                13
        ));

        g.drawString(
                getMissionText(),
                12,
                hudY + 50
        );

        g.setColor(Color.LIGHT_GRAY);
        g.drawString(
                "Arrow Keys: Move | P: Pause | R: Restart",
                12,
                hudY + 70
        );
    }

    private void drawOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));

        g.fillRect(
                0,
                0,
                COLUMNS * TILE_SIZE,
                ROWS * TILE_SIZE
        );

        String message;

        if (paused) {
            message = "PAUSED";
        } else if (gameWon) {
            message = "YOU WIN!";
        } else {
            message = "GAME OVER";
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                40
        ));

        int textWidth =
                g.getFontMetrics().stringWidth(message);

        g.drawString(
                message,
                (getWidth() - textWidth) / 2,
                250
        );
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.VK_R) {
            startNewGame();
            return;
        }

        if (keyCode == KeyEvent.VK_P && running) {
            paused = !paused;
            repaint();
            return;
        }

        if (!running || paused) {
            return;
        }

        player.move(keyCode, wallPositions);

        checkMission();
        checkEnemyCollision();

        if (!running) {
            repaint();
            return;
        }

        checkGoal();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }
}
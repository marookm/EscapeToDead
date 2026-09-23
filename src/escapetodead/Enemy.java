package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Enemy extends Entity {

    private final Random random = new Random();

    public Enemy(int column, int row) {
        super(column, row);
    }

    public void move(Set<Point> wallPositions, Point playerPosition) {
        List<Point> movements = new ArrayList<>();

        movements.add(new Point(0, 0));
        movements.add(new Point(0, -1));
        movements.add(new Point(0, 1));
        movements.add(new Point(-1, 0));
        movements.add(new Point(1, 0));

        if (random.nextInt(100) < 55) {
            movements.sort((first, second) -> {
                int firstDistance = calculateDistance(
                        position.x + first.x,
                        position.y + first.y,
                        playerPosition
                );

                int secondDistance = calculateDistance(
                        position.x + second.x,
                        position.y + second.y,
                        playerPosition
                );

                return Integer.compare(firstDistance, secondDistance);
            });
        } else {
            Collections.shuffle(movements);
        }

        for (Point movement : movements) {
            Point nextPosition = new Point(
                    position.x + movement.x,
                    position.y + movement.y
            );

            boolean insideMap =
                    nextPosition.x >= 0
                    && nextPosition.x < Board.COLUMNS
                    && nextPosition.y >= 0
                    && nextPosition.y < Board.ROWS;

            if (insideMap && !wallPositions.contains(nextPosition)) {
                position = nextPosition;
                return;
            }
        }
    }

    private int calculateDistance(
            int column,
            int row,
            Point playerPosition
    ) {
        return Math.abs(column - playerPosition.x)
                + Math.abs(row - playerPosition.y);
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        g.setColor(new Color(130, 205, 85));
        g.fillOval(x + 9, y + 6, 32, 34);

        g.setColor(new Color(90, 140, 60));
        g.fillRect(x + 11, y + 32, 28, 14);

        g.setColor(Color.RED);
        g.fillOval(x + 17, y + 17, 6, 6);
        g.fillOval(x + 29, y + 17, 6, 6);

        g.setColor(Color.BLACK);
        g.drawLine(x + 17, y + 30, x + 34, y + 27);
    }
}
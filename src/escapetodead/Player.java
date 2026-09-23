package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Set;

public class Player extends Entity {

    public Player(int column, int row) {
        super(column, row);
    }

    public void move(int keyCode, Set<Point> wallPositions) {
        int nextColumn = position.x;
        int nextRow = position.y;

        if (keyCode == KeyEvent.VK_UP) {
            nextRow--;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            nextRow++;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            nextColumn--;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            nextColumn++;
        } else {
            return;
        }

        Point nextPosition = new Point(nextColumn, nextRow);

        boolean insideMap =
                nextColumn >= 0
                && nextColumn < Board.COLUMNS
                && nextRow >= 0
                && nextRow < Board.ROWS;

        if (insideMap && !wallPositions.contains(nextPosition)) {
            position = nextPosition;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        g.setColor(new Color(255, 205, 150));
        g.fillOval(x + 11, y + 6, 28, 28);

        g.setColor(new Color(65, 38, 20));
        g.fillArc(x + 11, y + 4, 28, 20, 0, 180);

        g.setColor(new Color(42, 98, 180));
        g.fillRoundRect(x + 9, y + 28, 32, 18, 8, 8);

        g.setColor(Color.BLACK);
        g.fillOval(x + 19, y + 17, 4, 5);
        g.fillOval(x + 29, y + 17, 4, 5);
        g.drawArc(x + 20, y + 21, 12, 8, 190, 160);
    }
}
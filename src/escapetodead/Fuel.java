package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;

public class Fuel extends Entity {

    public Fuel(int column, int row) {
        super(column, row);
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        g.setColor(new Color(190, 45, 40));
        g.fillRoundRect(x + 13, y + 10, 25, 34, 5, 5);

        g.setColor(new Color(120, 25, 20));
        g.fillRect(x + 18, y + 6, 15, 7);

        g.setColor(Color.WHITE);
        g.fillOval(x + 20, y + 20, 11, 14);

        g.setColor(new Color(245, 180, 40));
        g.fillOval(x + 23, y + 25, 5, 7);
    }
}
package escapetodead;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Medical extends Entity {

    public Medical(int column, int row) {
        super(column, row);
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        g.setStroke(new BasicStroke(3));

        g.setColor(Color.WHITE);
        g.fillRoundRect(x + 13, y + 17, 27, 13, 5, 5);

        g.setColor(Color.RED);
        g.drawRoundRect(x + 13, y + 17, 27, 13, 5, 5);
        g.drawLine(x + 19, y + 23, x + 34, y + 23);
        g.drawLine(x + 36, y + 13, x + 36, y + 35);
        g.drawLine(x + 39, y + 12, x + 39, y + 36);
        g.drawLine(x + 8, y + 23, x + 13, y + 23);

        g.setStroke(new BasicStroke(1));
    }
}
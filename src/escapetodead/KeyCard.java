package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;

public class KeyCard extends Entity {

    public KeyCard(int column, int row) {
        super(column, row);
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        g.setColor(new Color(40, 160, 230));
        g.fillRoundRect(x + 9, y + 13, 32, 23, 5, 5);

        g.setColor(Color.WHITE);
        g.fillOval(x + 14, y + 18, 8, 8);

        g.setColor(new Color(20, 70, 120));
        g.fillRect(x + 26, y + 19, 10, 3);
        g.fillRect(x + 26, y + 25, 8, 3);

        g.setColor(Color.YELLOW);
        g.drawString("K", x + 35, y + 34);
    }
}
package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;

public class Wall extends Entity {

    public Wall(int column, int row) {
        super(column, row);
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        g.setColor(new Color(87, 91, 37));
        g.fillRect(
                x,
                y,
                Board.TILE_SIZE,
                Board.TILE_SIZE
        );

        g.setColor(new Color(151, 145, 54));

        for (int row = 0; row < 4; row++) {
            int wallY = y + row * 13;
            int offset = row % 2 == 0 ? 0 : 8;

            for (int wallX = x - offset;
                 wallX < x + Board.TILE_SIZE;
                 wallX += 18) {

                g.drawRect(wallX, wallY, 18, 12);
            }
        }
    }
}
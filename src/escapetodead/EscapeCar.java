package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;

public class EscapeCar extends Entity {

    private boolean ready;

    public EscapeCar(int column, int row) {
        super(column, row);
        ready = false;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        if (ready) {
            g.setColor(new Color(40, 190, 90));
        } else {
            g.setColor(new Color(90, 95, 100));
        }

        g.fillRoundRect(x + 5, y + 17, 40, 22, 8, 8);

        g.setColor(new Color(150, 220, 250));
        g.fillRect(x + 14, y + 12, 21, 12);

        g.setColor(Color.BLACK);
        g.fillOval(x + 10, y + 35, 10, 10);
        g.fillOval(x + 31, y + 35, 10, 10);

        if (!ready) {
            g.setColor(Color.RED);
            g.drawLine(x + 8, y + 10, x + 42, y + 42);
            g.drawLine(x + 42, y + 10, x + 8, y + 42);
        }
    }
}
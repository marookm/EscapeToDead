package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;

public class Exit extends Entity {

    private boolean unlocked;

    public Exit(int column, int row) {
        super(column, row);
        unlocked = false;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        // Door frame
        g.setColor(new Color(65, 45, 30));
        g.fillRoundRect(x + 8, y + 4, 34, 44, 7, 7);

        if (unlocked) {
            // Green unlocked door
            g.setColor(new Color(50, 190, 100));
        } else {
            // Red locked door
            g.setColor(new Color(180, 65, 65));
        }

        g.fillRoundRect(x + 12, y + 8, 26, 40, 5, 5);

        // Door handle
        g.setColor(Color.YELLOW);
        g.fillOval(x + 31, y + 27, 5, 5);

        if (!unlocked) {
            // Lock
            g.setColor(new Color(255, 210, 55));
            g.fillRoundRect(x + 18, y + 20, 15, 13, 3, 3);
            g.drawArc(x + 20, y + 13, 11, 14, 0, 180);
        }
    }
}
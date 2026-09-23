package escapetodead;

import java.awt.Color;
import java.awt.Graphics2D;

public class MissionSwitch extends Entity {

    private boolean activated;

    public MissionSwitch(int column, int row) {
        super(column, row);
        activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate() {
        activated = true;
    }

    @Override
    public void draw(Graphics2D g) {
        int x = position.x * Board.TILE_SIZE;
        int y = position.y * Board.TILE_SIZE;

        // Switch box
        g.setColor(new Color(65, 70, 75));
        g.fillRoundRect(x + 10, y + 8, 30, 36, 6, 6);

        if (activated) {
            g.setColor(new Color(50, 220, 100));
        } else {
            g.setColor(new Color(230, 65, 60));
        }

        g.fillOval(x + 17, y + 14, 16, 16);

        g.setColor(Color.WHITE);

        if (activated) {
            g.drawString("ON", x + 16, y + 41);
        } else {
            g.drawString("OFF", x + 14, y + 41);
        }
    }
}
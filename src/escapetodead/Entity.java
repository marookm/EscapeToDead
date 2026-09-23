package escapetodead;

import java.awt.Graphics2D;
import java.awt.Point;

public abstract class Entity {

    protected Point position;

    public Entity(int column, int row) {
        position = new Point(column, row);
    }

    public Point getPosition() {
        return new Point(position);
    }

    public abstract void draw(Graphics2D g);
}
package escapetodead;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Escape to Dead");

            Board board = new Board();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(board);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            board.requestFocusInWindow();
        });
    }
}
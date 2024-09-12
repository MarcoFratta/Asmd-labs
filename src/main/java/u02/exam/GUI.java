package u02.exam;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    
    private static final long serialVersionUID = -6218820567019985015L;
    private final Map<JButton, Position> cells = new HashMap<>();
    private final Logic logic;
    private final Logger logger;
    private final boolean exit;

    public GUI(final int width, final int height, final Logic logic, final Logger logger) {
        this(width, height, logic, logger, true);
    }

    public GUI(final int width, final int height, final Logic logic,
               final Logger logger, final boolean exit) {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(70*width, 70*height);
        this.logic = logic;
        this.logger = logger;
        this.exit = exit;
        this.logger.log("Starting gui");
        
        final JPanel panel = new JPanel(new GridLayout(height, width));
        this.getContentPane().add(panel);

        final ActionListener al = e -> {
            final var jb = (JButton)e.getSource();
            this.logic.hit(this.cells.get(jb));
            if (this.logic.isOver()){
                this.logger.log("Game over!");
                if (this.exit) System.exit(0);
            }
            this.redraw();
        };

        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
            	final JButton jb = new JButton();
                this.cells.put(jb, new Position(j,i));
                jb.addActionListener(al);
                panel.add(jb);
            }
        }
        this.redraw();
        this.setVisible(true);
    }

    private void redraw() {
        for (final var entry: this.cells.entrySet()){
            entry.getKey().setText(
                this.logic.isMarked(entry.getValue()) 
                    ? "*" 
                    : this.logic.getGoal().equals(entry.getValue()) ? "o" : " ");
        }
    }
    
}

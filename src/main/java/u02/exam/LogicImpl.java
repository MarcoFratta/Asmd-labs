package u02.exam;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LogicImpl implements Logic {

    private final int height;
    private final int width;
    private final Set<Position> marked = new HashSet<>();
    private final Random random = new Random();
    private final Position goal;
    private final Logger logger;

    public LogicImpl(int width, int height, Logger logger) {
        this.width = width;
        this.height = height;
        this.resetMarks();
        this.goal = new Position(random.nextInt(this.width), random.nextInt(this.height));
        this.logger = logger;
    }

    private void resetMarks() {
        this.marked.clear();
    }

    @Override
    public void hit(Position position) {
        this.marked.add(position);
        this.logger.log("Hit at " + position);
        for (int x = 1; x < this.width; x++){
            Set<Position> column = new HashSet<>();
            for (int y = 0; y < this.height; y++){
                if (Math.abs(position.y() - y) <= x && position.x() + x < this.width){
                    Position p = new Position(position.x() + x, y);
                    this.logger.log("Marking " +p);
                    column.add(p);
                }
            }
            this.marked.addAll(column);
            if (column.stream().anyMatch(p -> p.y() == 0 || p.y() == this.height-1)){
                return;
            }
        }
    }

    @Override
    public boolean isMarked(Position position) {
        return this.marked.contains(position);
    }

    @Override
    public boolean isOver() {
        return this.marked.contains(this.goal);
    }

    @Override
    public Position getGoal() {
        return this.goal;
    }
}

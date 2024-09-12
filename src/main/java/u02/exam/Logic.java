package u02.exam;

public interface Logic {

    void hit(Position position);

    boolean isMarked(Position position);

    Position getGoal();

    boolean isOver();
}

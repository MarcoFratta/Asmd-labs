package u01.exam;

public interface Logic {
    int width();

    int height();

    void hit(Position position);

    boolean isMarked(Position position);

    Position getGoal();

    boolean isOver();
}

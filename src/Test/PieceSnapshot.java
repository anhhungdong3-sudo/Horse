package Test;

public class PieceSnapshot {
    public int id;
    public int boardPos;
    public int stepsMoved;
    public int goalPos;

    public PieceSnapshot(int id, int boardPos, int stepsMoved, int goalPos) {
        this.id = id;
        this.boardPos = boardPos;
        this.stepsMoved = stepsMoved;
        this.goalPos = goalPos;
    }
}

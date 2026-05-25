package horse;

public class SkipTurn implements MoveStrategy {
    @Override
    public boolean move(int number, Piece piece, Board board) {
        System.out.println("Turn skipped.");
        return true;
    }
}


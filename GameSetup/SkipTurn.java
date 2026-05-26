package GameSetup;

public class SkipTurn implements MoveStrategy {
	// 3.3. không làm j hết chỉ bỏ lượt
	@Override
	public boolean move(int number, Piece piece, Board board) {
		System.out.println("Turn skipped.");
		return true;
	}
}

package GameSetup;

// 2. người chơi tương ứng sẽ gọi đến lớp này
public interface MoveStrategy {
	
	// 3.gọi thực hiện di chuyển bằng phương thức này
	public boolean move(int dice, Piece piece, Board board);
}

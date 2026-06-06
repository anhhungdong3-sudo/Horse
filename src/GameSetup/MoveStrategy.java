package GameSetup;

// 2. người chơi tương ứng sẽ gọi đến lớp này
public interface MoveStrategy {
	
	// 3.gọi thực hiện di chuyển bằng phương thức này
	public boolean move(int dice, Piece piece, Board board);
}
// tôi ở đây để farm commit vì chưa biết sẽ commit gì nên tôi tạm comment lên đây ngày mai tôi xóa sau thông cảm cho tôi ký tên Trung

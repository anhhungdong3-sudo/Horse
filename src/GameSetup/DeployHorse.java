package GameSetup;

public class DeployHorse implements MoveStrategy {
	@Override
	public boolean move(int number, Piece piece, Board board) {
		//3.3.1 lấy ô bắt đầu của người chơi đó và thay đổi thông tin quân được chợn
		int spawn = board.getStartPosition(piece.getColor());
		Piece pieceAtSpawn = board.getGridNormal().get(spawn).getPiece();
		if (pieceAtSpawn == null) {
			piece.setBoardPosition(spawn);
			//3.3.2 sau khi thay đổi thông tin add vào bàn cờ
			board.addPiece(piece);
			System.out.println("Piece " + piece.getId() + " deployed at position: " + piece.getBoardPosition());
			return true;
		} else {
			//3.3.1 nếu có quân khác màu đầu tiên loại bỏ quân đó, cập nhập thông tin quân vừa bị đá về trạng thái ở chuồng
			if (!pieceAtSpawn.getColor().equals(piece.getColor())) {
				System.out.println("Piece " + piece.getId() + " deployed. And piece " + pieceAtSpawn.getColor() + " "
						+ pieceAtSpawn.getId() + " has been kicked");
				board.removePiece(pieceAtSpawn);
				pieceAtSpawn.setBoardPosition(-1);
				pieceAtSpawn.setStepsMoved(0);

				//3.3.2 sau khi thay đổi thông tin add vào bàn cờ
				piece.setBoardPosition(spawn);
				board.addPiece(piece);
				return true;
			} else {
				System.out.println("Piece " + piece.getId() + " can't be deployed.");
				return false;
			}
		}
	}
}

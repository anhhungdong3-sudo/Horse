package horse;

public class DeployHorse implements MoveStrategy {
	@Override
	public boolean move(int number, Piece piece, Board board) {
		if (number != 1 && number != 6) {
			System.out.println("It's not a valid number to deploy");
			return false;
		}

		if (piece.getBoardPosition() != -1) {
			System.out.println("It's already deployed");
			return false;
		}

		int spawn = board.getStartPosition(piece.getColor());
		Piece pieceAtSpawn = board.getGridNormal().get(spawn).getPiece();
		if (pieceAtSpawn == null) {
			piece.setBoardPosition(spawn);
			board.addPiece(piece);
			System.out.println("Piece " + piece.getId() + " deployed tại ô: " + piece.getBoardPosition());
			return true;
		} else {
			if (!pieceAtSpawn.getColor().equals(piece.getColor())) {
				System.out.println("Piece " + piece.getId() + " deployed. And piece " + pieceAtSpawn.getColor() + " "
						+ pieceAtSpawn.getId() + "has been kick");
				board.removePiece(pieceAtSpawn);
				pieceAtSpawn.setBoardPosition(-1);
				pieceAtSpawn.setStepsMoved(0);

				piece.setBoardPosition(spawn);
				board.addPiece(piece);
				return true;
			} else {
				System.out.println("Piece " + piece.getId() + " can't deployed.");
				return false;
			}
		}
	}
}

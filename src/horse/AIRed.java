package horse;

import java.awt.Color;
import java.util.List;

public class AIRed extends AI {
	public AIRed(String name) {
		super(name);
		int x = 8, y = 1;
		for (int i = 0; i < 6; i++) {
			this.gridGoal.add(new GoalCell(i, new Coordinate(x, y)));
			y++;
		}
		for (int i = 0; i < 4; i++) {
			Piece p = new Piece(i, "red", this);
			addPiece(p);
		}
		barnCod = new Coordinate(0, 0);
	}

	@Override
	public Piece decideMove(int dice, Board board, List<Piece> movable) {
		// Ưu tiên 1: Có quân địch trong phạm vi 12 ô
		Piece attacker = getPieceWithEnemyNearby(movable, board, 12);
		if (attacker != null) {
			return attacker;
		}
		
		// Ưu tiên 2: Quân đi xa nhất
		Piece furthest = getFurthestPiece(movable);
		if (furthest != null) {
			return furthest;
		}

		// Ưu tiên 3: Xuất quân
		Piece deployable = getDeployablePiece(dice);
		if (deployable != null && movable.contains(deployable)) {
			return deployable;
		}

		// Nếu không có quân nào đi được
		return null;
	}
	
//	@Override
//	public Piece decideMove(int dice, Board board) {
//		List<Piece> movable = getMovablePieces(dice);
//		// Ưu tiên 1: Có quân địch trong phạm vi 12 ô
//		Piece attacker = getPieceWithEnemyNearby(movable, board, 12);
//		if (attacker != null) {
//			return attacker;
//		}
//		
//		// Ưu tiên 2: Quân đi xa nhất
//		Piece furthest = getFurthestPiece(movable);
//		if (furthest != null) {
//			return furthest;
//		}
//
//		// Ưu tiên 3: Xuất quân
//		Piece deployable = getDeployablePiece(dice);
//		if (deployable != null && movable.contains(deployable)) {
//			return deployable;
//		}
//
//		// Nếu không có quân nào đi được
//		return null;
//	}

	@Override
	public MoveStrategy decideStrategy(Piece piece) {
		if (piece == null)
			return new SkipTurn();
		if (piece.getBoardPosition() == -1)
			return new DeployHorse();
		return new NormalMove();
	}

	@Override
	public Color getColor() {
		return Color.red;
	}

	@Override
	public String getColorStr() {
		return "red";
	}
}

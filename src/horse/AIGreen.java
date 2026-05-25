package horse;

import java.awt.Color;
import java.util.List;

public class AIGreen extends AI {
	public AIGreen(String name) {
		super(name);
		int x = 15, y = 8;
		for (int i = 0; i < 6; i++) {
			this.gridGoal.add(new GoalCell(i, new Coordinate(x, y)));
			x--;
		}
		for (int i = 0; i < 4; i++) {
			Piece p = new Piece(i, "green", this);
			addPiece(p);
		}
		barnCod = new Coordinate(11, 0);
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
		return Color.green;
	}

	@Override
	public String getColorStr() {
		return "green";
	}

}
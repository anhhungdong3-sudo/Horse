package Player;

import java.awt.Color;
import java.util.List;

import GameSetup.Board;
import GameSetup.DeployHorse;
import GameSetup.GoalCell;
import GameSetup.MoveStrategy;
import GameSetup.NormalMove;
import GameSetup.Piece;
import GameSetup.SkipTurn;
import MVC.Coordinate;

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
		int far = 12;
		int shot = 6;
		Piece furthest = getFurthestPiece(movable);
		Piece kick = canKickOrher(movable, board, dice);
		Piece attacker = getPieceWithEnemyNearby(movable, board, far);
		Piece protecter = getPieceWithEnemyNearby(movable, board, -far);
		Piece deployable = getDeployablePiece(movable, dice);
		boolean inDangerTrap = isInDangerAfterKick(movable, board, dice, 6);
		boolean lazy = lazy();
		int pieceOnBoard = getActivePieceCount();

		// Ưu tiên 1: an toàn cho quân gần về đích
		if (furthest != null && furthest.getStepsMoved() > 48 && furthest.getStepsMoved() < 56) {
			if (protecter != null && protecter != furthest) {
				return protecter;
			}
		}
		// Ưu tiên 2: Quân đi xa nhất
		if (furthest != null) {
			return furthest;
		}

		// Ưu tiên 3: Xuất quân
		if (deployable != null) {
			return deployable;
		}

		// Ưu tiên 4: đá quân người chơi khác
		if (kick != null) {
			return kick;
		}

		// Ưu tiên 5: Có quân địch trước mặt
		if (attacker != null) {
			return attacker;
		}

		// fallback
		if (!movable.isEmpty()) {
			return movable.get(0);
		}

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
		return Color.red;
	}

	@Override
	public String getColorStr() {
		return "red";
	}
}

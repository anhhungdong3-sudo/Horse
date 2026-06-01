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

		// Ưu tiên 1: Nếu có thể đá mà không bị trả đũa ngay sau đó
		if (kick != null && !inDangerTrap) {
			return kick;
		}

		// Ưu tiên 2: an toàn cho quân gần về đích
		if (furthest != null && furthest.getStepsMoved() > 48 && furthest.getStepsMoved() < 56) {
			if (protecter != null) {
				return protecter;
			}
		}

		// Ưu tiên 3: Xuất quân nếu ít quân trên bàn
		if (deployable != null && pieceOnBoard < 2) {
			return deployable;
		}

		// Ưu tiên 4: Đánh quân địch trước mặt
		if (attacker != null) {
			return attacker;
		}

		// Ưu tiên 5: Quân đi xa nhất
		if (furthest != null) {
			return furthest;
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
		return Color.green;
	}

	@Override
	public String getColorStr() {
		return "green";
	}
}
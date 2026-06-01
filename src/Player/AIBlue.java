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

public class AIBlue extends AI {
	public AIBlue(String name) {
		super(name);
		int x = 1, y = 8;
		for (int i = 0; i < 6; i++) {
			this.gridGoal.add(new GoalCell(i, new Coordinate(x, y)));
			x++;
		}
		for (int i = 0; i < 4; i++) {
			Piece p = new Piece(i, "blue", this);
			addPiece(p);
		}
		barnCod = new Coordinate(0, 11);
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
			if (protecter != null) {
				return protecter;
			}
		}

		// Ưu tiên 3: Quân đi xa nhất
		if (furthest != null) {
			return furthest;
		}

		// Ưu tiên 4: có lười hay không
		if (lazy == true) {
			System.out.println("nah don't want to move bro");
			return null;
		}

		// Ưu tiên 5: Xuất quân
		if (deployable != null) {
			return deployable;
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
		return Color.BLUE;
	}

	@Override
	public String getColorStr() {
		return "blue";
	}
}

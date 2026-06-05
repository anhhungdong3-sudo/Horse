package P_AI;

import java.awt.Color;
import java.awt.Image;
import java.util.List;

import GameSetup.Board;
import GameSetup.DeployHorse;
import GameSetup.MoveStrategy;
import GameSetup.NormalMove;
import GameSetup.Piece;
import GameSetup.SkipTurn;

public class AINormal extends AI {
	public AINormal(String name, String colorStr, Image pieceImage) {
		super(name, colorStr, pieceImage);
		this.setupColorAndGoal(colorStr);
		int i = 0;
		while (i < 4) {
			this.addPiece(new Piece(i, colorStr, this));
			++i;
		}
	}

	@Override
	public Piece decideMove(int dice, Board board, List<Piece> movable) {
		int far = 12;
		Piece furthest = this.getFurthestPiece(movable);
		Piece kick = this.canKickOther(movable, board, dice);
		Piece attacker = this.getPieceWithEnemyNearby(movable, board, far);
		Piece protecter = this.getPieceWithEnemyNearby(movable, board, -far);
		Piece deployable = this.getDeployablePiece(movable, dice);
		boolean lazy = this.lazy();
		if (lazy) {
			return null;
		}
		if (furthest != null && furthest.getStepsMoved() > 48 && furthest.getStepsMoved() < 56 && protecter != null
				&& protecter != furthest) {
			return protecter;
		}
		if (furthest != null) {
			return furthest;
		}
		if (deployable != null) {
			return deployable;
		}
		if (kick != null) {
			return kick;
		}
		if (attacker != null) {
			return attacker;
		}
		if (!movable.isEmpty()) {
			return movable.get(0);
		}
		return null;
	}

	@Override
	public MoveStrategy decideStrategy(Piece piece) {
		if (piece == null) {
			return new SkipTurn();
		}
		if (piece.getBoardPosition() == -1) {
			return new DeployHorse();
		}
		return new NormalMove();
	}

	@Override
	public Color getColor() {
		return getColorFromString();
	}

	@Override
	public String getColorStr() {
		return this.colorStr;
	}
}

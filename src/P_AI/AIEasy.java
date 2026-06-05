package P_AI;

import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.Random;

import GameSetup.Board;
import GameSetup.DeployHorse;
import GameSetup.MoveStrategy;
import GameSetup.NormalMove;
import GameSetup.Piece;
import GameSetup.SkipTurn;

public class AIEasy extends AI {
	private Random random = new Random();

	public AIEasy(String name, String colorStr, Image pieceImage) {
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
		if (movable.isEmpty()) {
			return null;
		}
		int chose = this.random.nextInt(movable.size());
		return movable.get(chose);
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

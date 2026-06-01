package Player;

import GameSetup.Board;
import GameSetup.Piece;

public abstract class Human extends Player {
	public Human(String name) {
		super(name);
	}

	@Override
	public boolean move(int number, Piece piece, Board board) {
		return move.move(number, piece, board);
	}

	@Override
	public boolean isAI() {
		return false;
	}
}

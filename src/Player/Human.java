package Player;

import java.awt.Image;

import GameSetup.Board;
import GameSetup.Piece;

public abstract class Human extends Player {
	public Human(String name, Image pieceImage) {
		super(name, pieceImage);
        this.pieceImage = pieceImage;
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

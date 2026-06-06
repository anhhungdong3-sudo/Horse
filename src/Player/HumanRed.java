package Player;

import java.awt.Color;
import java.awt.Image;

import GameSetup.GoalCell;
import GameSetup.Piece;
import MVC.Coordinate;

public class HumanRed extends Human {
	public HumanRed(String name, Image pieceImage) {
		super(name, pieceImage);

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
	public Color getColor() {
		return Color.red;
	}

	@Override
	public String getColorStr() {
		return "red";
	}
}

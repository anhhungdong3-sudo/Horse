package Player;

import java.awt.Color;
import java.awt.Image;

import GameSetup.GoalCell;
import GameSetup.Piece;
import MVC.Coordinate;

public class HumanGreen extends Human {
	public HumanGreen(String name, Image pieceImage) {
		super(name, pieceImage);

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
	public Color getColor() {
		return Color.green;
	}

	@Override
	public String getColorStr() {
		return "green";
	}
}

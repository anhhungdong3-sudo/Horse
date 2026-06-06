package Player;

import java.awt.Color;
import java.awt.Image;

import GameSetup.GoalCell;
import GameSetup.Piece;
import MVC.Coordinate;

public class HumanBlue extends Human {
	public HumanBlue(String name, Image pieceImage) {
		super(name, pieceImage);

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
	public Color getColor() {
		return Color.blue;
	}

	@Override
	public String getColorStr() {
		return "blue";
	}
}

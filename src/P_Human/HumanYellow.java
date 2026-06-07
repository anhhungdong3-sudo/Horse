package P_Human;

import java.awt.Color;
import java.awt.Image;

import GameSetup.GoalCell;
import GameSetup.Piece;
import MVC.Coordinate;

public class HumanYellow extends Human {
	public HumanYellow(String name, Image pieceImage) {
		super(name, pieceImage);

		int x = 8, y = 15;
		for (int i = 0; i < 6; i++) {
			this.gridGoal.add(new GoalCell(i, new Coordinate(x, y)));
			y--;
		}
		for (int i = 0; i < 4; i++) {
			Piece p = new Piece(i, "yellow", this);
			addPiece(p);
		}
		barnCod = new Coordinate(11, 11);
	}

	@Override
	public Color getColor() {
		return Color.yellow;
	}

	@Override
	public String getColorStr() {
		return "yellow";
	}
}

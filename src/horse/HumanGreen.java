package horse;

import java.awt.Color;

public class HumanGreen extends Human {
	public HumanGreen(String name) {
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
	public Color getColor() {
		return Color.green;
	}

	@Override
	public String getColorStr() {
		return "green";
	}
}

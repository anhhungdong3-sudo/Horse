package GameSetup;

import MVC.Coordinate;

public class NormalCell extends BoardCell {


	public NormalCell(int position, Coordinate cod) {
		super(position, cod);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getIndex() {
		return this.position;
	}
}
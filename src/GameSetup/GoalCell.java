package GameSetup;

import MVC.Coordinate;

public class GoalCell extends BoardCell {


    public GoalCell(int position, Coordinate cod) {
		super(position, cod);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getIndex() {
        return this.position; // Hoặc xử lý đặc biệt nếu cần
    }
}


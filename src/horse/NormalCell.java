package horse;

public class NormalCell extends BoardCell {


	public NormalCell(int position, Coordinate cod) {
		super(position, cod);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getIndex() {
		return this.position;
	}
}
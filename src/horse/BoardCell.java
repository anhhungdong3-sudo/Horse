package horse;

public abstract class BoardCell {
	protected int position;
	protected Piece piece;
	protected Coordinate coordinate;

	public BoardCell(int position, Coordinate cod) {
		this.position = position;
		this.coordinate = cod;
		this.piece = null;
	}

	public int getPosition() {
		return position;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public boolean isOccupied() {
		return piece != null;
	}

	protected abstract int getIndex();
}

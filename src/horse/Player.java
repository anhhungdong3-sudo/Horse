package horse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Player {
	protected String name;
	protected List<Piece> pieceList;
	protected List<BoardCell> gridGoal;
	protected MoveStrategy move;
	protected Coordinate barnCod;

	public Player(String name) {
		this.name = name;
		this.pieceList = new ArrayList<>();
		this.gridGoal = new ArrayList<>();
	}

	public void setMoveStrategy(MoveStrategy move) {
		this.move = move;
	}

	public abstract boolean move(int number, Piece piece, Board board);
	
	public boolean hasWon() {
		int count = 0;
		for (int i = 0; i < pieceList.size(); i++) {
			if (pieceList.get(i).getBoardPosition() == -3) {
				count++;
			}
		}
		return count == 4;
	}

	public String getName() {
		return name;
	}

	public List<Piece> getPieces() {
		return pieceList;
	}

	public void addPiece(Piece piece) {
		pieceList.add(piece);
	}

	public List<BoardCell> getGridGoal() {
		return gridGoal;
	}

	public abstract Color getColor();
	
	public abstract String getColorStr();

	public abstract boolean isAI();

}

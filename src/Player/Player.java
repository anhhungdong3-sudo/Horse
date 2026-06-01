package Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import GameSetup.Board;
import GameSetup.BoardCell;
import GameSetup.MoveStrategy;
import GameSetup.Piece;
import MVC.Coordinate;

public abstract class Player {

	// ==========================
	// 🧩 Thuộc tính cơ bản
	// ==========================
	protected String name;
	protected MoveStrategy move;
	public Coordinate barnCod;

	// ==========================
	// 🎲 Dữ liệu gameplay
	// ==========================
	public List<Piece> pieceList;
	public List<BoardCell> gridGoal;

	// ==========================
	// 🏗️ Khởi tạo
	// ==========================
	public Player(String name) {
		this.name = name;
		this.pieceList = new ArrayList<>();
		this.gridGoal = new ArrayList<>();
	}

	// ==========================
	// ⚙️ Cấu hình & truy xuất
	// ==========================
	public String getName() {
		return name;
	}

	public void setMoveStrategy(MoveStrategy move) {
		this.move = move;
	}

	public MoveStrategy getMoveStrategy() {
		return move;
	}

	public Coordinate getBarnCod() {
		return barnCod;
	}

	public void setBarnCod(Coordinate barnCod) {
		this.barnCod = barnCod;
	}

	// ==========================
	// 🧩 Quản lý quân cờ & thẻ
	// ==========================
	public List<Piece> getPieces() {
		return pieceList;
	}

	public void addPiece(Piece piece) {
		pieceList.add(piece);
	}

	public List<BoardCell> getGridGoal() {
		return gridGoal;
	}

	// ==========================
	// 🎯 Chức năng chính
	// ==========================
	public abstract boolean move(int number, Piece piece, Board board);

	public boolean hasWon() {
		int count = 0;
		for (Piece p : pieceList) {
			if (p.getBoardPosition() == -3)
				count++;
		}
		return count == 4;
	}

	// ==========================
	// 🎨 Phân biệt người chơi
	// ==========================
	public abstract Color getColor();

	public abstract String getColorStr();

	public abstract boolean isAI();
}

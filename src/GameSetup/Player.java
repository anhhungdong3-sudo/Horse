package GameSetup;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import MVC.Coordinate;

public abstract class Player {
	// ==========================
	// 🧩 Thuộc tính cơ bản
	// ==========================
	protected String name;
	protected Image pieceImage;
	protected MoveStrategy move;
	public Coordinate barnCod;
	public int totalDiceRolls = 0;
	public int[] diceFrequency = new int[6];

	// ==========================
	// 🎲 Dữ liệu gameplay
	// ==========================
	public List<Piece> pieceList;
	public List<BoardCell> gridGoal;

	// ==========================
	// 🏗️ Khởi tạo
	// ==========================
	public Player(String name, Image pieceImage) {
		this.name = name;
		this.pieceImage = pieceImage;
		this.pieceList = new ArrayList<>();
		this.gridGoal = new ArrayList<>();
	}

	// ==========================
	// ⚙️ Cấu hình & truy xuất
	// ==========================
	public String getName() {
		return name;
	}

	// lần 2 thêm hình ảnh cho quân cờ
	public Image getPieceImage() {
		return pieceImage;
	}

	public void setPieceImage(Image pieceImage) {
		this.pieceImage = pieceImage;
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

	public void recordDiceRoll(int value) {
		if (value >= 1 && value <= 6) {
			++this.totalDiceRolls;
			int n = value - 1;
			this.diceFrequency[n] = this.diceFrequency[n] + 1;
		}
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

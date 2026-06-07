package GameSetup;

import java.util.ArrayList;
import java.util.List;

import MVC.Coordinate;

public class Board {
	private List<BoardCell> gridNormal;
	private List<Piece> piecesOnBoard;

	public Board() {
		gridNormal = new ArrayList<>();
		piecesOnBoard = new ArrayList<>();
		int x = 6, y = 0;
		int[][] directions = { { 0, 1 }, // xuống
				{ -1, 0 }, // trái
				{ 0, 1 }, // xuống
				{ 1, 0 }, // phải
				{ 0, 1 }, // xuống
				{ 1, 0 }, // phải
				{ 0, -1 }, // lên
				{ 1, 0 }, // phải
				{ 0, -1 }, // lên
				{ -1, 0 }, // trái
				{ 0, -1 }, // lên
				{ -1, 0 } // trái
		};

		int[] steps = { 6, 6, 2, 6, 6, 2, 6, 6, 2, 6, 6, 2 }; // Tổng 56 bước

		int index = 0;
		for (int dir = 0; dir < directions.length; dir++) {
			int dx = directions[dir][0];
			int dy = directions[dir][1];
			for (int i = 0; i < steps[dir]; i++) {
				BoardCell bc = new NormalCell(index, new Coordinate(x, y));
				gridNormal.add(bc); // hoặc lưu index riêng
				index++;
				x += dx;
				if (x == 7 || x == 9) {
					x += dx;
				}
				y += dy;
				if (y == 7 || y == 9) {
					y += dy;
				}
			}
		}
	}

	public List<BoardCell> getGridNormal() {
		return gridNormal;
	}

	public List<Piece> getPiecesOnBoard() {
		return piecesOnBoard;
	}

	public void addPiece(Piece piece) {
		piecesOnBoard.add(piece);
		gridNormal.get(piece.getBoardPosition()).setPiece(piece);
	}

	public void removePiece(Piece piece) {
		int pos = piece.getBoardPosition();

		// Nếu đang ở trên grid thường
		if (pos >= 0 && pos < gridNormal.size()) {
			BoardCell cell = gridNormal.get(pos);
			if (cell.getPiece() == piece) {
				cell.setPiece(null);
			}
		}

		// Nếu đang ở đường goal
		if (pos == -2) {
			int goalPos = piece.getGoalPosition();
			if (goalPos >= 0 && goalPos < piece.getOwner().gridGoal.size()) {
				BoardCell goalCell = piece.getOwner().gridGoal.get(goalPos);
				if (goalCell.getPiece() == piece) {
					goalCell.setPiece(null);
				}
			}
		}

		// Nếu đã hoàn thành hoặc về chuồng (-3 hoặc -1), thì không cần xóa gì cả
	}

	public void updateTileEffects() {
		// TODO Auto-generated method stub

	}

	public int getStartPosition(String color) {
		switch (color.toLowerCase()) {
		case "red":
			return 0;
		case "blue":
			return 14;
		case "yellow":
			return 28;
		case "green":
			return 42;
		default:
			return -1;
		}
	}

	public void resetBoard() {
		for (BoardCell cell : this.gridNormal) {
			cell.setPiece(null);
		}
		this.piecesOnBoard.clear();
		System.out.println("Board has been reset.");
	}
}

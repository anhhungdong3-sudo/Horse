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
	
	public void resetBoard() {
		for (BoardCell cell : this.gridNormal) {
			cell.setPiece(null);
		}
		this.piecesOnBoard.clear();
		System.out.println("Board has been reset.");
	}
}

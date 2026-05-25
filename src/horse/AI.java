package horse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AI extends Player {
	public AI(String name) {
		super(name);
	}

	@Override
	public boolean move(int dice, Piece piece, Board board) {
		return move.move(dice, piece, board);
	}

	public abstract Piece decideMove(int dice, Board board, List<Piece> movable);



	public abstract MoveStrategy decideStrategy(Piece piece);

//	protected List<Piece> getMovablePieces(int dice) {
//		List<Piece> result = new ArrayList<>();
//		for (Piece p : pieceList) {
//			if (p.getBoardPosition() == -3)
//				continue; // Đã về đích
//
//			if (p.getBoardPosition() == -1 && (dice == 1 || dice == 6)) {
//				result.add(p); // Có thể xuất quân
//			} else if (p.getBoardPosition() >= 0 || p.getBoardPosition() == -2) {
//				result.add(p); // Trên sân hoặc đã vào nhà
//			}
//		}
//		return result;
//	}

	protected Piece getFurthestPiece(List<Piece> candidates) {
		if (candidates == null || candidates.isEmpty()) {
			return null;
		}
		List<Piece> inHouse = new ArrayList<Piece>();
		Piece furthest = candidates.get(0);

		// lọc quân đã vào nhà
		for (int i = 0; i < candidates.size(); i++) {
			Piece current = candidates.get(i);
			if (current.getBoardPosition() == -2) {
				inHouse.add(current);
			}
		}
		// sắp xếp các quân đã vào nhà từ xa nhất trước
		Comparator<Piece> comp = new Comparator<Piece>() {

			@Override
			public int compare(Piece o1, Piece o2) {
				return o2.getGoalPosition() - o1.getGoalPosition();
			}
		};
		inHouse.sort(comp);

		if (inHouse.size() > 0) {
			return furthest = inHouse.get(0);

		}
		// nếu không có quân trong nhà nào thì lựa quân đi được xa nhất trên bàn
		for (int i = 1; i < candidates.size(); i++) {
			Piece current = candidates.get(i);
			if (current.getStepsMoved() > furthest.getStepsMoved()) {
				furthest = current;
			}
		}
		return furthest;
	}

	protected Piece getDeployablePiece(int dice) {
		if (dice != 1 && dice != 6)
			return null;
		for (Piece p : pieceList) {
			if (p.getBoardPosition() == -1)
				return p;
		}
		return null;
	}

	protected Piece getPieceWithEnemyNearby(List<Piece> candidates, Board board, int range) {
		for (Piece p : candidates) {
			int currentPos = p.getBoardPosition();
			if (currentPos >= 0 && currentPos < 56) {
				for (int i = 1; i <= range; i++) {
					int target = (currentPos + i) % 56;
					BoardCell cell = board.getGridNormal().get(target);
					if (cell.getPiece() != null && cell.getPiece().getColor() != p.getColor())
						return p;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isAI() {
		return true;
	}
	
}

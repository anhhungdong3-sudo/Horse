package Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import GameSetup.Board;
import GameSetup.BoardCell;
import GameSetup.MoveStrategy;
import GameSetup.Piece;

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

	protected Piece getDeployablePiece(List<Piece> candidates, int dice) {
		if (dice != 1 && dice != 6)
			return null;
		for (Piece p : pieceList) {
			if (p.getBoardPosition() == -1)
				return p;
		}
		return null;
	}

	protected Piece getPieceWithEnemyNearby(List<Piece> candidates, Board board, int range) {
		if (range > 0) {
			for (Piece p : candidates) {
				int currentPos = p.getBoardPosition();
				if (currentPos >= 0 && p.getStepsMoved() + range <= 55) {
					for (int i = 1; i <= range; i++) {
						int target = (currentPos + i) % 56;
						BoardCell cell = board.getGridNormal().get(target);
						if (cell.getPiece() != null && cell.getPiece().getColor() != p.getColor())
							return p;
					}
				}
			}
		} else if (range < 0) {
			for (Piece p : candidates) {
				int currentPos = p.getBoardPosition();
				if (currentPos >= 0) {
					for (int i = -1; i >= range; i--) {
						int target = (currentPos + i) % 56;
						if (target < 0)
							target += 56;
						BoardCell cell = board.getGridNormal().get(target);
						if (cell.getPiece() != null && cell.getPiece().getColor() != p.getColor())
							return p;
					}
				}
			}
		}
		return null;
	}

	protected Piece canKickOrher(List<Piece> candidates, Board board, int dice) {
		for (Piece p : candidates) {
			int currentPos = p.getBoardPosition();
			Piece PieceAtSrart = board.getGridNormal().get(board.getStartPosition(p.getColor())).getPiece();
			if ((dice == 1 || dice == 6) && p.getBoardPosition() == -1) {
				if (PieceAtSrart != null && !PieceAtSrart.getColor().equals(p.getColor())) {
					return p;
				}
			}
			if (p.getBoardPosition() > -1) {
				List<BoardCell> gridNormal = board.getGridNormal();
				for (int i = 1; i <= dice; i++) {
					int midPos = (currentPos + i) % 56;
					Piece midPiece = gridNormal.get(midPos).getPiece();
					if (midPiece != null) {
						if (i == dice) {
							if (!midPiece.getColor().equals(p.getColor())) {
								return p;
							}
						}
					}
				}
			}
		}
		return null;
	}

	protected boolean lazy() {
		Random random = new Random();
		int result = random.nextInt(5);
		if (result % 5 == 0) {
			return true;
		}
		return false;
	}

	protected int getActivePieceCount() {
		int count = 0;
		for (Piece p : pieceList) {
			if (p.getBoardPosition() >= 0)
				count++;
		}
		return count;
	}

	protected boolean isInDangerAfterKick(List<Piece> candidates, Board board, int dice, int range) {
		for (Piece p : candidates) {
			int newPos = (p.getBoardPosition() + dice) % 56;
			if (newPos == 0 || newPos == 14 || newPos == 28 || newPos == 42) {
				return true;
			}
			for (int i = 1; i <= range; i++) {
				int dangerPos = (newPos - i + 56) % 56;
				Piece possibleThreat = board.getGridNormal().get(dangerPos).getPiece();
				if (possibleThreat != null && !possibleThreat.getColor().equals(p.getColor())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAI() {
		return true;
	}

}

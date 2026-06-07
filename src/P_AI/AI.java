
package P_AI;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import GameSetup.Board;
import GameSetup.BoardCell;
import GameSetup.GoalCell;
import GameSetup.MoveStrategy;
import GameSetup.Piece;
import GameSetup.Player;
import MVC.Coordinate;

public abstract class AI extends Player {
	protected String colorStr;

	public AI(String name, String colorStr, Image pieceImage) {
		super(name, pieceImage);
		this.colorStr = colorStr;
		this.pieceImage = pieceImage;
	}

	public Color getColorFromString() {
		switch (colorStr.toLowerCase()) {
		case "red":
			return Color.RED;
		case "blue":
			return Color.BLUE;
		case "green":
			return Color.GREEN;
		case "yellow":
			return Color.YELLOW;
		default:
			return Color.BLACK; // màu mặc định
		}
	}

	@Override
	public boolean isAI() {
		return true;
	}

	@Override
	public boolean move(int dice, Piece piece, Board board) {
		return this.move.move(dice, piece, board);
	}

	public abstract Piece decideMove(int var1, Board board, List<Piece> lsPiece);

	public abstract MoveStrategy decideStrategy(Piece piece);

	protected Piece getFurthestPiece(List<Piece> candidates) {
		if (candidates == null || candidates.isEmpty()) {
			return null;
		}
		ArrayList<Piece> inHouse = new ArrayList<Piece>();
		Piece furthest = candidates.get(0);
		for (Piece p : candidates) {
			if (p.getBoardPosition() != -2)
				continue;
			inHouse.add(p);
		}
		inHouse.sort(Comparator.comparingInt(Piece::getGoalPosition).reversed());
		if (!inHouse.isEmpty()) {
			return (Piece) inHouse.get(0);
		}
		for (Piece p : candidates) {
			if (p.getStepsMoved() <= furthest.getStepsMoved())
				continue;
			furthest = p;
		}
		return furthest;
	}

	protected Piece getDeployablePiece(List<Piece> candidates, int dice) {
		if (dice != 1 && dice != 6) {
			return null;
		}
		for (Piece p : this.pieceList) {
			if (p.getBoardPosition() != -1)
				continue;
			return p;
		}
		return null;
	}

	protected Piece getPieceWithEnemyNearby(List<Piece> candidates, Board board, int range) {
		block8: {
			block7: {
				if (range <= 0)
					break block7;
				for (Piece p : candidates) {
					int currentPos = p.getBoardPosition();
					if (currentPos < 0 || p.getStepsMoved() + range > 55)
						continue;
					int i = 1;
					while (i <= range) {
						int target = (currentPos + i) % 56;
						BoardCell cell = board.getGridNormal().get(target);
						if (cell.getPiece() != null && cell.getPiece().getColor() != p.getColor()) {
							return p;
						}
						++i;
					}
				}
				break block8;
			}
			if (range >= 0)
				break block8;
			for (Piece p : candidates) {
				int currentPos = p.getBoardPosition();
				if (currentPos < 0)
					continue;
				int i = -1;
				while (i >= range) {
					BoardCell cell;
					int target = (currentPos + i) % 56;
					if (target < 0) {
						target += 56;
					}
					if ((cell = board.getGridNormal().get(target)).getPiece() != null
							&& cell.getPiece().getColor() != p.getColor()) {
						return p;
					}
					--i;
				}
			}
		}
		return null;
	}

	protected Piece canKickOther(List<Piece> candidates, Board board, int dice) {
		for (Piece p : candidates) {
			int currentPos = p.getBoardPosition();
			Piece pieceAtStart = board.getGridNormal().get(board.getStartPosition(p.getColor())).getPiece();
			if (!(dice != 1 && dice != 6 || p.getBoardPosition() != -1 || pieceAtStart == null
					|| pieceAtStart.getColor().equals(p.getColor()))) {
				return p;
			}
			if (p.getBoardPosition() <= -1)
				continue;
			List<BoardCell> gridNormal = board.getGridNormal();
			int i = 1;
			while (i <= dice) {
				int midPos = (currentPos + i) % 56;
				Piece midPiece = gridNormal.get(midPos).getPiece();
				if (midPiece != null && i == dice && !midPiece.getColor().equals(p.getColor())) {
					return p;
				}
				++i;
			}
		}
		return null;
	}

	protected boolean lazy() {
		return new Random().nextInt(10) == 0;
	}

	protected int getActivePieceCount() {
		int count = 0;
		for (Piece p : this.pieceList) {
			if (p.getBoardPosition() < 0)
				continue;
			++count;
		}
		return count;
	}

	protected Piece isInDangerAfterKick(List<Piece> candidates, Board board, int dice, int range) {
		Piece re = null;
		for (Piece p : candidates) {
			int newPos = (p.getBoardPosition() + dice) % 56;
			if (newPos == 0 || newPos == 14 || newPos == 28 || newPos == 42) {
				return p;
			}
			int i = 1;
			while (i <= range) {
				int dangerPos = (newPos - i + 56) % 56;
				Piece possibleThreat = board.getGridNormal().get(dangerPos).getPiece();
				if (possibleThreat != null && !possibleThreat.getColor().equals(p.getColor())) {
					return p;
				}
				++i;
			}
		}
		return re;
	}

	protected void setupColorAndGoal(String colorStr) {
		switch (colorStr.toLowerCase()) {
		case "red": {
			this.barnCod = new Coordinate(0, 0);
			int x = 8;
			int y = 1;
			int i = 0;
			while (i < 6) {
				this.gridGoal.add(new GoalCell(i, new Coordinate(x, y++)));
				++i;
			}
			break;
		}
		case "blue": {
			this.barnCod = new Coordinate(0, 11);
			int x = 1;
			int y = 8;
			int i = 0;
			while (i < 6) {
				this.gridGoal.add(new GoalCell(i, new Coordinate(x++, y)));
				++i;
			}
			break;
		}
		case "yellow": {
			this.barnCod = new Coordinate(11, 11);
			int x = 8;
			int y = 15;
			int i = 0;
			while (i < 6) {
				this.gridGoal.add(new GoalCell(i, new Coordinate(x, y--)));
				++i;
			}
			break;
		}
		case "green": {
			this.barnCod = new Coordinate(11, 0);
			int x = 15;
			int y = 8;
			int i = 0;
			while (i < 6) {
				this.gridGoal.add(new GoalCell(i, new Coordinate(x--, y)));
				++i;
			}
			break;
		}
		}
	}
}

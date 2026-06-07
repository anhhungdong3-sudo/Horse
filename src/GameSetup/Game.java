package GameSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
	// ====================== FIELDS ======================
	private final List<Player> players;
	private final Dice dice;
	private final Board board;
	private final Random random = new Random();

	private Player currentPlayer;
	private final List<Player> ranking;
	private int roundCount = 1;

	// ====================== CONSTRUCTOR ======================
	public Game() {
		this.players = new ArrayList<>();
		this.dice = new Dice();
		this.board = new Board();
		this.ranking = new ArrayList<>();
	}

	// ====================== TURN MANAGEMENT ======================
	public void start() {
		decideTurnOrder();
		currentPlayer = players.get(0);
		System.out.println("Round: " + roundCount + "\n");
	}

	public void switchTurn(Player p) {
		System.out.println();
		// Tìm người chơi tiếp theo
		int idx = players.indexOf(p);
		for (int i = 1; i <= players.size(); i++) {
			Player next = players.get((idx + i) % players.size());
			if (!ranking.contains(next)) {
				currentPlayer = next;

				// Nếu quay lại người đầu tiên → sang round mới
				if (players.indexOf(currentPlayer) == 0) {
					roundCount++;
					System.out.println("\n============================");
					System.out.println("Round: " + roundCount);
				}

				try {
					Thread.sleep(0);
//					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	// ====================== TURN ORDER ======================
	public void decideTurnOrder() {
		Map<Player, Integer> rollResults = new HashMap<>();
		System.out.println("Rolling dice to decide turn order");

		do {
			rollResults.clear();
			for (Player player : players) {
				int roll = random.nextInt(6) + 1;
				rollResults.put(player, roll);
				System.out.println(player.getName() + " rolled: " + roll);
			}
		} while (new HashSet<>(rollResults.values()).size() < players.size());

		players.sort((p1, p2) -> rollResults.get(p2) - rollResults.get(p1));

		System.out.println("\nTurn order:");
		for (int i = 0; i < players.size(); i++) {
			System.out.println((i + 1) + ". " + players.get(i).getName());
		}
		System.out.println("Starting the game.\n============================");
	}

	// ====================== DICE & CARD ======================
	public int rollDice() {
		return dice.rollDice();
	}

	// ====================== MOVE & HIT LOGIC ======================
	public void move(MoveStrategy move, Piece piece) {
		if (piece == null) {
			System.out.println("No piece selected. Skipping move.");
			return;
		}

		if (!canMove(piece) && !canDeploy(piece, roundCount)) {
			System.out.println("This piece can't move.");
			return;
		}

		currentPlayer.setMoveStrategy(move);
		int steps = getDice().getResult();
		boolean success = currentPlayer.move(steps, piece, board);
		if (!success) {
			System.out.println("can't move.");
			return;
		}

		if (currentPlayer.hasWon())
			notifyWinner(currentPlayer);
	}

	// ====================== MOVE VALIDATION ======================
	public boolean canDeploy(Piece piece, int dice) {
		Piece start = board.getGridNormal().get(board.getStartPosition(currentPlayer.getColorStr())).getPiece();
		return (start == null || !start.getColor().equals(currentPlayer.getColorStr())) && (dice == 1 || dice == 6);
	}

	public boolean canMove(Piece piece) {
		int currentPos = piece.getBoardPosition();
		int stepMoved = piece.getStepsMoved();
		int dice = getDice().getResult();
		int newStepMoved = stepMoved + dice;
		List<BoardCell> gridGoal = currentPlayer.getGridGoal();

		if (currentPos == -3) {
			System.out.println("This piece has completed the game!");
			return false;
		}

		// Trong khu vực Goal
		if (stepMoved == 55) {
			for (int i = 0; i < dice; i++) {
				BoardCell target = gridGoal.get(i);
				Piece midPiece = target.getPiece();
				if (midPiece != null) {
					if (i == dice - 1)
						return false;
				}
			}
			return true;
		}

		if (newStepMoved > 55)
			return false;

		if (currentPos == -2) {
			int inHousePos = piece.getGoalPosition() + 1;
			return dice == inHousePos + 1 && gridGoal.get(inHousePos).getPiece() == null;
		}

		if (currentPos >= 0) {
			List<BoardCell> gridNormal = board.getGridNormal();
			int destPos = wrapIndex(currentPos + dice, 56);
			int absDice = Math.abs(dice);

			for (int i = 1; i <= absDice; i++) {
				int step = (dice > 0) ? i : -i;
				int midPos = wrapIndex(currentPos + step, 56);
				Piece midPiece = gridNormal.get(midPos).getPiece();

				if (midPiece != null) {
					boolean isLastStep = (i == absDice);
					if (isLastStep) {
						if (!midPiece.getColor().equals(piece.getColor()))
							return true;
					}
					return false;
				}
			}

			Piece destPiece = gridNormal.get(destPos).getPiece();
			if (destPiece != null && destPiece.getColor().equals(piece.getColor()))
				return false;
		}
		return true;
	}

	public List<Piece> getMovablePieces(int dice) {
		List<Piece> result = new ArrayList<>();
		for (Piece p : currentPlayer.getPieces()) {
			int pos = p.getBoardPosition();
			if (pos == -3)
				continue;
			if (pos == -1 && canDeploy(p, dice))
				result.add(p);
			else if ((pos >= 0 || pos == -2) && canMove(p))
				result.add(p);
		}
		return result;
	}

	// ====================== GAME END ======================
	public boolean isGameOver() {
		return ranking.size() >= players.size() - 1;
	}

	public void notifyWinner(Player p) {
		if (!ranking.contains(p)) {
			ranking.add(p);
			System.out.println("Player " + p.getName() + " has won! Ranking: " + ranking.size());
		}
	}

	public String getFinalResultString() {
		StringBuilder sb = new StringBuilder();
		sb.append("🎉 The game has ended!\n");
		sb.append("Total rounds played: ").append(roundCount).append("\n\n");
		sb.append("📋 Player Rankings:\n");

		int rank = 1;
		for (Player p : ranking)
			sb.append(rank++).append(". ").append(p.getName()).append("\n");
		for (Player p : players) {
			if (!ranking.contains(p)) {
				sb.append(rank++).append(". ").append(p.getName()).append(" (not finished)\n");
			}
		}
		return sb.toString();
	}

	public void resetGame() {
		this.players.clear();
		this.ranking.clear();
		this.roundCount = 1;
		this.currentPlayer = null;
		this.board.resetBoard();
		System.out.println("Game state has been reset.");
	}

	// ====================== UTILITIES ======================
	private int wrapIndex(int index, int size) {
		int mod = index % size;
		return (mod < 0) ? mod + size : mod;
	}

	// ====================== GETTERS & SETTERS ======================
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Board getBoard() {
		return board;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Player> getRanking() {
		return ranking;
	}

	public Dice getDice() {
		return dice;
	}

	public int getRoundCount() {
		return roundCount;
	}

	public void incrementRoundCount() {
		roundCount++;
	}

	public void addPlayer(Player p) {
		players.add(p);
	}

	public void resetGame() {

	}
}

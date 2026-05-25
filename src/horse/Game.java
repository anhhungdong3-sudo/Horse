package horse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Game {
	private static List<Player> players;
	private Dice dice;
	private Board board;
	private Player currentPlayer;
	private List<Player> ranking = new ArrayList<>();

	public Game() {
		this.players = new ArrayList<>();
		this.dice = new Dice();
		this.board = new Board();
	}

	public void decideTurnOrder() {
		Map<Player, Integer> rollResults = new HashMap<>();
		Random random = new Random();

		System.out.println("=== Tung xúc xắc quyết định lượt chơi ===");

		// Mỗi người chơi tung xúc xắc
		for (Player player : players) {
			int roll = random.nextInt(6) + 1;
			rollResults.put(player, roll);
			System.out.println(player.getName() + " tung được: " + roll);
		}

		// Kiểm tra trùng số → xử lý lại nếu cần
		Set<Integer> used = new HashSet<>(rollResults.values());
		while (used.size() < rollResults.size()) {
			System.out.println("Có người tung trùng số → tung lại...");
			rollResults.clear();
			for (Player player : players) {
				int roll = random.nextInt(6) + 1;
				rollResults.put(player, roll);
				System.out.println(player.getName() + " tung lại: " + roll);
			}
			used = new HashSet<>(rollResults.values());
		}

		// Sắp xếp danh sách người chơi theo số tung được (giảm dần)
		players.sort((p1, p2) -> rollResults.get(p2) - rollResults.get(p1));

		System.out.println("Thứ tự lượt chơi:");
		for (int i = 0; i < players.size(); i++) {
			System.out.println((i + 1) + ". " + players.get(i).getName());
		}
	}

	public void start() {
		decideTurnOrder(); // Sắp xếp lại players
		currentPlayer = players.get(0); // ✅ Khởi tạo sau khi có thứ tự đúng
		System.out.println("Bắt đầu trò chơi. Người chơi đầu tiên: " + currentPlayer.getName());
	}

	public void switchTurn(Player p) {
		int idx = players.indexOf(p);
		currentPlayer = players.get((idx + 1) % players.size());
	}

	public int rollDice() {
		return dice.rollDice();
	}

	// kiểm tra quân có thể ra khỏi chuồng
	public boolean canDevop(Piece piece, int dice) {
		Piece start = board.getGridNormal().get(board.getStartPosition(currentPlayer.getColorStr())).getPiece();
		if (start == null) {
			return true;
		}
		if (start != null && start.getColor() != currentPlayer.getColorStr()) {
			return true;
		}
		return false;
	}

	// kiểm tra có thể di chuyển
	public boolean canMove(Piece piece, int dice) {
		List<BoardCell> gridGoal = currentPlayer.getGridGoal();
		int currentPos = piece.getBoardPosition();
		int stepMoved = piece.getStepsMoved();
		int newStepMoved = stepMoved + dice;

		// Nếu đang ở cửa nhà
		if (stepMoved == 55) {
			// Kiem tra co bi chan khong
			for (int i = 0; i < dice; i++) {
				Piece midPiece = gridGoal.get(i).getPiece();
				if (midPiece != null) {
					return false;
				}
			}
		}

		// nếu chưa đến cửa nhà
		if (newStepMoved > 55 && stepMoved != 55) {
			return false;
		}

		// Nếu đang trong nhà
		if (currentPos == -2) {
			int inHousePos = piece.getGoalPosition() + 1;
			if (dice != inHousePos + 1 || currentPlayer.getGridGoal().get(inHousePos).getPiece() != null) {
				return false;
			}
		}
		// Nếu đang trên sân
		if (currentPos >= 0) {
			List<BoardCell> gridNormal = board.getGridNormal();

			// Nếu bị chặn
			for (int i = 1; i <= dice; i++) {
				int midPos = (currentPos + i) % 56;
				Piece midPiece = gridNormal.get(midPos).getPiece();
				if (midPiece != null) {
					if (!midPiece.getColor().equals(piece.getColor()) && i == dice) {
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}

	// lấy danh sách ngựa có thể đi của người chơi trong lượt
	public List<Piece> getMovablePieces(int dice) {
		List<Piece> result = new ArrayList<>();
		List<Piece> ls = currentPlayer.getPieces();
		for (int i = 0; i < ls.size(); i++) {
			Piece p = ls.get(i);
			// Đã về đích
			if (p.getBoardPosition() == -3)
				continue;

			// Có thể xuất quân
			if (p.getBoardPosition() == -1 && (dice == 1 || dice == 6) && canDevop(p, dice)) {
				result.add(p);

				// Trên sân hoặc đã vào nhà
			} else if (p.getBoardPosition() >= 0 || p.getBoardPosition() == -2) {
				if (canMove(p, dice)) {
					result.add(p);
				}
			}
		}
		return result;
	}

	public void move(MoveStrategy move, Piece piece) {
		currentPlayer.setMoveStrategy(move);
		currentPlayer.move(dice.getResult(), piece, board);
		if (currentPlayer.hasWon()) {
			notifyWinner(currentPlayer);
		}
	}

	public boolean isGameOver() {
		return ranking.size() >= players.size() - 1;
	}

	public void notifyWinner(Player p) {
		if (!ranking.contains(p)) {
			ranking.add(p);
			System.out.println("🎉 Player " + p.getName() + " has won! Xếp hạng: " + ranking.size());
		}
	}

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

	public void addPlayer(Player p) {
		this.players.add(p);
	}

	public Dice getDice() {
		return dice;
	}
}
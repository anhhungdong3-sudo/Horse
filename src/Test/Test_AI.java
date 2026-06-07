package Test;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import GameSetup.Game;
import GameSetup.MoveStrategy;
import GameSetup.Piece;
import GameSetup.Player;
import GameSetup.SkipTurn;
import P_AI.AI;
import P_AI.AIEasy;
import P_AI.AIHard;
import P_AI.AINormal;

//chạy test nhớ xóa slepp trong "Controller" trong "MVC" và "game" trong "GameSetup" xuống 0 cho 

public class Test_AI {
	private static final int NUM_GAMES = 100;
	private static final int MAX_TURNS = 2000;
	private static final int NumAi = 4;
	private static int MAX_TURNS_real = 0;
	private int countErrorGame = 0;
	private static List<String> AI_COLORS;
	// Thống kê số lần xếp hạng (vị trí 1,2,3,...)
	private Map<String, int[]> rankingsCount = new HashMap<>();
	// Thống kê xúc xắc
	private Map<String, Integer> totalRolls = new HashMap<>();
	private Map<String, int[]> diceFrequency = new HashMap<>();

	// Thống kê lỗi game
	private List<GameSnapshot> errorSnapshots = new ArrayList<>();

	//
	List<String[]> config = List.of(new String[] { "red", "easy" }, new String[] { "blue", "hard" },
			new String[] { "yellow", "medium" }, new String[] { "green", "easy" });

	//
	private AI createAI(String color, String difficulty) {

		Image image = switch (color.toLowerCase()) {
		case "red" -> new ImageIcon("img/pieceRed.png").getImage();
		case "blue" -> new ImageIcon("img/pieceBlue.png").getImage();
		case "yellow" -> new ImageIcon("img/pieceYellow.png").getImage();
		case "green" -> new ImageIcon("img/pieceGreen.png").getImage();
		default -> throw new IllegalArgumentException("Unknown color");
		};

		switch (difficulty.toLowerCase()) {

		case "easy":
			return new AIEasy("(AI " + color + ")", color, image);

		case "medium":
			return new AINormal("(AI " + color + ")", color, image);

		case "hard":
			return new AIHard("(AI " + color + ")", color, image);

		default:
			throw new IllegalArgumentException("Unknown difficulty");
		}
	}

	public Test_AI() {
		initAIColors();

		for (String color : AI_COLORS) {
			rankingsCount.put(color, new int[NumAi]);
			totalRolls.put(color, 0);
			diceFrequency.put(color, new int[6]);
		}
	}

	private void initAIColors() {
		AI_COLORS = new ArrayList<>();

		if (NumAi >= 1)
			AI_COLORS.add("blue");
		if (NumAi >= 2)
			AI_COLORS.add("red");
		if (NumAi >= 3)
			AI_COLORS.add("yellow");
		if (NumAi >= 4)
			AI_COLORS.add("green");
	}

	private GameSnapshot captureGameState(Game game, int gameIndex, int turnCount) {
		GameSnapshot snap = new GameSnapshot();
		snap.gameIndex = gameIndex;
		snap.turn = turnCount;
		snap.currentPlayer = game.getCurrentPlayer().getName();

		for (Player p : game.getPlayers()) {
			PlayerSnapshot ps = new PlayerSnapshot(p.getName(), p.getColorStr());

			for (Piece piece : p.getPieces()) {
				ps.pieces.add(new PieceSnapshot(piece.getId(), piece.getBoardPosition(), piece.getStepsMoved(),
						piece.getGoalPosition()));
			}

			snap.players.add(ps);
		}

		return snap;
	}

	public void run() {
		for (int i = 1; i <= NUM_GAMES; i++) {
			System.out.println("=== Starting Game #" + i + " ===");

			Game game = new Game();

			for (String[] aiInfo : config) {

				String color = aiInfo[0];
				String difficulty = aiInfo[1];

				game.addPlayer(createAI(color, difficulty));
			}

			game.start();

			boolean errorGame = false;

			int turnCount = 0;

			while (!game.isGameOver()) {

				turnCount++;

				if (turnCount > MAX_TURNS) {
					System.out.println("❌ ERROR: Game #" + i + " exceeded " + MAX_TURNS + " turns.");

					GameSnapshot snapshot = captureGameState(game, i, turnCount);
					errorSnapshots.add(snapshot);

					countErrorGame++;
					errorGame = true;
					break;
				}

				Player current = game.getCurrentPlayer();
				boolean keepTurn;

				do {
					keepTurn = false;

					int diceRoll = game.rollDice();
					current.recordDiceRoll(diceRoll);

					AI ai = (AI) current;
					List<Piece> movable = game.getMovablePieces(diceRoll);

					if (movable.isEmpty()) {
						game.move(new SkipTurn(), null);
					} else {
						Piece chosen = ai.decideMove(diceRoll, game.getBoard(), movable);

						MoveStrategy strategy = ai.decideStrategy(chosen);

						game.move(strategy, chosen);
					}

					if (diceRoll == 1 || diceRoll == 6) {
						keepTurn = true;
					}

				} while (keepTurn && !game.isGameOver());

				if (!game.isGameOver()) {
					game.switchTurn(current);
				} else {
					if (MAX_TURNS_real < turnCount)
						MAX_TURNS_real = turnCount;
				}
			}

			// Bỏ qua thống kê nếu là game lỗi
			if (errorGame) {
				countErrorGame++;
				continue;
			}

			List<Player> ranking = new ArrayList<>(game.getRanking());

			// bổ sung người chưa có trong ranking (chưa finish)
			Set<Player> ranked = new HashSet<>(ranking);

			for (Player p : game.getPlayers()) {
				if (!ranked.contains(p)) {
					ranking.add(p); // mặc định đứng cuối
				}
			}

			for (int pos = 0; pos < ranking.size(); pos++) {
				Player p = ranking.get(pos);
				String colorStr = p.getColorStr().toLowerCase();

				if (rankingsCount.containsKey(colorStr)) {
					rankingsCount.get(colorStr)[pos]++;
				}
			}

			for (Player p : game.getPlayers()) {
				String c = p.getColorStr().toLowerCase();

				totalRolls.put(c, totalRolls.get(c) + p.totalDiceRolls);

				int[] freq = diceFrequency.get(c);

				for (int j = 0; j < 6; j++) {
					freq[j] += p.diceFrequency[j];
				}
			}
		}

		printSummary();
	}

	private void printSummary() {
		System.out.println("\n=== Simulation Summary after " + NUM_GAMES + " games ===");

		System.out.println("\n🏆 Win Counts:");
		System.out.println("ErrorGame Counts: " + countErrorGame);
		System.out.println("MAX_TURNS: " + MAX_TURNS_real);

		for (String color : AI_COLORS) {
			int[] counts = rankingsCount.get(color);

			System.out.printf("%-8s | ", color.toUpperCase());

			for (int i = 0; i < NumAi; i++) {
				System.out.printf("%d:%4d |", (i + 1), counts[i]);
			}

			System.out.println();
		}
		System.out.println("\n🎲 Dice Statistics:");

		for (String color : AI_COLORS) {
			int total = totalRolls.get(color);
			int[] freq = diceFrequency.get(color);

			System.out.printf("%-8s | Total:%5d | ", color.toUpperCase(), total);

			for (int i = 0; i < 6; i++) {
				double percent = total == 0 ? 0 : (double) freq[i] * 100 / total;

				System.out.printf("%d:%4d(%6.2f%%) ", i + 1, freq[i], percent);
			}

			System.out.println();
		}
		System.out.println("\n================ ERROR GAME SNAPSHOTS ================");

		for (GameSnapshot snap : errorSnapshots) {
			System.out.println("\nGame #" + snap.gameIndex + " | Turn: " + snap.turn);
			System.out.println("Current Player: " + snap.currentPlayer);

			for (PlayerSnapshot p : snap.players) {
				System.out.println("\nPlayer: " + p.name + " (" + p.color + ")");

				for (PieceSnapshot ps : p.pieces) {
					System.out.println("Piece " + ps.id + " | board=" + ps.boardPos + " | steps=" + ps.stepsMoved
							+ " | goal=" + ps.goalPos);
				}
			}
		}

		System.out.println("=======================================================");
	}

	// main test
	public static void main(String[] args) {
		new Test_AI().run();
	}
}
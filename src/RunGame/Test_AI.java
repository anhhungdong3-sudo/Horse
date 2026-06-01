//package RunGame;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import GameSetup.Game;
//import GameSetup.MoveStrategy;
//import GameSetup.Piece;
//import GameSetup.SkipTurn;
//import Player.AI;
//import Player.AIBlue;
//import Player.AIGreen;
//import Player.AIRed;
//import Player.AIYellow;
//import Player.Player;
//
//
//
//
//
////chạy test nhớ xóa slepp trong "Controller" trong "MVC" và "game" trong "GameSetup" xuống 0 cho 
//
//
//
//
//
//
//public class Test_AI {
//	private static final int NUM_GAMES = 20;
//	private static final List<String> AI_COLORS = Arrays.asList("blue", "red", "yellow", "green");
//
//	// Thống kê số lần xếp hạng (vị trí 1,2,3,...)
//	private Map<String, int[]> rankingsCount = new HashMap<>();
//	// Thống kê xúc xắc
//	private Map<String, Integer> totalRolls = new HashMap<>();
//	private Map<String, int[]> diceFrequency = new HashMap<>();
//
//	public Test_AI() {
//		for (String color : AI_COLORS) {
//			rankingsCount.put(color, new int[4]); // max 4 players, index 0 = số lần 1st place, ...
//			totalRolls.put(color, 0);
//			diceFrequency.put(color, new int[6]);
//		}
//	}
//
//	public void run() {
//		for (int i = 1; i <= NUM_GAMES; i++) {
//			System.out.println("=== Starting Game #" + i + " ===");
//			Game game = new Game();
//			// Thêm 4 AI players
//			for (String color : AI_COLORS) {
//				switch (color) {
//				case "red":
//					game.addPlayer(new AIRed("AI Red"));
//					break;
//				case "blue":
//					game.addPlayer(new AIBlue("AI Blue"));
//					break;
//				case "yellow":
//					game.addPlayer(new AIYellow("AI Yellow"));
//					break;
//				case "green":
//					game.addPlayer(new AIGreen("AI Green"));
//					break;
//				}
//			}
//			game.start();
//
//			// Chạy vòng lặp game tới khi kết thúc
//			while (!game.isGameOver()) {
//				Player current = game.getCurrentPlayer();
//				boolean keepTurn;
//
//				do {
//					keepTurn = false;
//
//					int diceRoll = game.rollDice();
//					current.recordDiceRoll(diceRoll);
//
//					// Thực hiện move AI
//					AI ai = (AI) current;
//					List<Piece> movable = game.getMovablePieces(diceRoll);
//					Piece chosen = ai.decideMove(diceRoll, game.getBoard(), movable);
//					MoveStrategy strategy = ai.decideStrategy(chosen);
//
//					if (movable.isEmpty()) {
//						game.move(new SkipTurn(), null);
//					} else {
//						game.move(strategy, chosen);
//					}
//
//					// Nếu ra 1 hoặc 6 thì được đi tiếp
//					if (diceRoll == 1 || diceRoll == 6) {
//						System.out.println(current.getName() + " rolled a " + diceRoll + " and gets another turn!");
//						keepTurn = true;
//					}
//				} while (keepTurn && !game.isGameOver());
//
//				game.switchTurn(current); // chuyển lượt khi không còn được thêm
//			}
//
//			// Khi game kết thúc, thu thập xếp hạng và thống kê xúc xắc
//			List<Player> ranking = game.getRanking();
//
//			for (int pos = 0; pos < ranking.size(); pos++) {
//				Player p = ranking.get(pos);
//				String colorStr = p.getColorStr().toLowerCase();
//				if (rankingsCount.containsKey(colorStr)) {
//					rankingsCount.get(colorStr)[pos]++;
//				}
//			}
//
//			// Cộng thống kê xúc xắc cho từng player
//			for (Player p : game.getPlayers()) {
//				String c = p.getColorStr().toLowerCase();
//				totalRolls.put(c, totalRolls.get(c) + p.totalDiceRolls);
//				int[] freq = diceFrequency.get(c);
//				for (int j = 0; j < 6; j++) {
//					freq[j] += p.diceFrequency[j];
//				}
//			}
//		}
//
//		printSummary();
//	}
//
//	private void printSummary() {
//		System.out.println("\n=== Simulation Summary after " + NUM_GAMES + " games ===\n");
//		System.out.println("🏆 Win Counts:");
//		for (String color : AI_COLORS) {
//			int[] counts = rankingsCount.get(color);
//			System.out.printf("- %s: 1st: %d, 2nd: %d, 3rd: %d, 4th: %d\n", color, counts[0], counts[1], counts[2],
//					counts[3]);
//		}
//
//		System.out.println("\n🎲 Dice Statistics:");
//		for (String color : AI_COLORS) {
//			int total = totalRolls.get(color);
//			int[] freq = diceFrequency.get(color);
//			System.out.printf("- %s: Total Rolls: %d | ", color, total);
//			for (int i = 0; i < 6; i++) {
//				System.out.print((i + 1) + ":" + freq[i] + " ");
//			}
//			System.out.println();
//		}
//	}
//
//	// main test
//	public static void main(String[] args) {
//		new Test_AI().run();
//	}
//}

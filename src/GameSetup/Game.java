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

	public void resetGame() {
		this.players.clear();
		this.ranking.clear();
		this.roundCount = 1;
		this.currentPlayer = null;
		this.board.resetBoard();
		System.out.println("Game state has been reset.");
	}
}

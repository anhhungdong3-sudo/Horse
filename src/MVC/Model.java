package MVC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import GameSetup.DeployHorse;
import GameSetup.Game;
import GameSetup.MoveStrategy;
import GameSetup.NormalMove;
import GameSetup.Piece;
import GameSetup.SkipTurn;
import Player.AI;
import Player.AIBlue;
import Player.AIGreen;
import Player.AIRed;
import Player.AIYellow;
import Player.HumanBlue;
import Player.HumanGreen;
import Player.HumanRed;
import Player.HumanYellow;
import Player.Player;

public class Model implements Subject {
	private Game game;
	private List<Observer> obList = new ArrayList<>();

	public Model() {
		this.game = new Game();
	}

	public Game getGame() {
		return game;
	}

	public void addObserver(Observer ob) {
		obList.add(ob);
	}

	@Override
	public void removeObserver(Observer ob) {
		obList.remove(ob);
	}

	// 🎲 Tung xúc xắc
	public void rollDice() {
		game.rollDice();
	}

	// ⚙️ Thiết lập người chơi (mới, có nhân vật)
	public void setUpGame(List<String> humanColors, int totalPlayers) {
		Set<String> allColors = new HashSet<>(Arrays.asList("red", "blue", "yellow", "green"));

		// Thêm người chơi thật với màu + nhân vật
		// humanColors: ["red","blue"]
		// humanCharacters: [PlayerCharacter1, PlayerCharacter2]
		for (int i = 0; i < humanColors.size(); i++) {
			String color = humanColors.get(i);
			switch (color) {
			case "red" -> game.addPlayer(new HumanRed("(Player RED)"));
			case "blue" -> game.addPlayer(new HumanBlue("(Player BLUE)"));
			case "yellow" -> game.addPlayer(new HumanYellow("(Player YELLOW)"));
			case "green" -> game.addPlayer(new HumanGreen("(Player GREEN)"));
			}
		}

		// Thêm AI nếu còn thiếu
		allColors.removeAll(humanColors);
		int aiToAdd = totalPlayers - humanColors.size();
		Iterator<String> it = allColors.iterator();

		for (int i = 0; i < aiToAdd && it.hasNext(); i++) {
			String color = it.next();
			switch (color) {
			case "red" -> game.addPlayer(new AIRed("(AI RED)"));
			case "blue" -> game.addPlayer(new AIBlue("(AI BLUE)"));
			case "yellow" -> game.addPlayer(new AIYellow("(AI YELLOW)"));
			case "green" -> game.addPlayer(new AIGreen("(AI GREEN)"));
			}
		}
	}

	public void start() {
		game.start();
		notifyStart();
		turn();
	}

	public void turn() {
		if (!game.isGameOver()) {
			Player currentPlayer = game.getCurrentPlayer();
			if (currentPlayer.isAI())
				notifyItsAI();
			else
				notifyItsHuman();
		} else
			notifyWin();
	}

	// 🧠 Lượt AI
	public void handleAITurn(AI currentPlayer) {
		int number = game.getDice().getResult();
		List<Piece> lsPiece = game.getMovablePieces(number);

		Piece chosenPiece = currentPlayer.decideMove(number, game.getBoard(), lsPiece);
		MoveStrategy strategy = currentPlayer.decideStrategy(chosenPiece);

		if (lsPiece.isEmpty()) {
			System.out.println("Player " + currentPlayer.getName() + " cannot move.");
			game.move(new SkipTurn(), null);
		} else {
			game.move(strategy, chosenPiece);
		}

		notifyMove();
		switchTurn();
		SwingUtilities.invokeLater(this::turn);
	}

	public void switchTurn() {
		Player current = game.getCurrentPlayer();
		if (game.getDice().getResult() != 1 && game.getDice().getResult() != 6) {
			game.switchTurn(current);
			notifySwitchTurn();
		}
	}

	// 🐴 Ra quân
	public void deploy() {
		Piece deployPiece = null;
		for (Piece p : game.getCurrentPlayer().pieceList) {
			if (p.getBoardPosition() == -1) {
				deployPiece = p;
				break;
			}
		}
		game.move(new DeployHorse(), deployPiece);
		notifyMove();
		switchTurn();
	}

	// 🧭 Di chuyển quân
	public void move(Piece piece) {
		game.move(new NormalMove(), piece);
		notifyMove();
		SwingUtilities.invokeLater(() -> {
			switchTurn();
			turn();
		});
	}

	// 🧱 Observer
	@Override
	public void notifyStart() {
		for (Observer o : obList)
			o.updateStart();
	}

	@Override
	public void notifySwitchTurn() {
		for (Observer o : obList)
			o.updateSwitchTurn();
	}

	@Override
	public void notifyItsAI() {
		for (Observer o : obList)
			o.updateItsAI();
	}

	@Override
	public void notifyItsHuman() {
		for (Observer o : obList)
			o.updateItsHuman();
	}

	@Override
	public void notifyMove() {
		for (Observer o : obList)
			o.updateMove();
	}

	@Override
	public void notifyWin() {
		for (Observer o : obList)
			o.updateWin();
	}
}

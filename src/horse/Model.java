package horse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

public class Model implements Subject {
	private Game game;
	private List<Observer> obList = new ArrayList<>();

	public Model() {
		super();
		this.game = new Game();
	}

	public Game getGame() {
		return game;
	}

	public void addObserver(Observer ob) {
		// TODO Auto-generated method stub
		obList.add(ob);
	}

	@Override
	public void removeObserver(Observer ob) {
		// TODO Auto-generated method stub
		obList.remove(ob);
	}

	public void rollDice() {
		game.rollDice();
//		notifyObserver();
	}

	public void setupGame(int playerCount, String playerColor) {
		Set<String> allColors = new HashSet<>(Arrays.asList("red", "blue", "yellow", "green"));
		switch (playerColor.toLowerCase()) {
		case "red":
			this.game.addPlayer(new HumanRed("Player"));
			break;
		case "blue":
			this.game.addPlayer(new HumanBlue("Player"));
			break;
		case "yellow":
			this.game.addPlayer(new HumanYellow("Player"));
			break;
		case "green":
			this.game.addPlayer(new HumanGreen("Player"));
			break;
		default:
			return;
		}

		allColors.remove(playerColor.toLowerCase());
		// Thêm các AI còn lại
		int aiToAdd = playerCount - 1;
		Iterator<String> it = allColors.iterator();

		while (aiToAdd > 0 && it.hasNext()) {
			String color = it.next();
			switch (color) {
			case "red":
				game.addPlayer(new AIRed("AI Red"));
				break;
			case "blue":
				game.addPlayer(new AIBlue("AI Blue"));
				break;
			case "yellow":
				game.addPlayer(new AIYellow("AI Yellow"));
				break;
			case "green":
				game.addPlayer(new AIGreen("AI Green"));
				break;
			}
			aiToAdd--;
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
			if (currentPlayer.isAI()) {
				notifyItsAI();
			} else {
				notifyItsHuman();
			}
		} else {
			notifyWin();
		}
	}

	public void handleAITurn(AI currentPlayer) {
		AI ai = (AI) currentPlayer;
		int number = game.getDice().getResult();

		System.out.println("Number: " + number);
		List<Piece> lsPiece = game.getMovablePieces(number);
		Piece chosenPiece = ai.decideMove(number, game.getBoard(), lsPiece);
		MoveStrategy strategy = ai.decideStrategy(chosenPiece);

		if (chosenPiece == null || strategy instanceof SkipTurn) {
			System.out.println("💀 Người chơi " + currentPlayer.getName() + " không thể đi.");
			game.move(new SkipTurn(), null);
			notifyMove();
		} else {
			game.move(strategy, chosenPiece);
		}
		notifyMove();
		switchTurn();

		// Gọi tiếp người tiếp theo
		SwingUtilities.invokeLater(this::turn);
	}

	public void switchTurn() {
		if (game.getDice().getResult() != 1 && game.getDice().getResult() != 6) {
			game.switchTurn(game.getCurrentPlayer());
			notifySwitchTurn();
		}
	}

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

	public void move(Piece piece) {
		game.move(new NormalMove(), piece);
		notifyMove();
		switchTurn();
	}

	@Override
	public void notifyStart() {
		for (Observer o : obList) {
			o.updateStart();
		}
	}

	@Override
	public void notifySwitchTurn() {
		for (Observer o : obList) {
			o.updateSwitchTurn();
		}
	}

	@Override
	public void notifyItsAI() {
		for (Observer o : obList) {
			o.updateItsAI();
		}
	}

	@Override
	public void notifyItsHuman() {
		for (Observer o : obList) {
			o.updateItsHuman();
		}
	}

	@Override
	public void notifyMove() {
		for (Observer o : obList) {
			o.updateMove();
		}
	}

	@Override
	public void notifyWin() {
		for (Observer o : obList) {
			o.updateWin();
		}
	}

}

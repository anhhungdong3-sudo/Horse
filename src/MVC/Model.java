package MVC;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import GameSetup.DeployHorse;
import GameSetup.Game;
import GameSetup.MoveStrategy;
import GameSetup.NormalMove;
import GameSetup.Piece;
<<<<<<< Updated upstream
import Player.Player;
=======
import GameSetup.Player;
>>>>>>> Stashed changes
import GameSetup.SkipTurn;
import P_AI.AI;
import P_AI.AIEasy;
import P_AI.AIHard;
import P_AI.AINormal;
<<<<<<< Updated upstream
import Player.HumanBlue;
import Player.HumanGreen;
import Player.HumanRed;
import Player.HumanYellow;
=======
import P_Human.HumanBlue;
import P_Human.HumanGreen;
import P_Human.HumanRed;
import P_Human.HumanYellow;
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
    // 🎲 Tung xúc xắc
    public void rollDice() {
        game.rollDice();
    }

    public static Image getPieceImage(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return new ImageIcon("img/pieceRed.png").getImage();
            case "blue":
                return new ImageIcon("img/pieceBlue.png").getImage();
            case "yellow":
                return new ImageIcon("img/pieceYellow.png").getImage();
            case "green":
                return new ImageIcon("img/pieceGreen.png").getImage();
            default:
                throw new IllegalArgumentException("Màu không hợp lệ: " + color);
        }
    }

    // ⚙️ Thiết lập người chơi
    public void setUpGame(List<String> humanColors, int humanCount, int totalPlayers, String DifficultyChose) {
        int aiCount = totalPlayers - humanCount;
        
        // ===== 0. DANH SÁCH MÀU GỐC =====
        List<String> allColors = new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow"));
        List<String> chosenColors = new ArrayList<>(humanColors);
        List<String> randomPool = new ArrayList<>(allColors);
        randomPool.removeAll(chosenColors);

        List<String> finalHumanColors = new ArrayList<>();
        List<String> finalAIColors = new ArrayList<>();
        Random rand = new Random();
=======
	public static Image getPieceImage(String color) {
		switch (color.toLowerCase()) {
		case "red":
			return new ImageIcon("img/pieceRed.png").getImage();
		case "blue":
			return new ImageIcon("img/pieceBlue.png").getImage();
		case "yellow":
			return new ImageIcon("img/pieceYellow.png").getImage();
		case "green":
			return new ImageIcon("img/pieceGreen.png").getImage();
		default:
			throw new IllegalArgumentException("Màu không hợp lệ: " + color);
		}
	}

	// ⚙️ Thiết lập người chơi (mới, có nhân vật)
	// lần 2
	// thêm ảnh quân cờ cho từng người chơi
	// thêm cơ chế ramdom màu nếu chưa chọn hya chọn thiếu màu.
	public void setUpGame(List<String> humanColors, int humanCount, int totalPlayers, String DifficultyChose) {
		int aiCount = totalPlayers - humanCount;

		// ===== 0. DANH SÁCH MÀU GỐC =====
		List<String> allColors = new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow"));

		// Màu người dùng tick (có thể dư)
		List<String> chosenColors = new ArrayList<>(humanColors);

		// Pool random (loại bỏ màu đã tick)
		List<String> randomPool = new ArrayList<>(allColors);
		randomPool.removeAll(chosenColors);

		List<String> finalHumanColors = new ArrayList<>();
		List<String> finalAIColors = new ArrayList<>();

		Random rand = new Random();

		// Ưu tiên màu đã tick
		for (String color : chosenColors) {
			if (finalHumanColors.size() >= humanCount)
				break;
			finalHumanColors.add(color);
		}

		// Random bổ sung nếu thiếu
		while (finalHumanColors.size() < humanCount && !randomPool.isEmpty()) {
			finalHumanColors.add(randomPool.remove(rand.nextInt(randomPool.size())));
		}

		/*
		 * 2. PHÂN MÀU CHO AI
		 */
		// Loại bỏ màu đã dùng cho human
		chosenColors.removeAll(finalHumanColors);

		// Ưu tiên màu tick dư cho AI
		for (String color : chosenColors) {
			if (finalAIColors.size() >= aiCount)
				break;
			finalAIColors.add(color);
		}

		// Random bổ sung nếu thiếu
		while (finalAIColors.size() < aiCount && !randomPool.isEmpty()) {
			finalAIColors.add(randomPool.remove(rand.nextInt(randomPool.size())));
		}

		// ====== ADD HUMAN PLAYERS ======
		for (String color : finalHumanColors) {
			switch (color) {
			case "red":
				game.addPlayer(new HumanRed("(Player RED)", getPieceImage(color)));
				break;
			case "blue":
				game.addPlayer(new HumanBlue("(Player BLUE)", getPieceImage(color)));
				break;
			case "yellow":
				game.addPlayer(new HumanYellow("(Player YELLOW)", getPieceImage(color)));
				break;
			case "green":
				game.addPlayer(new HumanGreen("(Player GREEN)", getPieceImage(color)));
				break;
			}
		}

		// ====== ADD AI PLAYERS ======
		for (int i = 0; i < aiCount; i++) {
			String color = finalAIColors.get(i);
			String aiName = "(AI " + color.toUpperCase() + " | " + DifficultyChose + ")";
			switch (DifficultyChose) {
			case "Easy":
				game.addPlayer(new AIEasy(aiName, color, getPieceImage(color)));
				break;

			case "Normal":
				game.addPlayer(new AINormal(aiName, color, getPieceImage(color)));
				break;

			case "Hard":
				game.addPlayer(new AIHard(aiName, color, getPieceImage(color)));
				break;
			}
		}
	}
>>>>>>> Stashed changes

        // Ưu tiên màu đã tick cho Human
        for (String color : chosenColors) {
            if (finalHumanColors.size() >= humanCount) break;
            finalHumanColors.add(color);
        }

        // Random bổ sung nếu thiếu
        while (finalHumanColors.size() < humanCount && !randomPool.isEmpty()) {
            finalHumanColors.add(randomPool.remove(rand.nextInt(randomPool.size())));
        }

        // PHÂN MÀU CHO AI
        chosenColors.removeAll(finalHumanColors);
        for (String color : chosenColors) {
            if (finalAIColors.size() >= aiCount) break;
            finalAIColors.add(color);
        }
        while (finalAIColors.size() < aiCount && !randomPool.isEmpty()) {
            finalAIColors.add(randomPool.remove(rand.nextInt(randomPool.size())));
        }

        // ====== ADD HUMAN PLAYERS ======
        for (String color : finalHumanColors) {
            switch (color) {
                case "red":
                    game.addPlayer(new HumanRed("(Player RED)", getPieceImage(color)));
                    break;
                case "blue":
                    game.addPlayer(new HumanBlue("(Player BLUE)", getPieceImage(color)));
                    break;
                case "yellow":
                    game.addPlayer(new HumanYellow("(Player YELLOW)", getPieceImage(color)));
                    break;
                case "green":
                    game.addPlayer(new HumanGreen("(Player GREEN)", getPieceImage(color)));
                    break;
            }
        }

        // ====== ADD AI PLAYERS ======
        for (int i = 0; i < aiCount; i++) {
            String color = finalAIColors.get(i);
            String aiName = "(AI " + color.toUpperCase() + " | " + DifficultyChose + ")";
            switch (DifficultyChose) {
                case "Easy":
                    game.addPlayer(new AIEasy(aiName, color, getPieceImage(color)));
                    break;
                case "Normal":
                    game.addPlayer(new AINormal(aiName, color, getPieceImage(color)));
                    break;
                case "Hard":
                    game.addPlayer(new AIHard(aiName, color, getPieceImage(color)));
                    break;
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

        notifyMove();           // 1.2 notifyMove() (cho AI)
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
        notifyMove();           // 1.2 notifyMove()
        switchTurn();
    }

    // 🧭 Di chuyển quân - PHẦN QUAN TRỌNG THEO SEQUENCE
    public void move(Piece piece) {
        // 1.1 game.move()
        game.move(new NormalMove(), piece);

        // 1.2 notifyMove()
        notifyMove();

        SwingUtilities.invokeLater(() -> {
            switchTurn();
            turn();
        });
    }

    // ==================== OBSERVER NOTIFY ====================

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
        // 1.2 notifyMove() - Thông báo cho View cập nhật
        for (Observer o : obList)
            o.updateMove();     // → Gọi 1.3 updateMove() ở View
    }

    @Override
    public void notifyWin() {
        for (Observer o : obList)
            o.updateWin();
    }
}
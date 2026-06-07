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
import GameSetup.Player;
import GameSetup.SkipTurn;
import P_AI.AI;
import P_AI.AIEasy;
import P_AI.AIHard;
import P_AI.AINormal;
import P_Human.HumanBlue;
import P_Human.HumanGreen;
import P_Human.HumanRed;
import P_Human.HumanYellow;

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

    public static Image getPieceImage(String color) {
        switch (color.toLowerCase()) {
            case "red":    return new ImageIcon("img/pieceRed.png").getImage();
            case "blue":   return new ImageIcon("img/pieceBlue.png").getImage();
            case "yellow": return new ImageIcon("img/pieceYellow.png").getImage();
            case "green":  return new ImageIcon("img/pieceGreen.png").getImage();
            default:
                throw new IllegalArgumentException("Màu không hợp lệ: " + color);
        }
    }

    // ⚙️ Thiết lập game
    public void setUpGame(List<String> humanColors, int humanCount, int totalPlayers, String DifficultyChose) {
        int aiCount = totalPlayers - humanCount;

        List<String> allColors = new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow"));
        List<String> chosenColors = new ArrayList<>(humanColors);
        List<String> randomPool = new ArrayList<>(allColors);
        randomPool.removeAll(chosenColors);

        List<String> finalHumanColors = new ArrayList<>();
        List<String> finalAIColors = new ArrayList<>();
        Random rand = new Random();

        // Ưu tiên màu người dùng đã chọn
        for (String color : chosenColors) {
            if (finalHumanColors.size() >= humanCount) break;
            finalHumanColors.add(color);
        }

        // Random bổ sung nếu thiếu
        while (finalHumanColors.size() < humanCount && !randomPool.isEmpty()) {
            finalHumanColors.add(randomPool.remove(rand.nextInt(randomPool.size())));
        }

        // Phân bổ màu cho AI
        chosenColors.removeAll(finalHumanColors);
        for (String color : chosenColors) {
            if (finalAIColors.size() >= aiCount) break;
            finalAIColors.add(color);
        }
        while (finalAIColors.size() < aiCount && !randomPool.isEmpty()) {
            finalAIColors.add(randomPool.remove(rand.nextInt(randomPool.size())));
        }

        // Add Human Players
        for (String color : finalHumanColors) {
            switch (color) {
                case "red":    game.addPlayer(new HumanRed("(Player RED)", getPieceImage(color))); break;
                case "blue":   game.addPlayer(new HumanBlue("(Player BLUE)", getPieceImage(color))); break;
                case "yellow": game.addPlayer(new HumanYellow("(Player YELLOW)", getPieceImage(color))); break;
                case "green":  game.addPlayer(new HumanGreen("(Player GREEN)", getPieceImage(color))); break;
            }
        }

        // Add AI Players
        for (int i = 0; i < aiCount; i++) {
            String color = finalAIColors.get(i);
            String aiName = "(AI " + color.toUpperCase() + " | " + DifficultyChose + ")";
            switch (DifficultyChose) {
                case "Easy":   game.addPlayer(new AIEasy(aiName, color, getPieceImage(color))); break;
                case "Normal": game.addPlayer(new AINormal(aiName, color, getPieceImage(color))); break;
                case "Hard":   game.addPlayer(new AIHard(aiName, color, getPieceImage(color))); break;
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
        } else {
            notifyWin();
        }
    }

    // ====================== UC1 - ANIMATION ======================

    /**
     * Xử lý lượt AI
     */
    public void handleAITurn(AI currentPlayer) {
        int number = game.getDice().getResult();
        List<Piece> lsPiece = game.getMovablePieces(number);
        Piece chosenPiece = currentPlayer.decideMove(number, game.getBoard(), lsPiece);
        MoveStrategy strategy = currentPlayer.decideStrategy(chosenPiece);

        if (lsPiece.isEmpty()) {
            System.out.println("Player " + currentPlayer.getName() + " cannot move.");
            game.move(new SkipTurn(), null);
        } else {
            game.move(strategy, chosenPiece);     // 1.1 game.move()
        }

        notifyMove();                             // 1.2 notifyMove()
        switchTurn();
        SwingUtilities.invokeLater(this::turn);
    }

    /**
     * 1.1 game.move() + 1.2 notifyMove()
     * Ra quân (Deploy)
     */
    public void deploy() {
        Piece deployPiece = null;
        for (Piece p : game.getCurrentPlayer().pieceList) {
            if (p.getBoardPosition() == -1) {
                deployPiece = p;
                break;
            }
        }

        game.move(new DeployHorse(), deployPiece);   // 1.1 game.move()
        notifyMove();                                // 1.2 notifyMove()
        switchTurn();
    }

    /**
     * 1.1 game.move() + 1.2 notifyMove()
     * Di chuyển quân bình thường
     */
    public void move(Piece piece) {
        game.move(new NormalMove(), piece);          // 1.1 game.move()
        notifyMove();                                // 1.2 notifyMove()

        SwingUtilities.invokeLater(() -> {
            switchTurn();
            turn();
        });
    }

    public void switchTurn() {
        Player current = game.getCurrentPlayer();
        if (game.getDice().getResult() != 1 && game.getDice().getResult() != 6) {
            game.switchTurn(current);
            notifySwitchTurn();
        }
    }

    // ====================== NOTIFY METHODS (Observer) ======================

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

    /**
     * 1.2 notifyMove()
     * Thông báo cho View cập nhật giao diện (animation)
     */
    @Override
    public void notifyMove() {
        for (Observer o : obList)
            o.updateMove();          // → View sẽ nhận và gọi repaint()
    }

    @Override
    public void notifyWin() {
        for (Observer o : obList)
            o.updateWin();
    }
}
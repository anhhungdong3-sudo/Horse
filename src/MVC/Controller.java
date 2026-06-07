package MVC;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import GameSetup.BoardCell;
import GameSetup.Dice;
import GameSetup.Game;
import GameSetup.Piece;
import Player.Player;
import GameSetup.SkipTurn;
import P_AI.AI;

public class Controller implements Observer {
	private View view;
	private Model model;
	private Piece selectedPiece = null;
	private int historyCounter = 0;
	private boolean hasRolledDice = false;
	private boolean Pause = false;

	public Controller(Model model) {
		this.model = model;
		this.model.addObserver(this);
	}

	public void setView(View view) {
		this.view = view;
	}

	public Piece getSelectedPiece() {
		return selectedPiece;
	}

	public void setSelectedPiece(Piece selectedPiece) {
		this.selectedPiece = selectedPiece;
	}

	// lần 2 thêm nút dừng và reset
	public boolean isPause() {
		return Pause;
	}

	public void setPause(boolean pause) {
		Pause = pause;
	}

	public void resumeGame() {
		model.turn();
	}

	public void resetGame() {
		model.getGame().resetGame();
	}

	// ===========================
	// 💻 Thiết lập và bắt đầu game
	// ===========================
	public void startGame() {
		model.start();
	}

	// ===========================
	// 🎲 Roll dice
	// ===========================
	public void rollDice() {
		if (isPause())
			return;
		hasRolledDice = false;
		Dice dice = model.getGame().getDice();
		Player player = model.getGame().getCurrentPlayer();
		view.btnRoll.setEnabled(false);

		new Thread(() -> {
			try {
				for (int i = 0; i < 15; i++) {
					dice.rollDice();
					view.setIconDice(dice.getResult());
					Thread.sleep(0);
//					Thread.sleep(100);
				}

				SwingUtilities.invokeLater(() -> {
					if (historyCounter == 20) {
						view.jta.setText("");
						historyCounter = 0;
					}
					view.jta.append(player.getName() + ": Rolled " + dice.getResult() + "\n");
					historyCounter++;
				});

				if (player.isAI())
					model.handleAITurn((AI) player);
				else {
					hasRolledDice = true;
					checkAndSkipIfNoMove();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		view.repaint();
	}

	// ===========================
	// ✅ Kiểm tra lượt có thể đi, dùng thẻ hoặc deploy
	// ===========================
	public void checkAndSkipIfNoMove() {
		Game game = model.getGame();
		Player player = game.getCurrentPlayer();
		int diceResult = game.getDice().getResult();
		List<Piece> movable = game.getMovablePieces(diceResult);

		boolean hasMovable = !movable.isEmpty();
		boolean canDeploy = canDeploy(player, diceResult);

		if (!hasMovable) {
			autoSkipTurn();
			return;
		}
		view.btnDeploy.setEnabled(canDeploy);
		view.btnSkipTurn.setEnabled(true);
		view.btnRoll.setEnabled(false);
	}

	private boolean canDeploy(Player player, int diceResult) {
		if (diceResult != 1 && diceResult != 6)
			return false;
		for (Piece p : player.getPieces()) {
			if (p.getBoardPosition() == -1 && model.getGame().canDeploy(p, diceResult))
				return true;
		}
		return false;
	}

	private void resetButtonsAfterSkip() {
		view.btnRoll.setEnabled(true);
		view.btnDeploy.setEnabled(false);
		view.btnSkipTurn.setEnabled(false);
	}

	// ===========================
	// 🟢 Click quân cờ
	// ===========================
	public void pieceClick(Point click) {
		Game game = model.getGame();
		Player player = game.getCurrentPlayer();
		if (player.isAI() || !hasRolledDice)
			return;

		Piece piece = findPieceAtClick(click, player, game);
		if (piece == null)
			return;

		List<Piece> movable = game.getMovablePieces(game.getDice().getResult());
		if (!movable.contains(piece))
			return;

		if (piece.equals(selectedPiece))
			moveSelectedPiece();
		else
			selectedPiece = piece;

		view.repaint();
	}

	private void moveSelectedPiece() {
		if (selectedPiece != null) {
			model.move(selectedPiece);
			model.turn();
			selectedPiece = null;
			view.repaint();
			hasRolledDice = false;
		}
	}

	private Piece findPieceAtClick(Point click, Player player, Game game) {
		Piece piece = null;
		for (Map.Entry<Integer, Rectangle> entry : this.view.positionToBounds.entrySet()) {
			BoardCell cell = game.getBoard().getGridNormal().get(entry.getKey());
			if (!entry.getValue().contains(click) || (piece = cell.getPiece()) == null)
				continue;
			return piece;
		}
		for (BoardCell goal : player.gridGoal) {
			Rectangle rect = new Rectangle(goal.coordinate.getX() * this.view.cellSize,
					goal.coordinate.getY() * this.view.cellSize, this.view.cellSize, this.view.cellSize);
			piece = goal.getPiece();
			if (!rect.contains(click) || piece == null)
				continue;
			return piece;
		}
		return null;
	}

	// ===========================
	// ⏭️ Skip/Deploy/Draw
	// ===========================
	public void autoSkipTurn() {
		selectedPiece = null;
		resetButtonsAfterSkip();
		model.switchTurn();
		model.turn();
		hasRolledDice = false;
		view.repaint();
	}

	public void skipTurn() {
		selectedPiece = null;
		model.getGame().move(new SkipTurn(), null);
		resetButtonsAfterSkip();
		model.switchTurn();
		model.turn();
		hasRolledDice = false;
		view.repaint();
	}

	public void deploy() {
		model.deploy();
		model.turn();
		hasRolledDice = false;
		view.repaint();
	}

	// ===========================
	// Right click
	// ===========================
	public void handleRightClick() {
		if (selectedPiece != null) {
			selectedPiece = null;
			view.repaint();
		}
	}

	// ===========================
	// Observer updates
	// ===========================
	@Override
	public void updateStart() {
		view.turnLabel.setBackground(model.getGame().getCurrentPlayer().getColor());
	}

	@Override
	public void updateSwitchTurn() {
		view.turnLabel.setBackground(model.getGame().getCurrentPlayer().getColor());
	}

	@Override
	public void updateItsAI() {
		rollDice();
	}

	@Override
	public void updateItsHuman() {
	}

	@Override
	public void updateMove() {
	}

	@Override
	public void updateWin() {
	}
}

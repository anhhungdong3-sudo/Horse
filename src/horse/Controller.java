package horse;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

public class Controller implements Observer {
	private View view;
	private Model model;

	private Color redColor = new Color(255, 102, 102);
	private Color blueColor = new Color(102, 178, 255);
	private Color yellowColor = new Color(255, 230, 100);
	private Color greenColor = new Color(100, 220, 153);

	private int historyCounter = 0;

	private boolean hasRolledDice = false;

	public Controller(Model model) {
		super();
		this.model = model;
		this.model.addObserver(this);
	}

	public void setUpGame(int playerCount, String color) {
		model.setupGame(playerCount, color);
		System.out.println("Bắt đầu game với " + playerCount + " người chơi, màu của bạn là " + color);
	}

	public void startGame() {
		model.start();
	}

	public void setView(View view) {
		this.view = view;
	}

	public void makeMap(Map<Integer, Rectangle> positionToBounds, int cellSize) {
		for (BoardCell cell : model.getGame().getBoard().getGridNormal()) {
			Coordinate coord = cell.coordinate;
			int x = coord.getX() * cellSize;
			int y = coord.getY() * cellSize;
			Rectangle rect = new Rectangle(x, y, cellSize, cellSize);
			positionToBounds.put(cell.getPosition(), rect);
		}
	}

	public void drawPath(int cellSize, Graphics2D g2d) {
		Board board = model.getGame().getBoard();
		for (BoardCell bc : board.getGridNormal()) {
			Coordinate cod = bc.coordinate;
			g2d.setColor(Color.gray);
			g2d.fillOval(cod.getX() * cellSize, cod.getY() * cellSize, cellSize, cellSize);
			g2d.setColor(Color.white);
			g2d.fillOval(cod.getX() * cellSize + 5, cod.getY() * cellSize + 5, cellSize - 10, cellSize - 10);
			if ((bc.getIndex() + 1) % 14 == 0) {
				g2d.setColor(Color.gray);
				g2d.fillRect(cod.getX() * cellSize + 12, cod.getY() * cellSize + 12, cellSize - 24, cellSize - 24);
			} else if (bc.getIndex() % 14 == 0) {
				g2d.setColor(Color.gray);
				g2d.fillOval(cod.getX() * cellSize + 12, cod.getY() * cellSize + 12, cellSize - 24, cellSize - 24);
			}
		}
	}

	private void drawingPiece(Graphics2D g2d, BoardCell cell, Color color, int cellSize) {
		int padding = cellSize / 6; // cho đẹp, tránh sát viền
		int x = cell.coordinate.getX() * cellSize + padding;
		int y = cell.coordinate.getY() * cellSize + padding;
		int size = cellSize - 2 * padding;

		g2d.setColor(color);
		g2d.fillOval(x, y, size, size); // tô màu
		g2d.setColor(Color.BLACK);
		g2d.drawOval(x, y, size, size); // viền đen
	}

	public void drawPiece(int cellSize, Graphics2D g2d) {
		Board board = model.getGame().getBoard();
		List<Player> listPlayer = model.getGame().getPlayers();
		for (Player player : listPlayer) {
			for (Piece p : player.pieceList) {
				int boardPos = p.getBoardPosition();
				if (boardPos != -1) {
					if (boardPos <= -2) {
						int goalPos = p.getGoalPosition();
						List<BoardCell> goalList = p.getOwner().gridGoal;
						BoardCell goalCell = goalList.get(goalPos);
						drawingPiece(g2d, goalCell, p.getOwner().getColor(), cellSize);
					} else {
						BoardCell boardCell = board.getGridNormal().get(boardPos);
						drawingPiece(g2d, boardCell, p.getOwner().getColor(), cellSize);
					}
				}
			}
		}
	}

	public void drawPieceAtBarn(int cellSize, Graphics2D g2d) {
		List<Player> players = model.getGame().getPlayers();
		for (Player player : players) {
			Color color = player.getColor(); // giả sử PlayerColor có getColor()
			g2d.setColor(color);
			Coordinate barnCod = player.barnCod;
			int i = 0, j = 0;
			for (Piece p : player.pieceList) {
				if (p.getBoardPosition() == -1) {
					int x = barnCod.getX() + 2 + i;
					int y = barnCod.getY() + 2 + j;
					g2d.fillOval(x * cellSize, y * cellSize, cellSize, cellSize);
				}
				if (i == 1)
					j++;
				if (i < 1)
					i++;
				else
					i = 0;
			}
		}
	}

	public void rollDice() {
		hasRolledDice = false;
		Dice dice = model.getGame().getDice();
		Player player = model.getGame().getCurrentPlayer();
		view.btnRoll.setEnabled(false);
		new Thread(() -> {
			try {
				for (int i = 1; i < 15; i++) {
					dice.rollDice();
					view.setIconDice(dice.getResult());
					Thread.sleep(100);
				}
				// In kết quả vào text area
				SwingUtilities.invokeLater(() -> {
					if (historyCounter == 20) {
						view.jta.setText("");
						historyCounter = 0;
					}
					view.jta.append(model.getGame().getCurrentPlayer().getName() + ": " + "Đã đổ được "
							+ dice.getResult() + "\n");
					historyCounter++;
				});
				if (player.isAI()) {
					
					model.handleAITurn((AI) player);
				} else {
					hasRolledDice = true;
					view.btnSkipTurn.setEnabled(true);
					btnDeployEnabler(player, dice.getResult());
				}

			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	private void btnDeployEnabler(Player player, int result) {
		Game game = model.getGame();
		if (result != 1 && result != 6) {
			view.btnDeploy.setEnabled(false);
		} else {
			int count = 0;
			for (Piece p : player.getPieces()) {
				if (!game.canDevop(p, result) || p.getBoardPosition() != -1) {
					count++;
				}
			}
			if (count == 4)
				view.btnDeploy.setEnabled(false);
			else
				view.btnDeploy.setEnabled(true);
		}
	}

	public void skipTurn() {
		model.switchTurn();
		model.turn();
	}

	public void deploy() {
		model.deploy();
		model.turn();
	}

	public void pieceClick(Point click) {
		Game game = model.getGame();
		if (!game.getCurrentPlayer().isAI() && hasRolledDice) {
			for (Map.Entry<Integer, Rectangle> entry : view.positionToBounds.entrySet()) {
				if (entry.getValue().contains(click)) {
					int pos = entry.getKey();
					BoardCell cell = game.getBoard().getGridNormal().get(pos);
					Piece piece = cell.getPiece();
					if (piece != null && !piece.getOwner().isAI() && game.canMove(piece, game.getDice().getResult())) {
						model.move(piece);
						model.turn();
						return;
					}
				}
			}
			for (Player player : game.getPlayers()) {
				for (BoardCell goalCell : player.gridGoal) {
					Coordinate cod = goalCell.coordinate;
					Rectangle bound = new Rectangle(cod.getX() * view.cellSize, cod.getY() * view.cellSize,
							view.cellSize, view.cellSize);
					if (bound.contains(click)) {
						Piece piece = goalCell.getPiece();
						if (piece != null && !piece.getOwner().isAI()
								&& game.canMove(piece, game.getDice().getResult())) {
							model.move(piece);
							model.turn();
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public void updateStart() {
		Player p = model.getGame().getCurrentPlayer();
		view.btnColoredHorse.setBackground(p.getColor());
	}

	@Override
	public void updateSwitchTurn() {
		Player p = model.getGame().getCurrentPlayer();
		view.btnColoredHorse.setBackground(p.getColor());
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
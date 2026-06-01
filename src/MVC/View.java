package MVC;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GameSetup.Board;
import GameSetup.BoardCell;
import GameSetup.Game;
import GameSetup.Piece;
import Player.Player;

public class View extends JFrame implements Observer {
	private static final long serialVersionUID = -8724949034995447071L;
	JLabel labelDice, turnLabel;
	JPanel mainPanel, btnPanel, optionPanel;
	JButton btnRoll, btnSkipTurn, btnDeploy;
	JTextArea jta;
	Controller controller;
	Model model;

	Map<Integer, Rectangle> positionToBounds = new HashMap<>();
	int cellSize = 0;

	private Icon iconDice[];
	private Color redColor = new Color(255, 102, 102);
	private Color blueColor = new Color(102, 178, 255);
	private Color yellowColor = new Color(255, 230, 100);
	private Color greenColor = new Color(100, 220, 153);

	public View(Controller controller, Model model) {
		this.controller = controller;
		this.model = model;

		// Khởi tạo JTextArea trước
		jta = new JTextArea(30, 30);
		jta.setEditable(false);

		// Redirect System.out, System.err vào JTextArea
		TextAreaOutputStream taOutputStream = new TextAreaOutputStream(jta);
		PrintStream ps = new PrintStream(taOutputStream);
		System.setOut(ps);
		System.setErr(ps);

		model.addObserver(this);
		showStartScreen();
	}

	private void showStartScreen() {
		JPanel startPanel = new JPanel(new BorderLayout(10, 10));
		startPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// ======== PANEL 1: Chọn tổng số người chơi ========
		JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
		JLabel lblTotalPlayers = new JLabel("Tổng số người chơi (2–4):");
		JLabel lblHumanCount = new JLabel("Số người chơi thật (0–4):");

		Integer[] totalOptions = { 2, 3, 4 };
		JComboBox<Integer> totalPlayersCombo = new JComboBox<>(totalOptions);
		JComboBox<Integer> humanCountCombo = new JComboBox<>(new Integer[] { 0, 1, 2, 3, 4 });

		inputPanel.add(lblTotalPlayers);
		inputPanel.add(totalPlayersCombo);
		inputPanel.add(lblHumanCount);
		inputPanel.add(humanCountCombo);
		startPanel.add(inputPanel, BorderLayout.NORTH);

		// ======== PANEL 2: Chọn màu ========
		JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		colorPanel.setBorder(BorderFactory.createTitledBorder("Chọn màu cho người chơi thật"));
		String[] colors = { "Red", "Blue", "Green", "Yellow" };
		JCheckBox[] colorCheckboxes = new JCheckBox[colors.length];
		for (int i = 0; i < colors.length; i++) {
			colorCheckboxes[i] = new JCheckBox(colors[i]);
			colorPanel.add(colorCheckboxes[i]);
		}

		// ======== Gộp panel chọn màu và nhân vật ========
		JPanel middlePanel = new JPanel(new BorderLayout());
		middlePanel.add(colorPanel, BorderLayout.NORTH);
		startPanel.add(middlePanel, BorderLayout.CENTER);

		// ======== PANEL 4: Nút bắt đầu ========
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btnStart = new JButton("Bắt đầu chơi");
		buttonPanel.add(btnStart);
		startPanel.add(buttonPanel, BorderLayout.SOUTH);

		// ======== Sự kiện nhấn nút bắt đầu ========
		btnStart.addActionListener(e -> {
			int totalPlayers = (Integer) totalPlayersCombo.getSelectedItem();
			int humanCount = (Integer) humanCountCombo.getSelectedItem();

			if (humanCount > totalPlayers) {
				JOptionPane.showMessageDialog(this, "Số người chơi thật không được vượt quá tổng số người chơi!");
				return;
			}

			// Lấy màu người chơi
			List<String> humanColors = new ArrayList<>();
			for (JCheckBox checkbox : colorCheckboxes) {
				if (checkbox.isSelected())
					humanColors.add(checkbox.getText().toLowerCase());
			}
			// Nếu không chọn đủ màu → tự gán mặc định
			while (humanColors.size() < humanCount) {
				if (!humanColors.contains("red"))
					humanColors.add("red");
				else if (!humanColors.contains("blue"))
					humanColors.add("blue");
				else if (!humanColors.contains("green"))
					humanColors.add("green");
				else if (!humanColors.contains("yellow"))
					humanColors.add("yellow");
			}

			// ✅ Lưu vào Model
			model.setUpGame(humanColors, totalPlayers);

			showGameBoard();
		});

		// ======== Thiết lập cửa sổ ========
		setTitle("Cờ cá ngựa – Chọn người chơi");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 250);
		setLocationRelativeTo(null);
		setContentPane(startPanel);
		setVisible(true);
	}

	private void showGameBoard() {
		getContentPane().removeAll();
		setSize(1150, 800);
		setLayout(new BorderLayout());

		mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				drawLudoBoard(g2d);
				drawHighlights(g2d, cellSize); // 👉 Vẽ viền vàng ở ô đang đứng và ô đích
			}
		};
		mainPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point click = e.getPoint();

				// Nếu chuột phải → hủy chọn thẻ
				if (SwingUtilities.isRightMouseButton(e)) {
					controller.handleRightClick();
					return;
				} else {
					controller.pieceClick(click);
				}
			}
		});

		mainPanel.setBackground(Color.WHITE);
		add(mainPanel, BorderLayout.CENTER);

		// Tạo nút và panel chứa nút
		btnRoll = new JButton("Roll Dice");
		btnSkipTurn = new JButton("Skip turn");
		btnDeploy = new JButton("Deploy piece");
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());

		btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(3, 1));

		btnPanel.add(btnRoll);
		btnPanel.add(btnDeploy);
		btnPanel.add(btnSkipTurn);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(btnPanel, BorderLayout.SOUTH);
		prepareDice();
		labelDice = new JLabel(iconDice[0]); // hiển thị hình ảnh xúc xắc ban đầu

		turnLabel = new JLabel("Turn", SwingConstants.CENTER);
		turnLabel.setOpaque(true); // Cho phép nền có màu
		turnLabel.setForeground(Color.BLACK);
		turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
		turnLabel.setBackground(Color.LIGHT_GRAY); // mặc định ban đầu
		turnLabel.setPreferredSize(new Dimension(100, 40)); // ✅ chỉnh chiều rộng và cao

		topPanel.add(labelDice, BorderLayout.NORTH);
		topPanel.add(turnLabel, BorderLayout.CENTER);

		JPanel historyPanel = new JPanel(new BorderLayout());
		jta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		jta.setLineWrap(true); // Tự động xuống hàng
		jta.setWrapStyleWord(true); // Ngắt dòng theo từ
		jta.setEditable(false); // Không cho người dùng sửa
		historyPanel.add(new JScrollPane(jta), BorderLayout.CENTER);

		btnRoll.addActionListener(e -> controller.rollDice());
		btnSkipTurn.addActionListener(e -> controller.skipTurn());
		btnDeploy.addActionListener(e -> controller.deploy());

		rightPanel.add(historyPanel, BorderLayout.CENTER);
		rightPanel.add(topPanel, BorderLayout.NORTH);
		add(rightPanel, BorderLayout.EAST);

		controller.startGame();
		setLocationRelativeTo(null); // Căn giữa màn hình
	}

	public void showMessage(String message) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			javax.swing.JOptionPane.showMessageDialog(this, message);
		});
	}

	private void drawLudoBoard(Graphics2D g2d) {
		int width = getWidth();
		int height = getHeight() - 130;
		cellSize = Math.min(width, height) / 15;

		makeMap(positionToBounds, cellSize);

		drawBarn(g2d, redColor, cellSize, 0, 0, 6, 6);
		drawBarn(g2d, blueColor, cellSize, 0, 11, 6, 6);
		drawBarn(g2d, yellowColor, cellSize, 11, 11, 6, 6);
		drawBarn(g2d, greenColor, cellSize, 11, 0, 6, 6);

		drawPath(cellSize, g2d);
		drawPieceAtBarn(cellSize, g2d);

		drawAllGoal(g2d, cellSize);
		drawPiece(cellSize, g2d);
	}

	public void makeMap(Map<Integer, Rectangle> positionToBounds, int cellSize) {
		for (BoardCell cell : model.getGame().getBoard().getGridNormal()) {
			Coordinate coord = cell.coordinate;
			int x = coord.getX() * cellSize;
			int y = coord.getY() * cellSize;
			positionToBounds.put(cell.getPosition(), new Rectangle(x, y, cellSize, cellSize));
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

	public void drawHighlights(Graphics2D g2d, int cellSize) {
		Game game = this.model.getGame();
		int dice = game.getDice().getResult();
		Piece selectedPiece = this.controller.getSelectedPiece();
		if (selectedPiece != null && selectedPiece.getBoardPosition() >= 0 && game.canMove(selectedPiece)) {
			int newPos = this.wrapIndex(selectedPiece.getBoardPosition() + dice, 56);
			System.out.println(newPos);
			BoardCell destCell = game.getBoard().getGridNormal().get(newPos);
			if (destCell != null) {
				drawCellHighlight(g2d, destCell, cellSize, new Color(255, 230, 100));
			}
		}

		if (selectedPiece == null) {
			drawAllHighlights(g2d, cellSize);
		}
	}

	public void drawAllHighlights(Graphics2D g2d, int cellSize) {
		Game game = this.model.getGame();
		// highlight các quân
		for (Piece piece : game.getMovablePieces(game.getDice().getResult())) {
			if (piece.getBoardPosition() < 0)
				continue;
			if (!game.canMove(piece)) // 👉 BẮT BUỘC thêm dòng này
				continue;
			BoardCell destCell = game.getBoard().getGridNormal().get(piece.getBoardPosition());
			drawingPiece(g2d, destCell, piece.getOwner().getColor(), cellSize, true);
		}
	}

	private void drawCellHighlight(Graphics2D g2d, BoardCell cell, int cellSize, Color color) {
		int padding = cellSize / 6;
		int x = cell.coordinate.getX() * cellSize + padding;
		int y = cell.coordinate.getY() * cellSize + padding;
		int size = cellSize - 2 * padding;
		g2d.setColor(color);
		g2d.fillOval(x, y, size, size);
	}

	public void drawPiece(int cellSize, Graphics2D g2d) {
		Board board = this.model.getGame().getBoard();
		List<Player> players = this.model.getGame().getPlayers();
		Piece selectedPiece = this.controller.getSelectedPiece();
		for (Player player : players) {
			for (Piece p : player.pieceList) {
				if (p.getBoardPosition() == -1 || p.getBoardPosition() == -4)
					continue;
				BoardCell cell = p.getBoardPosition() <= -2 ? player.gridGoal.get(p.getGoalPosition())
						: board.getGridNormal().get(p.getBoardPosition());
				this.drawingPiece(g2d, cell, player.getColor(), cellSize, p.equals(selectedPiece));
			}
		}
	}

	public void drawPieceAtBarn(int cellSize, Graphics2D g2d) {
		for (Player player : model.getGame().getPlayers()) {
			Color color = player.getColor();
			Coordinate barn = player.barnCod;
			int i = 0, j = 0;
			for (Piece p : player.pieceList) {
				if (p.getBoardPosition() == -1) {
					int x = barn.getX() + 2 + i;
					int y = barn.getY() + 2 + j;
					g2d.setColor(color);
					g2d.fillOval(x * cellSize, y * cellSize, cellSize, cellSize);
				}
				if (i == 1)
					j++;
				i = (i < 1) ? i + 1 : 0;
			}
		}
	}

	private void drawingPiece(Graphics2D g2d, BoardCell cell, Color color, int cellSize, boolean isSelected) {
		int padding = cellSize / 6;
		int x = cell.coordinate.getX() * cellSize + padding;
		int y = cell.coordinate.getY() * cellSize + padding;
		int size = cellSize - 2 * padding;

		// Thân quân
		g2d.setColor(color);
		g2d.fillOval(x, y, size, size);

		// Viền trắng mặc định
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.white);
		g2d.drawOval(x, y, size, size);

		// Viền vàng nếu được chọn
		if (isSelected) {
//			g2d.setColor(new Color(255, 215, 0)); // gold
			g2d.setColor(new Color(220, 220, 220));
			g2d.drawOval(x, y, size, size);
		}
		g2d.setStroke(oldStroke);
	}

	public void setModel(Model model) {
		this.model = model;
	}

	void prepareDice() {
		final int numberSide = 7;
		int size = 70;

		iconDice = new ImageIcon[numberSide];
		for (int i = 0; i < numberSide; i++) {
			ImageIcon icon = new ImageIcon("img/D" + i + ".JPG");

			Image img = icon.getImage(); // lấy ảnh gốc
			Image newImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH); // resize

			iconDice[i] = new ImageIcon(newImg); // gán lại
		}
	}

	private void drawBarn(Graphics2D g2d, Color cl, int cellSize, int x, int y, int width, int height) {
		g2d.setColor(cl);
		g2d.fillRect(cellSize * x + 5, cellSize * y + 5, cellSize * width - 10, cellSize * height - 10);
		g2d.setColor(Color.white);
		g2d.fillRect(cellSize * x + 10, cellSize * y + 10, cellSize * width - 20, cellSize * height - 20);
		g2d.setColor(cl);
		g2d.fillOval(cellSize * x + 30, cellSize * y + 30, cellSize * width - 60, cellSize * height - 60);
	}

	private void drawAllGoal(Graphics2D g2d, int cellSize) {
		drawGoal(g2d, Color.red, cellSize, 8, 1, 0, 1);
		drawGoal(g2d, Color.blue, cellSize, 1, 8, 1, 0);
		drawGoal(g2d, Color.yellow, cellSize, 8, 15, 0, -1);
		drawGoal(g2d, Color.green, cellSize, 15, 8, -1, 0);
	}

	/**
	 * 
	 * @param g2d
	 * @param cl
	 * @param cellSize
	 * @param x
	 * @param y
	 * @param dirX:    1 la di qua phai, -1 la di qua trai, 0 la khong di
	 * @param dirY:    1 la di xuong, -1 la di len, 0 la khong di
	 */
	private void drawGoal(Graphics2D g2d, Color cl, int cellSize, int x, int y, int dirX, int dirY) {
		int cellSizeX = cellSize + 60 * Math.abs(dirY);
		int cellSizeY = cellSize + 60 * Math.abs(dirX);
		int center = 7;
		Font font = new Font("Arial", Font.BOLD, cellSize / 2);
		g2d.setFont(font);
		for (int i = 0; i < 6; i++) {
			// Lưu stroke hiện tại
			Stroke oldStroke = g2d.getStroke();

			// Thiết lập stroke mới (nét dày)
			g2d.setStroke(new BasicStroke(3.0f));

			// vẽ ô nhà
			int startX = x * cellSize - 30 * Math.abs(dirY);
			int startY = y * cellSize - 30 * Math.abs(dirX);
			g2d.setColor(cl);
			g2d.fillRect(startX, startY, cellSizeX, cellSizeY);
			g2d.setColor(Color.white);
			g2d.drawRect(startX, startY, cellSizeX, cellSizeY);
			x += dirX;
			y += dirY;
			// Khôi phục stroke ban đầu
			g2d.setStroke(oldStroke);
		}
		// vẽ số đỏ, xanh dương
		for (int i = 1, num = 1; i < 7; i++) {
			if (i == center)
				continue;
			int a = i * cellSize;
			int b = center * cellSize;
			drawCenteredNumber(g2d, String.valueOf(num), a, b, cellSize + 7, cellSize * 3 - 5, 90);
			drawCenteredNumber(g2d, String.valueOf(num++), b + 22, a - 2, cellSize * 2, cellSize + 10, 180);
		}

		// vẽ số xanh lá, vàng
		for (int i = 9, num = 0; i <= 14; i++) {
			if (i == center)
				continue;
			int a = i * cellSize;
			int b = center * cellSize;
			drawCenteredNumber(g2d, String.valueOf(6 - num), a, b, cellSize * 3 - 5, cellSize * 3 - 3, -90);
			drawCenteredNumber(g2d, String.valueOf(6 - num++), b, a, cellSize * 3 - 3, cellSize * 3 - 5, 0);
		}
	}

	private void drawCenteredNumber(Graphics2D g2d, String text, int x, int y, int width, int height,
			double angleDegrees) {
		// Tạo bản sao Graphics2D để không ảnh hưởng gốc
		Graphics2D g2dCopy = (Graphics2D) g2d.create();

		// Đặt font lớn hơn
		Font originalFont = g2d.getFont();
		Font newFont = originalFont.deriveFont(Font.BOLD, 35f);
		g2dCopy.setFont(newFont);

		// Đặt màu chữ trắng
		g2dCopy.setColor(Color.WHITE);

		// Lấy kích thước chữ mới
		FontMetrics fm = g2dCopy.getFontMetrics();
		int textWidth = fm.stringWidth(text);
		int textHeight = fm.getAscent();

		// Tính tâm của ô
		int centerX = x + width / 2;
		int centerY = y + height / 2;

		// Di chuyển và xoay
		g2dCopy.translate(centerX, centerY);
		g2dCopy.rotate(Math.toRadians(angleDegrees));

		// Vẽ chữ căn giữa
		g2dCopy.drawString(text, -textWidth / 2, textHeight / 2);

		// Giải phóng
		g2dCopy.dispose();
	}

	public void setIconDice(int result) {
		labelDice.setIcon(iconDice[result]);
	}

	private int wrapIndex(int index, int size) {
		int mod = index % size;
		return (mod < 0) ? mod + size : mod;
	}

	@Override
	public void updateStart() {
		jta.setText("Game start\n");
	}

	@Override
	public void updateSwitchTurn() {

	}

	@Override
	public void updateItsAI() {
		btnDeploy.setEnabled(false);
		btnRoll.setEnabled(false);
		btnSkipTurn.setEnabled(false);
	}

	@Override
	public void updateItsHuman() {
		btnRoll.setEnabled(true);
		btnDeploy.setEnabled(false);
		btnSkipTurn.setEnabled(false);
	}

	@Override
	public void updateMove() {
		mainPanel.repaint();
	}

	@Override
	public void updateWin() {
		JOptionPane.showMessageDialog(null, "Game đã kết thúc!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
		String result = model.getGame().getFinalResultString();
		JOptionPane.showMessageDialog(null, result, "Bảng xếp hạng", JOptionPane.INFORMATION_MESSAGE);
	}
}

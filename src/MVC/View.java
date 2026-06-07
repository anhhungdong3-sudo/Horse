package MVC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import GameSetup.Player;

public class View extends JFrame implements Observer {
	private static final long serialVersionUID = -8724949034995447071L;
	JLabel labelDice, turnLabel;
	JPanel mainPanel, btnPanel, optionPanel, resetPanel;
	JButton btnRoll, btnSkipTurn, btnDeploy, btnPause, btnReset;
	JDialog pauseDialog;
	JTextArea jta;
	Controller controller;
	Model model;

	Map<Integer, Rectangle> positionToBounds = new HashMap<>();
	int cellSize = 0;

	private Icon iconDice[];

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
		JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		JLabel lblTotalPlayers = new JLabel("Tổng số người chơi (2–4):");
		JLabel lblHumanCount = new JLabel("Số người chơi thật (0–4):");
		JLabel lblDifficulty = new JLabel("Chọn độ khó:");

		Integer[] totalOptions = { 2, 3, 4 };
		JComboBox<Integer> totalPlayersCombo = new JComboBox<>(totalOptions);
		JComboBox<Integer> humanCountCombo = new JComboBox<>(new Integer[] { 0, 1, 2, 3, 4 });
		JComboBox<String> Difficulty = new JComboBox<>(new String[] { "Easy", "Normal", "Hard" });

		inputPanel.add(lblTotalPlayers);
		inputPanel.add(totalPlayersCombo);
		inputPanel.add(lblHumanCount);
		inputPanel.add(humanCountCombo);
		inputPanel.add(lblDifficulty);
		inputPanel.add(Difficulty);
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
			String DifficultyChose = (String) Difficulty.getSelectedItem();

			if (humanCount > totalPlayers) {
				JOptionPane.showMessageDialog(this, "Số người chơi thật không được vượt quá tổng số người chơi!");
				return;
			}

			/* ---- Lấy màu Human ---- */
			ArrayList<String> humanColors = new ArrayList<>();
			for (JCheckBox checkbox : colorCheckboxes) {
				if (checkbox.isSelected()) {
					humanColors.add(checkbox.getText().toLowerCase());
				}
			}

			// Không được chọn quá số người chơi
			if (humanColors.size() > totalPlayers) {
				JOptionPane.showMessageDialog(this, "Không được chọn quá số người chơi!");
				return;
			}

			// Random fill màu còn thiếu
			if (humanColors.size() < humanCount) {

				List<String> allColors = new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow"));
				allColors.removeAll(humanColors);

				Random random = new Random();

				while (humanColors.size() < humanCount && !allColors.isEmpty()) {
					int index = random.nextInt(allColors.size());
					String color = allColors.remove(index);
					humanColors.add(color);
				}
			}

			// ✅ Lưu vào Model
			model.setUpGame(humanColors, humanCount, totalPlayers, DifficultyChose);

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
		setSize(1175, 880);
		setLayout(new BorderLayout());

		mainPanel = new JPanel() {
			private Image boardImage = new ImageIcon("img/Board.png").getImage();

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				int width = getWidth();
				int height = getHeight() - 130;
				cellSize = Math.min(width, height) / 15;

				int plus = 13;
				// Vẽ ảnh bàn cờ
				g2d.drawImage(boardImage, -5, -0, (cellSize * 17) + plus, (cellSize * 17) + plus, null);

				drawPieceAtBarn(cellSize, g2d);
				drawPiece(cellSize, g2d);
				drawHighlights(g2d, cellSize);
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
		btnRoll = new JButton("Roll dice");
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

		// lần 2 thêm nút dùng và reset
		this.btnPause = new JButton("Pause game");
		this.btnPause.addActionListener(e -> pauseGame());

		this.btnReset = new JButton("Reset game");
		this.btnReset.addActionListener(e -> updateWin());

		this.resetPanel = new JPanel(new GridLayout(2, 1));
		this.resetPanel.add(this.btnPause);
		this.resetPanel.add(this.btnReset);

		historyPanel.add(this.resetPanel, BorderLayout.SOUTH);

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

	public void makeMap(Map<Integer, Rectangle> positionToBounds, int cellSize) {
		for (BoardCell cell : model.getGame().getBoard().getGridNormal()) {
			Coordinate coord = cell.coordinate;
			int x = coord.getX() * cellSize;
			int y = coord.getY() * cellSize;
			positionToBounds.put(cell.getPosition(), new Rectangle(x, y, cellSize, cellSize));
		}
	}

	public void drawHighlights(Graphics2D g2d, int cellSize) {
		Game game = this.model.getGame();
		int dice = game.getDice().getResult();
		Piece selectedPiece = this.controller.getSelectedPiece();
		if (selectedPiece != null && selectedPiece.getBoardPosition() >= 0 && game.canMove(selectedPiece)) {
			int newPos = this.wrapIndex(selectedPiece.getBoardPosition() + dice, 56);
			BoardCell destCell = game.getBoard().getGridNormal().get(newPos);
			if (destCell != null) {
				drawCellHighlight(g2d, destCell, cellSize, new Color(255, 230, 100, 150));
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
			drawingPiece(g2d, destCell, piece.getOwner(), cellSize, true);
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
				this.drawingPiece(g2d, cell, player, cellSize, p.equals(selectedPiece));
			}
		}
	}

	public void drawPieceAtBarn(int cellSize, Graphics2D g2d) {
		for (Player player : model.getGame().getPlayers()) {
			Coordinate barn = player.barnCod;
			int i = 0, j = 0;
			for (Piece p : player.pieceList) {
				if (p.getBoardPosition() == -1) {
					int x = barn.getX() + 2 + i;
					int y = barn.getY() + 2 + j;
					g2d.drawImage(player.getPieceImage(), x * cellSize, y * cellSize, cellSize, cellSize, null);
				}
				if (i == 1)
					j++;
				i = (i < 1) ? i + 1 : 0;
			}
		}
	}

	private void drawingPiece(Graphics2D g2d, BoardCell cell, Player player, int cellSize, boolean isSelected) {
		int padding = cellSize / 6;
		int x = cell.coordinate.getX() * cellSize + padding;
		int y = cell.coordinate.getY() * cellSize + padding;
		int size = cellSize - 2 * padding;
		g2d.drawImage(player.getPieceImage(), x, y, size, size, null);
	}

	public void setModel(Model model) {
		this.model = model;
	}

	private Map<String, ImageIcon> pieceIcons = new HashMap<>();

	void preparePieceImages() {
		int size = 50;

		String[] colors = { "Red", "Blue", "Green", "Yellow" };

		for (String color : colors) {
			ImageIcon icon = new ImageIcon("img/" + color + "Piece.png");

			Image img = icon.getImage();
			Image newImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);

			pieceIcons.put(color, new ImageIcon(newImg));
		}
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

	public void setIconDice(int result) {
		labelDice.setIcon(iconDice[result]);
	}

	// lần 2 thêm nút dùng và reset game
	public void pauseGame() {
		if (controller.isPause()) {
			return;
		}

		controller.setPause(true);

		pauseDialog = new JDialog(this, "Paused", false);
		pauseDialog.setLayout(new BorderLayout(10, 10));
		pauseDialog.setSize(300, 140);
		pauseDialog.setLocationRelativeTo(this);

		JLabel label = new JLabel("Game đang tạm dừng", JLabel.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 15));

		JButton btnResume = new JButton("Tiếp tục");
		btnResume.addActionListener(e -> resumeGame());

		JPanel btnPanel = new JPanel();
		btnPanel.add(btnResume);

		pauseDialog.add(label, BorderLayout.CENTER);
		pauseDialog.add(btnPanel, BorderLayout.SOUTH);

		// ⭐ QUAN TRỌNG: xử lý khi bấm nút X
		pauseDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pauseDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				resumeGame();
			}
		});

		pauseDialog.setVisible(true);
	}

	public void resumeGame() {
		controller.setPause(false);

		if (pauseDialog != null) {
			pauseDialog.dispose();
			controller.resumeGame();
		}
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
		controller.setPause(true);
		JOptionPane.showMessageDialog(null, "Game đã kết thúc!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
		String result = model.getGame().getFinalResultString();
		JOptionPane.showMessageDialog(null, result, "Bảng xếp hạng", JOptionPane.INFORMATION_MESSAGE);

		// lần 2 thêm reset game
		// ✔ reset trạng thái pause về lại bình thường
		controller.setPause(false);

		// Reset game trước
		this.controller.resetGame();

		// Sau đó mới mở màn hình start
		SwingUtilities.invokeLater(() -> this.showStartScreen());
	}
}

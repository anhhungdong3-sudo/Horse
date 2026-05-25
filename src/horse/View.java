package horse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class View extends JFrame implements Observer {
	JLabel labelDice;
	JPanel mainPanel;
	JButton btnRoll, btnSkipTurn, btnDeploy, btnColoredHorse;
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

		model.addObserver(this);
		showStartScreen();
	}

	private void showStartScreen() {
		JPanel startPanel = new JPanel(new GridLayout(4, 2));
		startPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JLabel lblPlayers = new JLabel("Chọn số người chơi:");
		Integer[] playerOptions = { 2, 3, 4 };
		JComboBox<Integer> playerCountCombo = new JComboBox<>(playerOptions);

		JLabel lblColor = new JLabel("Chọn màu của bạn:");
		String[] colors = { "Red", "Blue", "Green", "Yellow" };
		JComboBox<String> colorCombo = new JComboBox<>(colors);

		JButton btnStart = new JButton("Bắt đầu chơi");

		btnStart.addActionListener(e -> {
			int playerCount = (Integer) playerCountCombo.getSelectedItem();
			String color = (String) colorCombo.getSelectedItem();
			controller.setUpGame(playerCount, color);
			showGameBoard();
		});

		startPanel.add(lblPlayers);
		startPanel.add(playerCountCombo);
		startPanel.add(lblColor);
		startPanel.add(colorCombo);
		startPanel.add(new JLabel());
		startPanel.add(btnStart);

		setTitle("Cờ cá ngựa");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(null);
		setContentPane(startPanel);
		setVisible(true);
	}

	private void showGameBoard() {
		getContentPane().removeAll();
		setSize(1015, 800);
		setLayout(new BorderLayout());

		mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				drawLudoBoard(g2d);
			}
		};
		mainPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point click = e.getPoint();
				controller.pieceClick(click);
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

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(3, 1));
		btnPanel.add(btnRoll);
		btnPanel.add(btnDeploy);
		btnPanel.add(btnSkipTurn);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(btnPanel, BorderLayout.SOUTH);
		prepareDice();
		labelDice = new JLabel(iconDice[0]); // hiển thị hình ảnh xúc xắc ban đầu
		btnColoredHorse = new JButton("Turn");
		btnColoredHorse.setEnabled(false);
		btnColoredHorse.setForeground(Color.white);
		topPanel.add(labelDice, BorderLayout.NORTH);
		topPanel.add(btnColoredHorse, BorderLayout.CENTER);

		JPanel historyPanel = new JPanel();
		jta = new JTextArea(25, 20);
		jta.setEditable(false);
		historyPanel.add(new JScrollPane(jta));
		historyPanel.add(jta);

		btnRoll.addActionListener(e -> controller.rollDice());
		btnSkipTurn.addActionListener(e -> controller.skipTurn());
		btnDeploy.addActionListener(e -> controller.deploy());

		rightPanel.add(historyPanel, BorderLayout.CENTER);
		rightPanel.add(topPanel, BorderLayout.NORTH);
		add(rightPanel, BorderLayout.EAST);

		controller.startGame();
		setLocationRelativeTo(null); // Căn giữa màn hình
	}

	private void drawLudoBoard(Graphics2D g2d) {
		int width = getWidth();
		int height = getHeight() - 130;
		cellSize = Math.min(width, height) / 15;

		controller.makeMap(positionToBounds, cellSize);

		drawBarn(g2d, redColor, cellSize, 0, 0, 6, 6);
		drawBarn(g2d, blueColor, cellSize, 0, 11, 6, 6);
		drawBarn(g2d, yellowColor, cellSize, 11, 11, 6, 6);
		drawBarn(g2d, greenColor, cellSize, 11, 0, 6, 6);

		controller.drawPath(cellSize, g2d);
		controller.drawPieceAtBarn(cellSize, g2d);

		drawAllGoal(g2d, cellSize);
		controller.drawPiece(cellSize, g2d);
	}

	public void setModel(Model model) {
		this.model = model;
	}

	void prepareDice() {
		final int numberSide = 7;
		iconDice = new ImageIcon[numberSide];
		for (int i = 0; i < numberSide; i++) {
			iconDice[i] = new ImageIcon("img/D" + i + ".JPG");

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
		for (int i = 0; i < 6; i++) {
			int startX = x * cellSize - 30 * Math.abs(dirY);
			int startY = y * cellSize - 30 * Math.abs(dirX);
			g2d.setColor(cl);
			g2d.fillRect(startX, startY, cellSizeX, cellSizeY);
			g2d.setColor(Color.gray);
			g2d.drawRect(startX, startY, cellSizeX, cellSizeY);
			g2d.setColor(Color.white);
			g2d.fillOval(x * cellSize, y * cellSize, cellSize, cellSize);
			x += dirX;
			y += dirY;
		}
	}

	public void setIconDice(int result) {
		labelDice.setIcon(iconDice[result]);
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
		btnDeploy.setEnabled(false);
		btnRoll.setEnabled(true);
		btnSkipTurn.setEnabled(false);
	}

	@Override
	public void updateMove() {
		mainPanel.repaint();
	}

	@Override
	public void updateWin() {
		JOptionPane.showMessageDialog(null, "Game over", "Game over", JOptionPane.INFORMATION_MESSAGE);
	}

}

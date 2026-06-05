package MVC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// lần 2: thêm nút dừng và reset trận đấu.
public class View extends JFrame implements Observer {
	private static final long serialVersionUID = -8724949034995447071L;
	JLabel labelDice, turnLabel;
	JPanel mainPanel, btnPanel, optionPanel;
	JButton btnRoll, btnSkipTurn, btnDeploy;
	// thêm nút dừng và reset
	JButton btnPause, btnReset;
	JDialog pauseDialog;
	JTextArea jta;
	Controller controller;
	Model model;
	Map<Integer, Rectangle> positionToBounds = new HashMap<>();
	int cellSize = 0;

	public View(Controller controller, Model model) {
		this.controller = controller;
		this.model = model;

		model.addObserver(this);
	}
	private void showGameBoard() {
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
	}
	public void setModel(Model model) {
		this.model = model;
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

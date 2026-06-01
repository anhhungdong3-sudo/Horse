package GameSetup;

import Player.Player;

public class Piece {
	private int id;
	private String color;
	private Player owner;
	private int boardPosition;
	private int goalPosition;
	private int stepsMoved;

	public Piece(int id, String color, Player owner) {
		this.id = id;
		this.color = color;
		this.owner = owner;
		this.boardPosition = -1; // -1 nghĩa là chưa ra sân
		this.goalPosition = -1; // chưa lên đích
		this.stepsMoved = 0;
	}

	// ===== Getter & Setter =====
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getBoardPosition() {
		return boardPosition;
	}

	public void setBoardPosition(int boardPosition) {
		this.boardPosition = boardPosition;
	}

	public int getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(int goalPosition) {
		this.goalPosition = goalPosition;
	}

	public int getStepsMoved() {
		return stepsMoved;
	}

	public void setStepsMoved(int stepsMoved) {
		this.stepsMoved = stepsMoved;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Piece{id=" + id + ", color='" + color + "}";
	}
}

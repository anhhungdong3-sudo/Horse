package Test;

import java.util.ArrayList;
import java.util.List;

public class PlayerSnapshot {
	public String name;
	public String color;
	public List<PieceSnapshot> pieces = new ArrayList<>();

	public PlayerSnapshot(String name, String color) {
		this.name = name;
		this.color = color;
	}
}

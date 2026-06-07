package Test;

import java.util.ArrayList;
import java.util.List;

public class GameSnapshot {
	public int gameIndex;
	public int turn;
	public String currentPlayer;
	public List<PlayerSnapshot> players = new ArrayList<>();
}

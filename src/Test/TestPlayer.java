package Test;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.ImageIcon;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import P_Human.HumanRed;
import GameSetup.*;

class TestPlayer {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testConstructor() {
		Player player = new HumanRed("Player1", new ImageIcon("img/pieceRed.png").getImage());

		assertNotNull(player.getPieceImage());
	}

	@Test
	void testRecordDiceRoll() {
		Player player = new HumanRed("Player1", new ImageIcon("img/pieceRed.png").getImage());
		
		// invalid roll
	    player.recordDiceRoll(0);
	    player.recordDiceRoll(7);
	    assertEquals(0, player.totalDiceRolls);

		// 1 roll
		player.recordDiceRoll(6);
		assertEquals(1, player.totalDiceRolls);
		assertEquals(1, player.diceFrequency[5]);
		
		// multiple rolls
	    player.recordDiceRoll(1);
	    player.recordDiceRoll(1);
	    player.recordDiceRoll(3);
	    assertEquals(4, player.totalDiceRolls);
	    assertEquals(2, player.diceFrequency[0]);
	    assertEquals(1, player.diceFrequency[2]);
	}
}

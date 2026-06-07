package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import MVC.Controller;
import MVC.Model;
import GameSetup.Game;
import java.util.ArrayList;

public class Test_UC2 {

    private Model model;
    private Controller controller;

    @BeforeEach
    public void setUp() {
        model = new Model();
        controller = new Controller(model);
        
        ArrayList<String> colors = new ArrayList<>();
        colors.add("RED");
        colors.add("BLUE");
        model.setUpGame(colors, 0, 2, "Normal");
    }

    @Test
    public void testPauseToggle() {
        assertFalse(controller.isPause());

        controller.setPause(true);
        assertTrue(controller.isPause());
    }

    @Test
    public void testResetGameClearsData() {
        Game game = model.getGame();
        
        assertFalse(game.getPlayers().isEmpty());

        controller.resetGame();

        assertNull(game.getCurrentPlayer());
        assertEquals(1, game.getRoundCount());
        assertTrue(game.getPlayers().isEmpty());
    }
}
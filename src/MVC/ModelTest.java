package MVC;

import GameSetup.Game;
import GameSetup.Piece;
import GameSetup.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



class ModelTest {

    private Model model;
    private TestObserver testObserver;   // Observer giả để kiểm tra notify

    @BeforeEach
    void setUp() {
        model = new Model();
        testObserver = new TestObserver();
        model.addObserver(testObserver);
        
        // Setup game đơn giản với 2 người chơi
        model.setUpGame(List.of("red", "blue"), 2, 2, "Easy");
        model.start();
    }

    // ==================== DEVELOPMENT TESTING (Unit Test) ====================

    @Test
    void testGameMove_NotifyUpdate() {
        System.out.println("=== TEST 1.1 - 1.2 - 1.3: game.move() -> notifyMove() -> updateMove() ===");

        Player currentPlayer = model.getGame().getCurrentPlayer();
        Piece piece = currentPlayer.pieceList.get(0);

        // Đảm bảo quân có thể di chuyển
        piece.setBoardPosition(0);

        // Thực hiện move (1.1)
        model.move(piece);

        // Kiểm tra 1.2 & 1.3
        assertTrue(testObserver.updateMoveCalled, "updateMove() phải được gọi sau khi move");
        assertEquals(1, testObserver.moveCount, "Phải gọi notifyMove đúng 1 lần");

        System.out.println("✅ TEST PASSED: Luồng move + animation hoạt động đúng");
    }

    @Test
    void testDeploy_NotifyUpdate() {
        System.out.println("=== TEST Deploy -> notifyMove ===");

        model.deploy();

        assertTrue(testObserver.updateMoveCalled, "Deploy phải trigger updateMove()");
        assertEquals(1, testObserver.moveCount);

        System.out.println(" TEST PASSED: Deploy hoạt động đúng");
    }

    @Test
    void testMultipleMoves() {
        System.out.println("=== TEST Multiple Moves ===");

        Player player = model.getGame().getCurrentPlayer();
        Piece piece = player.pieceList.get(0);
        piece.setBoardPosition(5);

        for (int i = 0; i < 3; i++) {
            model.move(piece);
            assertTrue(testObserver.updateMoveCalled);
            testObserver.reset(); // reset cho lần test tiếp theo
        }

        System.out.println("✅ TEST PASSED: Nhiều lần move đều trigger repaint đúng");
    }

    @Test
    void manualReleaseTest() {
        System.out.println("\n=== RELEASE TESTING - Kiểm tra bằng tay ===");
        System.out.println("1. Chạy chương trình chính");
        System.out.println("2. Chọn 2 người chơi, bắt đầu game");
        System.out.println("3. Roll dice → Click quân → Kiểm tra animation có mượt không");
        System.out.println("4. Kiểm tra quân di chuyển + highlight + repaint");
        System.out.println("5. Kiểm tra AI turn có update board không");
    }

    // Lớp Observer giả để test
    private static class TestObserver implements Observer {
        boolean updateMoveCalled = false;
        int moveCount = 0;

        @Override
        public void updateStart() {}
        @Override
        public void updateSwitchTurn() {}
        @Override
        public void updateItsAI() {}
        @Override
        public void updateItsHuman() {}
        
        @Override
        public void updateMove() {
            updateMoveCalled = true;
            moveCount++;
            System.out.println("   → updateMove() được gọi lần " + moveCount);
        }
        
        @Override
        public void updateWin() {}

        public void reset() {
            updateMoveCalled = false;
        }
    }
}
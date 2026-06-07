package GameSetup;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MakeMoveTest {

    private Game game;
    private Board board;
    private Player redPlayer;
    private Player bluePlayer;
    private Piece redPiece;
    private Piece bluePiece;

    // Các hằng số trạng thái (đảm bảo khớp với định nghĩa trong model của bạn)
    private static final int STATE_IN_BARN = -1;
    private static final int STATE_IN_GOAL = -2;
    private static final int STEPS_TO_GOAL_ENTRY = 56; // Giả định số bước đi hết 1 vòng

    @BeforeEach
    void setUp() {
        // 1. Khởi tạo danh sách người chơi dựa trên các class Human đã định nghĩa
        List<Player> players = new ArrayList<>();
        redPlayer = new HumanRed("Player Red", "RED");
        bluePlayer = new HumanBlue("Player Blue", "BLUE");
        players.add(redPlayer);
        players.add(bluePlayer);

        // 2. Khởi tạo Game và lấy Board
        game = new Game(players);
        board = game.getBoard();

        // 3. Lấy quân cờ đầu tiên của mỗi bên để test
        redPiece = redPlayer.getPieces().get(0);
        bluePiece = bluePlayer.getPieces().get(0);
        
        // Setup lượt chơi luôn là của Red để dễ test
        game.setCurrentPlayer(redPlayer);
    }

    /* =========================================================
     * TEST CASE 1: CHIẾN LƯỢC RA QUÂN (DEPLOY HORSE)
     * ========================================================= */

    @Test
    @DisplayName("TC 01: Ra quân thành công vào ô xuất phát trống")
    void testDeployToEmptySpawn() {
        // Cố định xúc xắc = 6 để đủ điều kiện ra quân
        game.getDice().setResult(6);
        MoveStrategy deploy = new DeployHorse();

        // Thực hiện ra quân
        game.move(deploy, redPiece);

        // Kiểm tra vị trí: Quân đỏ phải nằm ở vị trí xuất phát của màu Đỏ
        int redSpawn = board.getStartPosition("RED");
        assertEquals(redSpawn, redPiece.getBoardPosition(), "Quân cờ phải ở tọa độ spawn.");
        assertEquals(redPiece, board.getGridNormal().get(redSpawn).getPiece(), "Ô spawn trên Board phải chứa quân cờ này.");
    }

    @Test
    @DisplayName("TC 02: Ra quân và đá bay quân địch tại ô xuất phát")
    void testDeployAndKickEnemy() {
        int redSpawn = board.getStartPosition("RED");

        // Giả lập quân Xanh đang cản đường ngay cửa nhà quân Đỏ
        bluePiece.setBoardPosition(redSpawn);
        bluePiece.setStepsMoved(10);
        board.addPiece(bluePiece);

        // Đổ xúc xắc = 1 (đủ điều kiện ra quân)
        game.getDice().setResult(1);
        MoveStrategy deploy = new DeployHorse();

        // Thực hiện nước đi ra quân của quân Đỏ
        game.move(deploy, redPiece);

        // Kiểm tra quân Đỏ đã chiếm được ô xuất phát
        assertEquals(redSpawn, redPiece.getBoardPosition());
        assertEquals(redPiece, board.getGridNormal().get(redSpawn).getPiece());

        // Kiểm tra quân Xanh đã bị đá về chuồng (-1) và reset số bước (0)
        assertEquals(STATE_IN_BARN, bluePiece.getBoardPosition(), "Quân địch phải bị đá về chuồng.");
        assertEquals(0, bluePiece.getStepsMoved(), "Số bước của quân địch phải bị reset về 0.");
    }

    /* =========================================================
     * TEST CASE 2: CHIẾN LƯỢC ĐI BÌNH THƯỜNG (NORMAL MOVE)
     * ========================================================= */

    @Test
    @DisplayName("TC 03: Đi bình thường trên bàn cờ (không bị chặn)")
    void testNormalMoveSuccess() {
        int startPos = board.getStartPosition("RED");
        
        // Đưa quân đỏ ra sân trước
        redPiece.setBoardPosition(startPos);
        redPiece.setStepsMoved(0);
        board.addPiece(redPiece);

        // Tung xúc xắc = 4
        game.getDice().setResult(4);
        MoveStrategy normalMove = new NormalMove();

        // Thực hiện đi
        game.move(normalMove, redPiece);

        // Tọa độ mới phải là startPos + 4
        int expectedPos = startPos + 4;
        assertEquals(expectedPos, redPiece.getBoardPosition());
        assertEquals(4, redPiece.getStepsMoved());
        
        // Ô cũ phải null, ô mới phải chứa quân đỏ
        assertNull(board.getGridNormal().get(startPos).getPiece());
        assertEquals(redPiece, board.getGridNormal().get(expectedPos).getPiece());
    }

    @Test
    @DisplayName("TC 04: Đi bình thường kết hợp đá quân địch dọc đường")
    void testNormalMoveAndKick() {
        int startPos = board.getStartPosition("RED");
        int targetPos = startPos + 3; // Nơi quân địch đứng

        // Set up quân Đỏ
        redPiece.setBoardPosition(startPos);
        redPiece.setStepsMoved(5);
        board.addPiece(redPiece);

        // Set up quân Xanh (địch)
        bluePiece.setBoardPosition(targetPos);
        bluePiece.setStepsMoved(15);
        board.addPiece(bluePiece);

        // Xúc xắc = 3 (Vừa đúng ô quân Xanh)
        game.getDice().setResult(3);
        MoveStrategy normalMove = new NormalMove();
        game.move(normalMove, redPiece);

        // Đỏ chiếm ô của Xanh
        assertEquals(targetPos, redPiece.getBoardPosition());
        assertEquals(redPiece, board.getGridNormal().get(targetPos).getPiece());

        // Xanh bị đá về chuồng
        assertEquals(STATE_IN_BARN, bluePiece.getBoardPosition());
        assertEquals(0, bluePiece.getStepsMoved());
    }

    @Test
    @DisplayName("TC 05: Đi bình thường nhưng bị chặn bởi quân đồng minh")
    void testNormalMoveBlockedByAlly() {
        int startPos = board.getStartPosition("RED");
        
        // Quân Đỏ 1 ở vị trí xuất phát
        redPiece.setBoardPosition(startPos);
        redPiece.setStepsMoved(5);
        board.addPiece(redPiece);

        // Quân Đỏ 2 cản đường ở khoảng cách 2 ô
        Piece redAlly = redPlayer.getPieces().get(1);
        redAlly.setBoardPosition(startPos + 2);
        redAlly.setStepsMoved(7);
        board.addPiece(redAlly);

        // Tung xúc xắc = 4 (bước qua ô quân đồng minh hoặc đáp ngay ô đồng minh)
        game.getDice().setResult(4);
        MoveStrategy normalMove = new NormalMove();
        
        // Lấy vị trí trước khi đi
        int posBeforeMove = redPiece.getBoardPosition();
        
        // Hành động
        game.move(normalMove, redPiece);

        // Kỳ vọng: Di chuyển thất bại, vị trí vẫn giữ nguyên do bị chặn
        assertEquals(posBeforeMove, redPiece.getBoardPosition(), "Quân cờ không được đi nếu bị cản bởi đồng minh.");
    }

    /* =========================================================
     * TEST CASE 3: LÊN CHUỒNG ĐÍCH (GOAL AREA)
     * ========================================================= */

    @Test
    @DisplayName("TC 06: Quân cờ đi hết 1 vòng và bước vào chuồng đích")
    void testEnterGoalArea() {
        // Đặt quân Đỏ ở ô sát cửa vào chuồng đích (ví dụ đã đi được 55 bước)
        int stepsBeforeGoal = STEPS_TO_GOAL_ENTRY - 1;
        redPiece.setStepsMoved(stepsBeforeGoal);
        
        // Vị trí cửa chuồng thường là trước vị trí xuất phát 1 ô
        int doorPos = (board.getStartPosition("RED") - 1 + 56) % 56;
        redPiece.setBoardPosition(doorPos);
        board.addPiece(redPiece);

        // Xúc xắc = 1 (Bước vào ô số 1 của Goal)
        game.getDice().setResult(1);
        MoveStrategy normalMove = new NormalMove();
        game.move(normalMove, redPiece);

        // Kỳ vọng: boardPosition chuyển thành STATE_IN_GOAL (-2)
        assertEquals(STATE_IN_GOAL, redPiece.getBoardPosition(), "Quân cờ phải chuyển trạng thái vào chuồng (-2).");
        assertEquals(0, redPiece.getGoalPosition(), "Quân cờ phải đứng ở bậc số 0 (hoặc 1 tùy mảng) của chuồng.");
    }
}

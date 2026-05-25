package horse;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
//		AI red = new AIRed("Red");
//		AI blue = new AIBlue("Blue");
//		AI green = new AIGreen("Green");
//		AI yellow = new AIYellow("Yellow");
//
//		List<Player> ls = new ArrayList<>();
//		ls.add(red);
//		ls.add(blue);
//		ls.add(green);
//		ls.add(yellow);
//
//		// Khởi tạo Game
//		Game game = new Game(ls);
//
//		// Khởi tạo Model, View, Controller
//		Model model = new Model(game);
//		Controller controller = new Controller(model);
//		View view = new View(model, controller);
//		controller.setView(view);
//
//		// Chạy game đến khi kết thúc
//		game.runUntilEnd(500); // Giới hạn 500 vòng hoặc đến khi kết thúc
		Model model = new Model();
		Controller ctrl = new Controller(model);
		View view = new View(ctrl, model);
		ctrl.setView(view);
	}
}

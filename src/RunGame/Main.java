package RunGame;

import MVC.Controller;
import MVC.Model;
import MVC.View;

public class Main {
	public static void main(String[] args) {
		Model model = new Model();
		Controller ctrl = new Controller(model);
		View view = new View(ctrl, model);
		ctrl.setView(view);
	}
}

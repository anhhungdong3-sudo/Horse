
package GameSetup;

import java.util.Random;

public class Dice {
	private int result;

	private Random random = new Random();

	public int rollDice() {
		result = random.nextInt(6) + 1;
		return result;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}

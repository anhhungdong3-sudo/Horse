package horse;

public interface Observer {

	public void updateStart();

	public void updateSwitchTurn();
	
	public void updateItsAI();
	
	public void updateItsHuman();
	
	public void updateMove();
	
	public void updateWin();
}

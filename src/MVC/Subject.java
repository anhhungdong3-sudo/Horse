package MVC;

public interface Subject {
	public void addObserver(Observer ob);

	public void removeObserver(Observer ob);

	public void notifyStart();

	public void notifySwitchTurn();
	
	public void notifyItsAI();
	
	public void notifyItsHuman();
	
	public void notifyMove();
	
	public void notifyWin();
}

package minesweeper.event;

import java.util.EventListener;

public interface FieldListener extends EventListener
{
	public void fieldRevealed (FieldEvent e);
	
	public void gameWon (FieldEvent e);
	
	public void gameLost (FieldEvent e);
	
	public void gameStarted (FieldEvent e);
}

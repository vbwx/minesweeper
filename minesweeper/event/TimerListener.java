package minesweeper.event;

import java.util.EventListener;

public interface TimerListener extends EventListener
{
	public void timeElapsed (TimerEvent e);
	
	public void gameResumed (TimerEvent e);
	
	public void gamePaused (TimerEvent e);
}

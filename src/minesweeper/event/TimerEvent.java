package minesweeper.event;

import java.util.EventObject;

@SuppressWarnings("serial")
public class TimerEvent extends EventObject
{
	private final int elapsed;
	
	public int getElapsedTime () { return elapsed; }
	
	public TimerEvent (Object source, int elapsedTime)
	{
		super(source);
		elapsed = elapsedTime;
	}
}

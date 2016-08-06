package minesweeper.event;

import java.util.EventObject;
import minesweeper.Model;
import minesweeper.Position;

@SuppressWarnings("serial")
public class FieldEvent extends EventObject
{
	private final Position position;
	
	public Position getPosition () { return position; }
	
	public FieldEvent (Model source, Position position)
	{
		super(source);
		this.position = position;
	}
}

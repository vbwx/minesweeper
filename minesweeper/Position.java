package minesweeper;

public class Position
{
	private final byte col, row;
	
	public Position (byte row, byte col)
	{
		this.row = row;
		this.col = col;
	}
	
	public byte getCol () { return col; }
	
	public byte getRow () { return row; }
	
	@Override
	public String toString ()
	{
		return row + " | " + col;
	}
}

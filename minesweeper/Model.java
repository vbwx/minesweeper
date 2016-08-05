package minesweeper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import minesweeper.event.FieldEvent;
import minesweeper.event.FieldListener;
import minesweeper.event.TimerEvent;
import minesweeper.event.TimerListener;

/*
	field values (8 bits):
	bits [0,3]     ... how many mines around the field
	bit  [4] set   ... it's flagged
	bit  [5] set   ... it's marked
	bit  [6] set   ... revealed
	bits [5,6] set ... actively revealed (clicked)
	bit  [7] set   ... it's a mine
*/

public class Model
{
	private byte        rows, cols;
	private short       mines;
	private short       flagged;
	private byte[][]    field;
	private boolean     cheat;
	private boolean     done;
	private int         elapsed;
	private boolean     paused;
	private boolean[][] changed;
	
	private Set<FieldListener> fieldListeners;
	private Set<TimerListener> timerListeners;

	public byte    getRows          () { return rows;  }
	public byte    getCols          () { return cols;  }
	public short   getMines         () { return mines; }
	public short   getFlaggedFields () { return flagged; }
	public int     getElapsedTime   () { return elapsed; }
	public boolean isDone           () { return done;  }
	public boolean isCheating       () { return cheat; }
	public boolean isPaused         () { return paused; }
	public void    activateCheating () { cheat = true; }
	
	public void incrementTime ()
	{
		elapsed++;
		fireTimerEvent(false);
	}
	
	public void resetTime ()
	{
		elapsed = 0;
		fireTimerEvent(false);
	}
	
	public void setPaused (boolean paused)
	{
		this.paused = paused;
		if (elapsed > 0) fireTimerEvent(true);
	}

	public Model (int rows, int cols, int mines)
	{
		if (rows  < 1)              rows  = 1;
		if (rows  > Byte.MAX_VALUE) rows  = Byte.MAX_VALUE;
		if (cols  < 1)              cols  = 1;
		if (cols  > Byte.MAX_VALUE) cols  = Byte.MAX_VALUE;
		if (mines < 0)              mines = 0;
		if (mines > rows*cols-1)    mines = rows*cols-1;
		field      = new byte[rows][cols];
		changed    = new boolean[rows][cols];
		this.rows  = (byte)rows;
		this.cols  = (byte)cols;
		this.mines = (short)mines;
		this.cheat = false;
		fieldListeners = new HashSet<FieldListener>();
		timerListeners = new HashSet<TimerListener>();
	}
	
	public void addFieldListener (FieldListener l)
	{
		if (l != null) fieldListeners.add(l);
	}
	
	public void removeFieldListener (FieldListener l)
	{
		if (l != null) fieldListeners.remove(l);
	}
	
	public FieldListener[] getFieldListeners ()
	{
		return fieldListeners.toArray(new FieldListener[] {});
	}
	
	public void addTimerListener (TimerListener l)
	{
		if (l != null) timerListeners.add(l);
	}
	
	public void removeTimerListener (TimerListener l)
	{
		if (l != null) timerListeners.remove(l);
	}
	
	public TimerListener[] getTimerListeners ()
	{
		return timerListeners.toArray(new TimerListener[] {});
	}
	
	private void fireTimerEvent (boolean stateChanged)
	{
		TimerEvent e = new TimerEvent(this, elapsed);
		for (TimerListener l : timerListeners) {
			if (stateChanged) {
				if (paused)
					l.gamePaused(e);
				else
					l.gameResumed(e);
			} else
				l.timeElapsed(e);
		}
	}
	
	private void fireFieldEvent (boolean wonOrNew, Position pos)
	{
		FieldEvent e = new FieldEvent(this, pos);
		for (FieldListener l : fieldListeners) {
			if (done && wonOrNew)
				l.gameWon(e);
			else if (wonOrNew)
				l.gameStarted(e);
			else if (done)
				l.gameLost(e);
			else if (pos != null)
				l.fieldRevealed(e);
		}
	}

	public void init ()
	{
		done = false;
		flagged = (cheat ? mines : 0);
		resetTime();
		Random rand = new Random();
		for (short i = 0; i < mines; i++) {
			int r, c;
			r = rand.nextInt(rows);
			c = rand.nextInt(cols);
			if ((field[r][c]&0x80) == 0x80) i--; // continue
			else {
				if (cheat) {
					field[r][c] |= (byte)0x90;
					changed[r][c] = true;
				} else {
					field[r][c] |= (byte)0x80;
				}
				if (r > 0) {
					if (c > 0) field[r-1][c-1]++;
					field[r-1][c]++;
					if (c+1 < cols) field[r-1][c+1]++;
				}
				if (c > 0) field[r][c-1]++;
				if (c+1 < cols) field[r][c+1]++;
				if (r+1 < rows) {
					if (c > 0) field[r+1][c-1]++;
					field[r+1][c]++;
					if (c+1 < cols) field[r+1][c+1]++;
				}
			}
		}
		fireFieldEvent(true, null);
	}
	
	public void flagField (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return;
		field[r][c] |= 0x10;
		changed[r][c] = true;
		flagged++;
		fireFieldEvent(false, pos);
	}
	
	public boolean isFlagged (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return false;
		return (field[r][c]&0x10) == 0x10;
	}
	
	public void markField (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return;
		field[r][c] |= 0x20;
		field[r][c] &= 0xEF; // no longer flagged
		changed[r][c] = true;
		flagged--;
		fireFieldEvent(false, pos);
	}
	
	public boolean isMarked (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return false;
		return (field[r][c]&0x60) == 0x20;
	}
	
	public void unmarkField (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return;
		field[r][c] &= 0xCF;
		changed[r][c] = true;
		fireFieldEvent(false, pos);
	}
	
	public boolean isMined (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return false;
		return (field[r][c]&0x80) == 0x80;
	}
	
	public boolean isClicked (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return false;
		return (field[r][c]&0x60) == 0x60;
	}
	
	public byte getValue (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return -1;
		return (byte)(field[r][c]&0x0F);
	}

	public void revealField (Position pos)
	{
		boolean won = false;
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return;
		else if (revealed(r, c))
			pos = null;
		else {
			if (reveal(r, c))
				done = won = everythingRevealed();
			else
				done = true;
			field[r][c] |= 0x20; // clicked
		}
		fireFieldEvent(won, pos);
	}
	
	public boolean isRevealed (Position pos)
	{
		byte r = pos.getRow(), c = pos.getCol();
		if (r < 0 || r >= rows || c < 0 || c >= cols)
			return false;
		return revealed(r, c);
	}
	
	private boolean revealed (int r, int c)
	{
		return (field[r][c]&0x40) == 0x40;
	}
	
	// for better performance in update()
	public boolean hasChanged (Position pos)
	{
		if (done) return true;
		if (changed[pos.getRow()][pos.getCol()]) {
			changed[pos.getRow()][pos.getCol()] = false;
			return true;
		}
		return false;
	}

	private boolean reveal (int r, int c)
	{
		if ((field[r][c]&0x10) == 0) { // not flagged
			field[r][c] |= 0x40; // it's revealed
			field[r][c] &= 0xDF; // no longer marked
			if ((field[r][c]&0x0F) == 0) { // no mines in surroundings
				if (r > 0) {
					if (c > 0 && !revealed(r-1, c-1)) reveal(r-1, c-1);
					if (!revealed(r-1, c)) reveal(r-1, c);
					if (c+1 < cols && !revealed(r-1, c+1)) reveal(r-1, c+1);
				}
				if (c > 0 && !revealed(r, c-1)) reveal(r, c-1);
				if (c+1 < cols && !revealed(r, c+1)) reveal(r, c+1);
				if (r+1 < rows) {
					if (c > 0 && !revealed(r+1, c-1)) reveal(r+1, c-1);
					if (!revealed(r+1, c)) reveal(r+1, c);
					if (c+1 < cols && !revealed(r+1, c+1)) reveal(r+1, c+1);
				}
			}
			changed[r][c] = true;
		}
		return (field[r][c]&0x80) == 0; // not mined
	}

	private boolean everythingRevealed ()
	{
		for (byte r = 0; r < rows; r++)
			for (byte c = 0; c < cols; c++)
				if (!revealed(r, c) && (field[r][c]&0x80) == 0)
					return false;
		return true;
	}
}

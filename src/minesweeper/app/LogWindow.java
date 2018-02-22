package minesweeper.app;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import minesweeper.Model;
import minesweeper.event.FieldEvent;
import minesweeper.event.FieldListener;
import minesweeper.event.TimerEvent;
import minesweeper.event.TimerListener;

@SuppressWarnings("serial")
public final class LogWindow extends JDialog
	implements WindowListener, TimerListener, FieldListener
{
	private ApplicationWindow parent;
	private JScrollPane pane;
	private DefaultListModel<String> log;
	private Controller controller;
	private boolean shown;
	
	static final int WIDTH = 180;
	
	public LogWindow (Controller ctrl)
	{
		super(ctrl.getAppWindow(), "Game Log");
		parent = ctrl.getAppWindow();
		controller = ctrl;
		log = new DefaultListModel<String>();
		setResizable(false);
		setFocusableWindowState(false);
		addWindowListener(this);
		JList<String> list = new JList<String>(log);
		list.setEnabled(false);
		pane = new JScrollPane(list);
		pane.setBorder(BorderFactory.createEmptyBorder());
		add(pane);
		setSize(WIDTH, 0);
		shown = false;
	}
	
	@Override
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		if (visible) {
			int x, y, w, scr;
			x = parent.getX(); y = parent.getY();
			w = parent.getWidth();
			scr = Controller.SCR_WIDTH;
			if (x+w+WIDTH <= scr)
				setLocation(x+w+1, y);
			else if (x-WIDTH >= 0)
				setLocation(x-WIDTH-1, y);
			else {
				parent.setLocation(Math.max(scr-(w+WIDTH+1), 0), y);
				x = parent.getX();
				setLocation(x+w+1, y);
			}
			setSize(WIDTH, parent.getHeight());
		} else {
			super.setSize(WIDTH, 0);
		}
		shown = visible;
	}
	
	@Override
	public void setLocation (int xEnd, int yEnd)
	{
		if (controller != null && controller.animation && isVisible() && shown) {
			int x = getX(), y = getY();
			while (x != xEnd || y != yEnd) {
				if      (x > xEnd) x -= (x-10 < xEnd ? (x-3 < xEnd ? 1 : 3) : 10);
				else if (x < xEnd) x += (x+10 > xEnd ? (x+3 > xEnd ? 1 : 3) : 10);
				if      (y > yEnd) y -= (y-10 < yEnd ? (y-3 < yEnd ? 1 : 3) : 10);
				else if (y < yEnd) y += (y+10 > yEnd ? (y+3 > yEnd ? 1 : 3) : 10);
				super.setLocation(x, y);
			}
		} else
			super.setLocation(xEnd, yEnd);
	}
	
	@Override
	public void setSize (int width, int height)
	{
		if (controller != null && controller.animation && isVisible()) {
			int w = getWidth(), h = getHeight();
			while (w != width || h != height) {
				if      (w > width)  w -= (w-10 < width  ? (w-3 < width  ? 1 : 3) : 10);
				else if (w < width)  w += (w+10 > width  ? (w+3 > width  ? 1 : 3) : 10);
				if      (h > height) h -= (h-10 < height ? (h-3 < height ? 1 : 3) : 10);
				else if (h < height) h += (h+10 > height ? (h+3 > height ? 1 : 3) : 10);
				super.setSize(w, h);
			}
		} else
			super.setSize(width, height);
	}
	
	public void windowActivated (WindowEvent e) { }

	public void windowClosed (WindowEvent e) { }

	public void windowClosing (WindowEvent e)
	{
		parent.hideLogWindow();
	}

	public void windowDeactivated (WindowEvent e) { }

	public void windowDeiconified (WindowEvent e) { }

	public void windowIconified (WindowEvent e) { }

	public void windowOpened (WindowEvent e) { }

	public void gamePaused (TimerEvent e)
	{
		Model m = (Model)e.getSource();
		log.addElement("Paused (" + m.getElapsedTime() + " s)");
	}

	public void gameResumed (TimerEvent e)
	{
		log.addElement("Resumed");
	}

	public void timeElapsed (TimerEvent e) { }

	public void fieldRevealed (FieldEvent e)
	{
		log.addElement(e.getPosition().toString());
	}

	public void gameLost (FieldEvent e)
	{
		Model m = (Model)e.getSource();
		log.addElement("Lost (" + m.getElapsedTime() + " s)");
	}

	public void gameStarted (FieldEvent e)
	{
		log.addElement("Started");
	}

	public void gameWon (FieldEvent e)
	{
		Model m = (Model)e.getSource();
		log.addElement("Won" + (m.isCheating() ? " LOL" : "") +
			" (" + m.getElapsedTime() + " s)");
	}
}

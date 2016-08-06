package minesweeper.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import minesweeper.Model;
import minesweeper.Position;
import minesweeper.event.FieldEvent;
import minesweeper.event.FieldListener;
import minesweeper.event.TimerEvent;
import minesweeper.event.TimerListener;

@SuppressWarnings("serial")
public class Field extends JPanel implements FieldListener, TimerListener
{
	private Model model;
	private Controller controller;
	private JButton[][] bField;
	
	public Field (Controller ctrl)
	{
		controller = ctrl;
	}
	
	protected void update (boolean won)
	{
		if (model == null) return;
		for (byte r = 0; r < model.getRows(); r++)
			for (byte c = 0; c < model.getCols(); c++) {
				Position pos = new Position(r, c);
				if (model.hasChanged(pos)) {
					JButton b        = bField[r][c];
					boolean mined    = model.isMined(pos);
					boolean flagged  = model.isFlagged(pos);
					boolean clicked  = model.isClicked(pos);
					boolean marked   = model.isMarked(pos);
					boolean revealed = model.isRevealed(pos);
					boolean cheating = model.isCheating();
					byte    value    = model.getValue(pos);
					if (model.isDone()) {
						if (flagged || (mined && won))
							b.setIcon(loadImage(mined ? "flag" : "x"));
						else if (mined)
							b.setIcon(loadImage(clicked ? "boom" : "mine"));
						else if (revealed || cheating)
							b.setIcon(value>0 ? loadImage(String.valueOf(value)) : null);
						else
							b.setIcon(null);
						if (b.getMouseListeners().length == 2) {
							b.removeMouseListener(b.getMouseListeners()[0]);
							// b.getMouseListeners()[1] is BasicButtonUI's MouseListener
							b.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
						}
					} else {
						if (flagged)
							b.setIcon(loadImage("flag"));
						else if (marked)
							b.setIcon(loadImage("mark"));
						else if (revealed) {
							if (b.getMouseListeners().length == 2) {
								b.removeMouseListener(b.getMouseListeners()[0]);
								b.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
							}
							b.setIcon(value>0 ? loadImage(String.valueOf(value)) : null);
						} else
							b.setIcon(null);
					}
				}
			}
	}
	
	public void fieldRevealed (FieldEvent e)
	{
		update(false);
	}

	public void gameLost (FieldEvent e)
	{
		update(false);
	}

	public void gameStarted (FieldEvent e)
	{
		model = (Model)e.getSource();
		setVisible(false);
		removeAll();
		setLayout(new GridLayout(model.getRows(), model.getCols()));
		setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.GRAY));
		bField = new JButton[model.getRows()][model.getCols()];
		for (byte r = 0; r < model.getRows(); r++)
			for (byte c = 0; c < model.getCols(); c++) {
				JButton b = new JButton();
				Position pos = new Position(r, c);
				b.setPreferredSize(new Dimension(16, 16));
				b.addMouseListener(new FieldButtonController(pos));
				b.setMargin(new Insets(0, 0, 0, 0));
				b.setFocusable(false);
				b.setBackground(Color.LIGHT_GRAY);
				b.setRolloverEnabled(false);
				b.setUI(new BasicButtonUI());
				b.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				bField[r][c] = b;
				add(b);
			}
		update(false);
		setVisible(true);
	}

	public void gameWon (FieldEvent e)
	{
		update(true);
	}

	public void gamePaused (TimerEvent e)
	{
		setVisible(false);
	}

	public void gameResumed (TimerEvent e)
	{
		setVisible(true);
	}

	public void timeElapsed (TimerEvent e) { }
	
	private class FieldButtonController extends MouseAdapter
	{
		final Position position;
		
		FieldButtonController (Position pos)
		{
			position = pos;
		}
		
		@Override
		public void mousePressed (MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && !model.isFlagged(position)) {
				JButton btn = Field.this.bField[position.getRow()][position.getCol()];
				btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
				controller.getAppWindow().toggleSmiley(false);
			} else if (e.getButton() == MouseEvent.BUTTON3) { // right click
				controller.toggleFieldState(position);
			}
		}
		
		@Override
		public void mouseReleased (MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && !model.isFlagged(position)) {
				byte row = position.getRow(), col = position.getCol();
				JButton btn = Field.this.bField[row][col];
				btn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				controller.getAppWindow().toggleSmiley(true);
				if (bField[row][col].getVisibleRect().contains(e.getPoint())) // button clicked
					controller.revealField(position);
			}
		}
	}
	
	private Icon loadImage (String name)
	{
		return new ImageIcon(getClass().getResource(Controller.RES_PATH + name + ".gif"));
	}
}

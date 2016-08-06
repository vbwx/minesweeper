package minesweeper.app;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import minesweeper.Model;
import minesweeper.event.FieldEvent;
import minesweeper.event.FieldListener;
import minesweeper.event.TimerEvent;
import minesweeper.event.TimerListener;

@SuppressWarnings("serial")
public final class ApplicationWindow extends JFrame
implements MouseListener, FieldListener, TimerListener
{
	private Model model;
	private Controller controller;
	private Field field;
	private JButton bNewGame;
	private JMenuItem mLog;
	private JTextField tTimer, tMines;
	private AudioClip boom, yeah;

	public Field getField () { return field; }

	public ApplicationWindow (Controller c)
	{
		super("Minesweeper");
		controller = c;
		Container panel = getContentPane();
		panel.setLayout(new BorderLayout());
		field = new Field(controller);
		try {
			boom = Applet.newAudioClip(getClass().getResource(Controller.RES_PATH + "boom.wav"));
			yeah = Applet.newAudioClip(getClass().getResource(Controller.RES_PATH + "yeah.wav"));
		} catch (Exception e) { }

		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("Spiel");
		JMenuItem mNew = new JMenuItem("Neues Spiel");
		mNew.setActionCommand("new_game");
		mNew.addActionListener(controller);
		menu.add(mNew);
		menu.addSeparator();
		mLog = new JMenuItem("Aufzeichnung anzeigen");
		mLog.setActionCommand("show_log");
		mLog.addActionListener(controller);
		menu.add(mLog);
		JMenuItem mi = new JMenuItem("Einstellungen...");
		mi.setActionCommand("settings");
		mi.addActionListener(controller);
		menu.add(mi);
		menu.addSeparator();
		mi = new JMenuItem("Beenden");
		mi.setActionCommand("quit");
		mi.addActionListener(controller);
		menu.add(mi);
		mb.add(menu);
		menu = new JMenu("Hilfe");
		mi = new JMenuItem("Anleitung...");
		mi.setActionCommand("help");
		mi.addActionListener(controller);
		menu.add(mi);
		mi = new JMenuItem("Cheat-Modus...");
		mi.setActionCommand("cheating");
		mi.addActionListener(controller);
		menu.add(mi);
		menu.addSeparator();
		mi = new JMenuItem("Über Minesweeper");
		mi.setActionCommand("about");
		mi.addActionListener(controller);
		menu.add(mi);
		mb.add(menu);

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		bNewGame = new JButton();
		bNewGame.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		bNewGame.setPreferredSize(new Dimension(23, 23));
		bNewGame.setFocusPainted(false);
		bNewGame.setRolloverEnabled(false);
		bNewGame.setBackground(Color.LIGHT_GRAY);
		bNewGame.setMargin(new Insets(1, 1, 1, 1));
		bNewGame.setActionCommand("new_game");
		bNewGame.addActionListener(controller);
		bNewGame.addMouseListener(this);
		p.add(bNewGame, BorderLayout.CENTER);
		top.add(p, BorderLayout.CENTER);
		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		tMines = new JTextField();
		tMines.setBackground(Color.GRAY);
		tMines.setForeground(Color.RED);
		tMines.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		tMines.setFont(new Font("Monospaced", Font.BOLD, 16));
		tMines.setEditable(false);
		tMines.setFocusable(false);
		tMines.setBorder(BorderFactory.createLoweredBevelBorder());
		tMines.setPreferredSize(new Dimension(46, 20));
		p.add(tMines);
		top.add(p, BorderLayout.WEST);
		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tTimer = new JTextField();
		tTimer.setBackground(Color.GRAY);
		tTimer.setForeground(Color.RED);
		tTimer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		tTimer.setFont(new Font("Monospaced", Font.BOLD, 16));
		tTimer.setEditable(false);
		tTimer.setFocusable(false);
		tTimer.setBorder(BorderFactory.createLoweredBevelBorder());
		tTimer.setPreferredSize(new Dimension(46, 20));
		p.add(tTimer);
		top.add(p, BorderLayout.EAST);

		setJMenuBar(mb);
		add(top, BorderLayout.NORTH);
		add(field, BorderLayout.CENTER);

		addWindowFocusListener(controller);
		addWindowListener(controller);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(161, 120));
		setResizable(false);
	}

	public void mouseClicked (MouseEvent e) { }

	public void mouseEntered (MouseEvent e) { }

	public void mouseExited (MouseEvent e) { }

	public void mousePressed (MouseEvent e)
	{
		bNewGame.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void mouseReleased (MouseEvent e)
	{
		bNewGame.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	}

	@Override
	public void setVisible (boolean visible)
	{
		if (mLog.getActionCommand().equals("hide_log"))
			controller.getLogWindow().setVisible(visible);
		super.setVisible(visible);
	}

	@Override
	public void setLocation (int xEnd, int yEnd)
	{
		if (controller != null && controller.animation && isVisible()) {
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

	public void showAboutMessage ()
	{
		JOptionPane.showMessageDialog(this,
			"Minesweeper 2.4\nCopyright © 2010 Bernhard Waldbrunner", "",
			JOptionPane.PLAIN_MESSAGE);
	}

	public void showLogWindow ()
	{
		controller.getLogWindow().setVisible(true);
		mLog.setText("Aufzeichnung verbergen");
		mLog.setActionCommand("hide_log");
	}

	public void hideLogWindow ()
	{
		controller.getLogWindow().setVisible(false);
		mLog.setText("Aufzeichnung anzeigen");
		mLog.setActionCommand("show_log");
	}

	private Icon loadImage (String name)
	{
		return new ImageIcon(getClass().getResource(Controller.RES_PATH + name + ".gif"));
	}

	public void showHelpMessage ()
	{
		JOptionPane.showMessageDialog(this,
			"Klicken Sie auf ein Feld, um es aufzudecken.\n\n" +
			"Ein Rechtsklick belegt das Feld mit einer Flagge bzw. Markierung.\n" +
			"Flaggen lassen sich nicht mehr aufdecken, sie müssen zuerst " +
			"mit einem weiteren Rechtsklick entfernt werden.\n\n" +
			"Das Spiel kann pausiert werden, indem man z.B. außerhalb des " +
			"Fensters klickt.",
			"Anleitung", JOptionPane.PLAIN_MESSAGE);
	}

	public void showCheatMessage ()
	{
		JOptionPane.showMessageDialog(this, "Hinweis: Konami-Code", "Cheat-Modus",
			JOptionPane.PLAIN_MESSAGE);
	}

	public boolean confirmQuitting ()
	{
		return (JOptionPane.showConfirmDialog(this,
			"Wollen Sie das Spiel wirklich abbrechen?", "Spiel beenden",
			JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION);
	}

	public void fieldRevealed (FieldEvent e)
	{
		tMines.setText(String.format("%04d", model.getMines() - model.getFlaggedFields()));
	}

	public void gameLost (FieldEvent e)
	{
		controller.getTimer().stop();
		if (controller.sound) boom.play();
		if (controller.animation) {
			int x = getX(), y = getY();
			// shaking animation:
			for (int i = 0; i < 5; i++) {
				setLocation(x-2, y-2);
				setLocation(x-2, y+2);
				setLocation(x+2, y  );
				setLocation(x,   y+2);
				setLocation(x,   y  );
			}
		}
		bNewGame.setIcon(loadImage("dead"));
	}

	public void gameStarted (FieldEvent e)
	{
		model = (Model)e.getSource();
		tMines.setText(model.isCheating() ? "0000" : String.format("%04d", model.getMines()));
		tTimer.setText("0000");
		bNewGame.setIcon(loadImage("smiley"));
	}

	public void gameWon (FieldEvent e)
	{
		controller.getTimer().stop();
		if (controller.sound) yeah.play();
		bNewGame.setIcon(loadImage("yeah"));
	}

	public void gamePaused (TimerEvent e) { }

	public void gameResumed (TimerEvent e) { }

	public void timeElapsed (TimerEvent e)
	{
		tTimer.setText(String.format("%04d", e.getElapsedTime()));
	}

	public void toggleSmiley (boolean normal)
	{
		bNewGame.setIcon(loadImage(normal ? "smiley" : "o"));
	}
}

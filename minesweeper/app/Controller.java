package minesweeper.app;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import minesweeper.Model;
import minesweeper.Position;

public class Controller
implements ActionListener, WindowListener, WindowFocusListener
{
	static final String RES_PATH;
	static final int SCR_WIDTH, SCR_HEIGHT;
	
	private Model model;
	private Timer timer;
	private ApplicationWindow window;
	private SettingsWindow wSettings;
	private LogWindow wLog;
	
	// Settings:
	protected boolean cheat, sound, animation;
	protected int rows, cols, mines;
	
	static {
		SCR_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		SCR_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		RES_PATH = "/minesweeper/res/";
	}
	
	public Model getModel () { return model; }
	
	public ApplicationWindow getAppWindow () { return window; }
	
	public LogWindow getLogWindow () { return wLog; }
	
	public SettingsWindow getSettingsWindow () { return wSettings; }
	
	public Timer getTimer () { return timer; }
	
	public Controller (int rows, int cols, int mines, boolean sound, boolean animation)
	{
		window = new ApplicationWindow(this);
		wLog = new LogWindow(this);
		wSettings = new SettingsWindow(this);
		timer = new Timer(1000, this);
		timer.setInitialDelay(1000);
		this.cols = cols;
		this.rows = rows;
		this.mines = mines;
		this.sound = sound;
		this.animation = animation;
		cheat = false;
		wSettings.exchange(false);
		try {
			UIManager.setLookAndFeel(selectPLAF());
			SwingUtilities.updateComponentTreeUI(window);
			SwingUtilities.updateComponentTreeUI(wLog);
			SwingUtilities.updateComponentTreeUI(wSettings);
		} catch (Exception e) { }
	}
	
	private String selectPLAF ()
	{
		String laf = "";
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if (info.getClassName().endsWith("NimbusLookAndFeel"))
				return info.getClassName();
			else if (info.getClassName().endsWith("MetalLookAndFeel"))  // fallback
				laf = info.getClassName();
		}
		return laf;
	}
	
	private void play ()
	{
		timer.restart();
		model = new Model(rows, cols, mines);
		if (cheat) model.activateCheating();
		model.addFieldListener(window.getField());
		model.addTimerListener(window.getField());
		model.addFieldListener(window);
		model.addTimerListener(window);
		model.addFieldListener(wLog);
		model.addTimerListener(wLog);
		model.init();
		window.pack();
		if (wSettings.hasChanged()) {
			window.setLocation((Controller.SCR_WIDTH-window.getWidth())/2,
			                   (Controller.SCR_HEIGHT-window.getHeight())/2);
		}
		window.setVisible(true);
	}
	
	public static void main (String[] args)
	{
		new Controller(10, 10, 5, true, true).play();
	}

	public void revealField (Position pos)
	{
		if (pos == null) return;
		model.revealField(pos);
	}

	public void toggleFieldState (Position pos)
	{
		if (pos == null) return;
		if (model.isFlagged(pos))
			model.markField(pos);
		else if (model.isMarked(pos))
			model.unmarkField(pos);
		else
			model.flagField(pos);
	}
	
	public void quit ()
	{
		if (model.isDone() || window.confirmQuitting())
			System.exit(0);
	}
	
	public void actionPerformed (ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if (e.getSource() == timer) {
			model.incrementTime();
		} else if (cmd.equals("new_game")) {
			play();
		} else if (cmd.equals("quit")) {
			quit();
		} else if (cmd.equals("about")) {
			window.showAboutMessage();
		} else if (cmd.equals("help")) {
			window.showHelpMessage();
		} else if (cmd.equals("cheating")) {
			window.showCheatMessage();
		} else if (cmd.equals("show_log")) {
			window.showLogWindow();
		} else if (cmd.equals("hide_log")) {
			window.hideLogWindow();
		} else if (cmd.equals("settings")) {
			wSettings.setVisible(true);
		} else if (cmd.equals("settings.cancel")) {
			wSettings.setVisible(false);
			wSettings.exchange(false);
		} else if (cmd.equals("settings.save")) {
			if (wSettings.exchange(true))
				wSettings.showSettingsMessage();
			wSettings.setVisible(false);
		}
	}
	
	public void windowActivated (WindowEvent e) { }

	public void windowClosed (WindowEvent e) { }

	public void windowClosing (WindowEvent e)
	{
		quit();
	}

	public void windowDeactivated (WindowEvent e) { }

	public void windowDeiconified (WindowEvent e) { }

	public void windowIconified (WindowEvent e) { }

	public void windowOpened (WindowEvent e) { }

	public void windowGainedFocus (WindowEvent e)
	{
		model.setPaused(false);
		if (!model.isDone()) timer.start();
	}

	public void windowLostFocus (WindowEvent e)
	{
		model.setPaused(true);
		timer.stop();
	}
}

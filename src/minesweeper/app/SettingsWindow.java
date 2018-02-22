package minesweeper.app;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public final class SettingsWindow extends JDialog implements ChangeListener, KeyListener
{
	private JLabel lRows, lCols, lMines;
	private JSlider sRows, sCols, sMines;
	private JCheckBox cSound, cAnimation;
	private JButton bCancel, bOK;
	private boolean cheat;
	private boolean changed;
	private String konami;
	private Controller ctrl;
	
	public SettingsWindow (Controller controller)
	{
		super(controller.getAppWindow());
		Container panel = getContentPane();
		panel.setLayout(new GridLayout(7, 1));
		ctrl = controller;
		
		JLabel l = new JLabel("Settings", JLabel.CENTER);
		panel.add(l);
		JPanel p = new JPanel(new FlowLayout());
		l = new JLabel("Columns:", JLabel.RIGHT);
		l.setPreferredSize(new Dimension(77, 24));
		p.add(l);
		sCols = new JSlider(1, 99);
		sCols.setSnapToTicks(true);
		sCols.setPreferredSize(new Dimension(240, 24));
		sCols.setMinorTickSpacing(1);
		sCols.addChangeListener(this);
		sCols.addKeyListener(this);
		p.add(sCols);
		lCols = new JLabel("0", JLabel.LEFT);
		lCols.setPreferredSize(new Dimension(40, 24));
		p.add(lCols);
		panel.add(p);
		p = new JPanel(new FlowLayout());
		l = new JLabel("Rows:", JLabel.RIGHT);
		l.setPreferredSize(new Dimension(77, 24));
		p.add(l);
		sRows = new JSlider(1, 99);
		sRows.setSnapToTicks(true);
		sRows.setPreferredSize(new Dimension(240, 24));
		sRows.setMinorTickSpacing(1);
		sRows.addChangeListener(this);
		sRows.addKeyListener(this);
		p.add(sRows);
		lRows = new JLabel("0", JLabel.LEFT);
		lRows.setPreferredSize(new Dimension(40, 24));
		p.add(lRows);
		panel.add(p);
		p = new JPanel(new FlowLayout());
		l = new JLabel("Mines:", JLabel.RIGHT);
		l.setPreferredSize(new Dimension(77, 24));
		p.add(l);
		sMines = new JSlider(0, 99);
		sMines.setSnapToTicks(true);
		sMines.setPreferredSize(new Dimension(240, 24));
		sMines.setMinorTickSpacing(1);
		sMines.addChangeListener(this);
		sMines.addKeyListener(this);
		p.add(sMines);
		lMines = new JLabel("0", JLabel.LEFT);
		lMines.setPreferredSize(new Dimension(40, 24));
		p.add(lMines);
		panel.add(p);
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		l = new JLabel("Options:", JLabel.RIGHT);
		l.setPreferredSize(new Dimension(77, 24));
		p.add(l);
		cSound = new JCheckBox("Sound effects");
		cSound.addKeyListener(this);
		p.add(cSound);
		panel.add(p);
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		l = new JLabel("", JLabel.RIGHT);
		l.setPreferredSize(new Dimension(77, 24));
		p.add(l);
		cAnimation = new JCheckBox("Animations");
		cAnimation.addKeyListener(this);
		p.add(cAnimation);
		panel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bOK = new JButton("OK");
		bOK.addKeyListener(this);
		bOK.setActionCommand("settings.save");
		bOK.setFocusPainted(false);
		bOK.addActionListener(controller);
		p.add(bOK);
		getRootPane().setDefaultButton(bOK);
		panel.add(p);
		bCancel = new JButton("Cancel");
		bCancel.setActionCommand("settings.cancel");
		bCancel.addKeyListener(this);
		bCancel.addActionListener(controller);
		p.add(bCancel);
		
		konami = "";
		changed = true;
		setResizable(false);
		setUndecorated(true);
		setModal(true);
		pack();
	}
	
	@Override
	public void setVisible (boolean visible)
	{
		if (visible) {
			bOK.requestFocusInWindow();
			setLocation((Controller.SCR_WIDTH-getWidth())/2,
			            (Controller.SCR_HEIGHT-getHeight())/2);
		}
		super.setVisible(visible);
	}

	public void stateChanged (ChangeEvent e)
	{
		JSlider s = (JSlider)e.getSource();
		String str = String.format("%2d", s.getValue());
		if (s == sRows) {
			lRows.setText(str);
			sMines.setMaximum(sRows.getValue()*sCols.getValue()-1);
		} else if (s == sCols) {
			lCols.setText(str);
			sMines.setMaximum(sRows.getValue()*sCols.getValue()-1);
		} else if (s == sMines) {
			lMines.setText(str);
		}
	}

	public void keyPressed (KeyEvent e)
	{
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				bCancel.doClick();
				return;
			case KeyEvent.VK_UP:    
				konami += "u";
				break;
			case KeyEvent.VK_DOWN:  
				konami += "d";
				break;
			case KeyEvent.VK_LEFT:  
				konami += "l";
				break;
			case KeyEvent.VK_RIGHT: 
				konami += "r";
				break;
			case KeyEvent.VK_A:     
				konami += "A";
				break;
			case KeyEvent.VK_B:     
				konami += "B";
				break;
			default:                
				konami += " ";
				break;
		}
		if (!konami.matches("^(u(u(d(d(l(r(l(r(B(A)?)?)?)?)?)?)?)?)?)?$"))
			konami = "";
		if (konami.equals("uuddlrlrBA")) {
			bOK.requestFocusInWindow();
			JOptionPane.showMessageDialog(this, "God mode " + (cheat ? "de" : "") +
				"activated.", "", JOptionPane.INFORMATION_MESSAGE);
			cheat = !cheat;
			konami = "";
		}
	}

	public void keyReleased (KeyEvent e) { }

	public void keyTyped (KeyEvent e) { }
	
	public void showSettingsMessage ()
	{
		JOptionPane.showMessageDialog(this, "Start a new game to apply these settings.",
			"", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public boolean exchange (boolean write)
	{
		boolean changed = (ctrl.cols  != sCols.getValue()  ||
		                   ctrl.rows  != sRows.getValue()  ||
		                   ctrl.mines != sMines.getValue() ||
		                   ctrl.cheat != cheat);
		this.changed    = (ctrl.cols  != sCols.getValue()  ||
		                   ctrl.rows  != sRows.getValue());
		if (write) {
			ctrl.cols      = sCols.getValue();
			ctrl.rows      = sRows.getValue();
			ctrl.mines     = sMines.getValue();
			ctrl.cheat     = cheat;
			ctrl.animation = cAnimation.isSelected();
			ctrl.sound     = cSound.isSelected();
		} else {
			sCols.setValue(ctrl.cols);
			sRows.setValue(ctrl.rows);
			sMines.setValue(ctrl.mines);
			cAnimation.setSelected(ctrl.animation);
			cSound.setSelected(ctrl.sound);
			cheat = ctrl.cheat;
		}
		return changed;
	}
	
	public boolean hasChanged ()
	{
		if (changed) {
			changed = false;
			return true;
		}
		return false;
	}
}

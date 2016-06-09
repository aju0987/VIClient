/*
 * Summary: Application that plays with different 2d graphics attributes
 * Author: Kannan Balasubramanian 
 * Creation Date: 06-Aug-2012
 * Last updated date: 08-Aug-2012
 */

package com.vmware.viclient.ui;

import java.util.Vector;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Frame;
import java.awt.Window;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.vmware.viclient.helper.Utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;

public class AboutPanel extends JPanel implements ActionListener {

  private int TIMERCOUNT = 30;
  private float fade = 0.0f;
  private String [] names = null;
  private int xLoc = 200, yLoc = -Integer.MAX_VALUE;
  private Color [] colors = null; 
  private int fontSize = 20;
  private int LINEGAP = fontSize + 8;
  private Font font = new Font("Dialog", Font.PLAIN, fontSize);
  private javax.swing.Timer timer = null;
  private int colorCount = 0;
  private boolean bIncrement = true;
  private GradientPaint gPaint = null;
  private boolean bShowFading = false;
  private boolean bRandomColor = true;
  private JLabel hiddenBtn = new JLabel() ;

  public AboutPanel(Window w) {
	setDoubleBuffered(false);
	setOpaque(true);
	fillNamesList();
	colors = new Color[names.length];
	timer = new javax.swing.Timer(TIMERCOUNT, this);
	timer.start();
	hiddenBtn.setVisible(true);
	add(hiddenBtn);
	final Window window = w;
	Utilities.handleEscKey(hiddenBtn, new Runnable() {
             public void run() {
		  if (window != null) {
                      window.setVisible(false);
		  }
	     }
	});
  }

  public void setShowFading(boolean bFlag) {
	this.bShowFading = bFlag;
  }

  public void setRandomColor(boolean bFlag) {
	this.bRandomColor = bFlag;
  }

  public void setFontSize(int size) {
	this.fontSize = size;
        this.LINEGAP = fontSize + 8;
        font = new Font("Dialog", Font.PLAIN, fontSize);
  }

  public void setTimerCount(int timerCount) {
	this.TIMERCOUNT = timerCount;
	timer.setDelay(TIMERCOUNT);
	timer.restart();
  }

  public void fillNamesList() {

	try {
		URL fileURL = getClass().getResource("/JARs/names.properties");
		System.err.println("file url: " + fileURL);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileURL.openStream()));
		String lineStr = null;
		Vector v = new Vector();
		while ( (lineStr = reader.readLine()) != null) {
			v.addElement(lineStr);
		}
		names = new String[v.size()];
		for (int i=0; i<v.size(); i++) {
			names[i] = (String)v.elementAt(i);
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
  }

  @Override
  public void actionPerformed(ActionEvent e) {

				try {
					if (colorCount == 0) {
						for (int i=0; i<names.length; i++) {
							int r = (int)(Math.random() * 255.0f);
							int g = (int)(Math.random() * 255.0f);
							int b = (int)(Math.random() * 255.0f);
							colors[i] = new Color(r, g, b);
						}
					}
					colorCount++;
					if (colorCount > 10) {
						colorCount = 0;
					}
					if (yLoc == -Integer.MAX_VALUE) {
						return;
					}
					if (yLoc < 0 && Math.abs(yLoc- names.length*fontSize) > ((names.length) * fontSize) + ((names.length-1) * LINEGAP)) {
						yLoc = AboutPanel.this.getHeight();
					}
					yLoc --;
					AboutPanel.this.repaint();

					if (!bShowFading) {
						return;
					}

					if (bIncrement) {
						if (fade + 0.1 < 1.0f) {
							fade += 0.1;
						} else {
							bIncrement = false;
						}
					} else {
						if (fade - 0.1 > 0.0f) { 
							fade -= 0.1;
						} else {
							bIncrement = true;
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
  }

  @Override
  public void update(Graphics g) {
	if (g == null) {
	    return;
	}
	paint(g);
  }
  
  
  @Override
  public void paintComponent(Graphics g) {
      
	Graphics2D g2D = (Graphics2D)g;
	if (gPaint == null) {
  		gPaint = new GradientPaint(0, 0, Color.blue, this.getWidth(), this.getHeight(), Color.red);
	}

	g2D.setPaint(gPaint);
	g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

	if (bShowFading) {
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade);
		g2D.setComposite(composite);
	}
	g.setFont(font);
        int step = 0;
	if (yLoc == -Integer.MAX_VALUE) {
		yLoc = getHeight() - 3;
	}
	if (!bRandomColor) {
		g2D.setColor(Color.green);
	}
	for (int i=0; i<names.length; i++) {

		if (bRandomColor) {
			g2D.setColor(colors[i]);
		}
		g2D.drawString(names[i], xLoc, yLoc + step);
		step += LINEGAP;
	}
  }

   public static void Usage() {
	System.out.println("Usage...: java <-Dtimer=[int]> AboutPanel <-fade> <-norandom> <-fullscreen> <-Dfontsize=[int]> <-help> \n\n");
   }
  
   public static void main (String [] args) {

		AboutPanel panel = new AboutPanel(null);
		JDialog dlg = new JDialog((Frame)null, "VI Client");
		dlg.setContentPane(panel);
		dlg.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
		if (args != null & args.length > 0) {
			if (args[0].equals("-help")) {
				Usage();
                                System.exit(0);
				return;
			}
		}
		boolean bFullscreen = false;
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-fade")) {
				panel.setShowFading(true);
			} else if (args[i].equals("-norandom")) {
				panel.setRandomColor(false);
			} else if (args[i].equals("-fullscreen")) {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice[] gs = ge.getScreenDevices();
				if (gs != null && gs.length >0) {
					gs[0].setFullScreenWindow(dlg);
					bFullscreen = true;
				}
			}
		}
		String timerStr = System.getProperty("timer");
		if (timerStr != null) {
			try {
				int timerCount = Integer.valueOf(timerStr).intValue();
				System.out.println("Timer set to " + timerCount + " milliseconds...");
				panel.setTimerCount(timerCount);

			} catch (Exception e) {

			}
		}
		String fontSizeStr = System.getProperty("fontsize");
		if (fontSizeStr != null) {
			try {
				int size = Integer.valueOf(fontSizeStr).intValue();
				System.out.println("Font size = " + size);
				panel.setFontSize(size);

			} catch (Exception e) {

			}
		}
		Usage();
		if (!bFullscreen) {
		    dlg.setSize(600, 600);
		    dlg.setVisible(true);
		}
		dlg.setResizable(false);
   }
}

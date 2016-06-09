
package com.vmware.viclient.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import java.util.HashMap;

import java.awt.Point;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.util.*;

public class PropertiesPanel extends JPanel {

	private HashMap propMap = null;

	public PropertiesPanel(HashMap map) {
             propMap = map;
	     init();
 	}

	public void init() {
	     setLayout(new SpringLayout());
             int size = propMap.size();
	     
	     final Object [] keys = propMap.keySet().toArray();
	     final Object [] values = propMap.values().toArray();
	     JLabel [] keyLabels = new JLabel[size+1];
	     JLabel [] valueLabels = new JLabel[size+1];
	     for (int i=0; i<size; i++) {
                  keyLabels[i] = new JLabel((String)keys[i]);
		  if (values[i] instanceof Vector) {
		      final Object val = values[i];
                      valueLabels[i] = new JLabel("<html><font color=\"blue\">" + "Click here for details" + " </font></html>");
		      valueLabels[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		      final JLabel lbl = valueLabels[i];
		      valueLabels[i].addMouseListener(new MouseAdapter() {
			      public void mouseClicked(MouseEvent e) {
                                  Point pt = e.getPoint();
				  SwingUtilities.convertPointToScreen(pt, lbl);
				  ToolTipTree w = ToolTipTree.getInstance();
				  w.showToolTip(val, pt);
			      }
		      });
		  } else  {
                      valueLabels[i] = new JLabel("<html><font color=\"green\">" + values[i].toString() + " </font></html>");
		  }
		  add(keyLabels[i]);
		  add(valueLabels[i]);
	     }
	     SpringUtilities.makeCompactGrid(this, size, 2, 50, 5, 15, 6);

	}

	public static void main(String [] arg) {
             JFrame f = new JFrame("Testing...");
	     HashMap map = new HashMap();
	     map.put("1", "100");
	     map.put("2", "200");
	     Vector v = new Vector();
	     v.addElement("First");
	     v.addElement("Second");
	     v.addElement("Third");
	     map.put("3", v);
	     f.setContentPane(new PropertiesPanel(map));
	     f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     f.setSize(500, 500);
	     f.setVisible(true);
	}
}

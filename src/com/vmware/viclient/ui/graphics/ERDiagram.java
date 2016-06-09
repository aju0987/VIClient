package com.vmware.viclient.ui.graphics;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class ERDiagram extends JFrame {

	ManagedObject root = null;
	static ERDiagram erd = null;
	ManagedObjectManager mgr = ManagedObjectManager.getInstance();

	public ERDiagram() {
             setTitle("Entity-Relationship Diagram (Kannan)");
	     init(null, 2);
	     erd = this;
	     ERPanel panel = new ERPanel(root);
	     JScrollPane scrollPane = new JScrollPane(panel);
	     panel.setPreferredSize(new Dimension(mgr.drawingAreaX, mgr.drawingAreaY));
             setContentPane(scrollPane);
	     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     setSize(500, 500);
	     setVisible(true);
	}

	public void init(ManagedObject parent, int count) {
	     if (root == null) {
	         root = new ManagedObject("Folder", new String[] {"One", "Two"});
	         ManagedObjectManager.getInstance().add(root);
	     }
	     if (count == 0) return;
	     --count;
	     for (int i=0; i<5; i++) {
                  ManagedObject child = new ManagedObject("Entity Relationship Test " +i, new String[] {"Prop"+i, "Prop"+(i+1) } );
		  if (parent == null) {
		      root.addChild(child);
		  } else {
		      parent.addChild(child);
		  }
		  init (child, count);
	     }
	}

	public static void main (String [] args) {
		new ERDiagram();
             
	}
}




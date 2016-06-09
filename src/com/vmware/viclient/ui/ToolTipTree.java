
/*
 * Summary: Customized class for displaying a list of values as a tool tip
 * Author: Kannan Balasubramanian
 * Creation Date: 10-Aug-2012
 * Last update date: 10-Aug-2012
 */
package com.vmware.viclient.ui;

import javax.swing.JWindow;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;
import java.awt.Color;
import javax.swing.JFrame;

public class ToolTipTree extends JWindow {

   private static ToolTipTree toolTip = null;
   private ComponentTreePanel panel = new ComponentTreePanel();
   private static JFrame dummyFrame = new JFrame();
   private ToolTipTree () {
        super(dummyFrame);
	dummyFrame.setLocation(-100,-100);
	dummyFrame.setVisible(true);
        setContentPane(new JScrollPane(panel));
        this.setSize(250, 150);
   }

   public Component getFocusOwner() {
        if (panel != null && panel.getTree() != null) {
            return panel.getTree();
	}	
	return super.getFocusOwner();
   }

   //Making the class as a singleton
   public static ToolTipTree getInstance() {
       if (toolTip == null) {
          toolTip = new ToolTipTree();
       }
       return toolTip;
   }

   public void showToolTip(Object comp, Point pt) {

        final Object c = comp;
        final Point p = pt;

        //Make sure the window is shown on EDT
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
	       if (c instanceof Vector) {
                   ToolTipTree.this.panel.showList((Vector)c);
	       } else {
                   ToolTipTree.this.panel.showComponent(c);
	       }
               //toolTip.pack();
               toolTip.setLocation(p.x, p.y);
               toolTip.setVisible(true);
           }
        });

        //Hide the window after 2000 ms
        Thread t = new Thread(new Runnable() {
             public void run() {
                try {
                    Thread.sleep(2000);
                    toolTip.setVisible(false);
                } catch (Exception e) {

                }
             }
        });
        //t.start();
   }

   //Just a test method to run it as a standalone
   public static void main(String [] args) {
        ToolTipTree test = ToolTipTree.getInstance();
        test.showToolTip(new JTree(), new Point(50, 50));
   }
}

/*
 * ComponentTreePanel: Class for displaying component hierarchy in a JTree 
 */
class ComponentTreePanel extends JPanel {

   private JTree tree = null;
   private Object prevObj = null;

   public void showComponent(Object obj) {
      if (tree == null) {
          tree = new JTree();
          tree.setShowsRootHandles(false);
          tree.setEditable(false);
          add(tree);
      }
      if (obj == null || obj == prevObj) {
          return;
      }
      prevObj = obj;
      Class cl = obj.getClass();
      String tabStr = " ";
      int rowSize = 1;
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
      root.removeAllChildren();
      root = new DefaultMutableTreeNode(cl.toString());
      while ((cl = cl.getSuperclass()) != null) {
         root.add(new DefaultMutableTreeNode(tabStr + cl.toString()));
         tabStr = tabStr + "  ";
         rowSize++;
      }
      tree.setModel(new DefaultTreeModel(root));
      tree.setVisibleRowCount(rowSize);
      tree.requestFocus();
   }

   public JTree getTree() {
      return tree;
   }

   public void showList(Vector v) {
      if (tree == null) {
          tree = new JTree();
          tree.setShowsRootHandles(false);
          tree.setEditable(false);
          add(tree);
      }
      if (v == null || v.size() == 0 || v == prevObj) {
          return;
      }
      prevObj = v;
      int rowSize = 1;
      int size = v.size();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
      root.removeAllChildren();
      root = new DefaultMutableTreeNode("");
      for (int i=0; i<size; i++) {
         root.add(new DefaultMutableTreeNode((String)v.elementAt(i)));
         rowSize++;
      }
      tree.setModel(new DefaultTreeModel(root));
      tree.setVisibleRowCount(rowSize);
   }
}

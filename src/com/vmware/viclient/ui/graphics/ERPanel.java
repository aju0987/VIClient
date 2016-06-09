package com.vmware.viclient.ui.graphics;

import com.vmware.viclient.ui.fx.LaunchPopup;
import com.vmware.viclient.ui.fx.VcenterUI;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.PopupMenu;
import java.awt.MenuItem;

import java.awt.image.VolatileImage;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.util.Vector;

import javafx.application.Platform;

public class ERPanel extends JPanel {

        private ManagedObject mobj = null;
	ManagedObjectManager mgr = ManagedObjectManager.getInstance();
	int startX = 0;
	int startY = 0;
	ManagedObject selObj = null;
	Vector <ManagedObject> selObjects = new Vector<ManagedObject> ();
	Cursor defaultCursor = Cursor.getDefaultCursor();
	Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	Color bkColor = Color.BLACK;
        HighlightThread ht = null;
	SystemTrayUtility trayUtility = null;
	Image offscreenImage = null;
	Graphics2D offscreenG = null;
	String searchEntityName = "";

	public ERPanel(ManagedObject obj) {
            mobj = obj;
            setBackground(bkColor);

	    final JPopupMenu menu = new JPopupMenu();
	    JMenuItem item1 = new JMenuItem("Background color");
	    item1.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
                       JColorChooser chooser = new JColorChooser();
		       Color c = chooser.showDialog(ERPanel.this, "Choose Background Color", bkColor);
		       if (c != null) {
			   bkColor = c;
                           ERPanel.this.setBackground(bkColor);
		       }
		    }
	    });
	    JMenuItem item2 = new JMenuItem("Entity title color");
	    item2.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
                       JColorChooser chooser = new JColorChooser();
		       Color c = chooser.showDialog(ERPanel.this, "Choose Entity Title Color", ManagedObject.titleColor);
		       if (c != null) {
			   ManagedObject.titleColor = c;
			   ERPanel.this.repaint();
		       }
		    }
	    });
	    JMenuItem item3 = new JMenuItem("Entity property color");
	    item3.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
                       JColorChooser chooser = new JColorChooser();
		       Color c = chooser.showDialog(ERPanel.this, "Choose Entity Property Color", ManagedObject.propColor);
		       if (c != null) {
			   ManagedObject.propColor = c;
			   ERPanel.this.repaint();
		       }
		    }
	    });
	    JMenuItem item4 = new JMenuItem("Node color");
	    item4.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
                       JColorChooser chooser = new JColorChooser();
		       Color c = chooser.showDialog(ERPanel.this, "Choose Node Color", Node.lineColor);
		       if (c != null) {
			   Node.lineColor = c;
			   ERPanel.this.repaint();
		       }
		    }
	    });
	    JMenuItem item5 = new JMenuItem("Reset Diagram");
	    item5.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
		    }
	    });
	    final JCheckBoxMenuItem item6 = new JCheckBoxMenuItem("3d Diagram");
	    item6.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
		       ManagedObject.b3d = item6.getState();
		       repaint();
		    }
	    });
	    JMenuItem item7 = new JMenuItem("Find Object");
	    item7.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
	    item7.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
                       searchEntityName = (String)JOptionPane.showInputDialog(ERPanel.this, "Enter the name of entity you would like to search!", "Find Dialog", JOptionPane.QUESTION_MESSAGE, null, null, searchEntityName);
		       if (searchEntityName == null || searchEntityName.length() == 0) {
                           return;
		       }
		       ManagedObject obj = mgr.findObject(mgr.getRootObject(), searchEntityName);
		       if (obj == null){
                           JOptionPane.showMessageDialog(ERPanel.this, "Entity " + searchEntityName + " not found !");
			   return;
		       }
		       Rectangle rect = mgr.getRect(obj);
                       scrollRectToVisible(rect);
		       highlight(obj);
		    }
	    });
	    final JCheckBoxMenuItem item8 = new JCheckBoxMenuItem("Direct Node");
	    item8.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
                       menu.setVisible(false);
		       Node.bDirectLine = item8.getState();
		       repaint();
		    }
	    });
	    final JMenuItem item9 = new JCheckBoxMenuItem("Add New VM");
	    item9.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			   EREvent event = new EREvent();

			   mgr.fireStateChanged(event);
		    }
	    });

	    menu.add(item1);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item2);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item3);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item4);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item6);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item7);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item8);
	    menu.add(new JPopupMenu.Separator());
	    menu.add(item9);

	    addKeyListener(new KeyAdapter() {
		    public void keyPressed(KeyEvent e) {
                       if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			   deSelectAll();
			   ERPanel.this.repaint();
		       }
		    }
	    });

            final LaunchPopup popup = new LaunchPopup("");
	    addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { 
		      requestFocus();
		      Platform.runLater(new Runnable() {
			      public void run() {
		                  VcenterUI ui = VcenterUI.getInstance();
		                  if (ui.primaryStage != null) {
		                      ui.primaryStage.hide();
		                  }
		              }
		      });
		      if (e.getClickCount() == 2) {
	                  ManagedObject sobj = mgr.hitTest(e.getX(), e.getY());
			  if (sobj != null) {
			      popup.invokePopup(popup.getProps(), e.getX() + 20, e.getY());
			  }
                          return;
		      }
                      Dimension dim = new Dimension(mgr.drawingAreaX, mgr.drawingAreaY);
                      setPreferredSize(dim);
		      revalidate();
                      repaint();

		      if (e.getButton() == e.BUTTON3) {
	                  ManagedObject sobj = mgr.hitTest(e.getX(), e.getY());
			  if (sobj == null) {
			      menu.show(ERPanel.this, e.getX(), e.getY());
			  }
		      } else {
			  menu.setVisible(false);
		      }
		      if (e.isControlDown()) {
	                  ManagedObject sobj = mgr.hitTest(e.getX(), e.getY());
			  if (sobj != null ) {
			      if (selObjects.contains(sobj)) {
                                  sobj.bSelected = false;
                                  selObjects.remove(sobj);
		              } else { 
                                  sobj.bSelected = true;
                                  selObjects.add(sobj);
			      }
			  }
			  repaint();
		      }

		}

                public void mousePressed(MouseEvent e) { 
		   startX = e.getX();
		   startY = e.getY();
		   if (e.isShiftDown() && e.isControlDown()) {
                       return;
		   }
		   if (! e.isControlDown()) {
                       deSelectAll();
		   }
		   if (! SwingUtilities.isLeftMouseButton(e)) {
                        return;
		   }
	           selObj = mgr.hitTest(startX,startY);
		   if (selObj != null) {
                       setCursor(handCursor);
		       selObj.bSelected = true;
		       if (e.isShiftDown()) {
			   selectAllChildren(selObj, true);
		       }
                       repaint();
		   }
		}

                public void mouseReleased(MouseEvent e) { 
		   startX = 0;
		   startY = 0;
		   if (e.isControlDown()) return;

		   if (selObj != null) {
                       setCursor(defaultCursor);
		       mgr.updateDrawingArea(selObj);
		       //if (e.isShiftDown()) {
			   selectAllChildren(selObj, false);
		       //} 
		       selObj.bSelected = false;
                       Dimension dim = new Dimension(mgr.drawingAreaX, mgr.drawingAreaY);
                       setPreferredSize(dim);
                       revalidate();
                       repaint();
		   }
	           selObj = null;
		}
	    });
	    addMouseMotionListener(new MouseMotionAdapter() {


                public void mouseDragged(MouseEvent e) { 
		     int x = e.getX();
		     int y = e.getY();
		     if (! SwingUtilities.isLeftMouseButton(e)) {
                         return;
		     }
		     if (selObj == null) return;
	             Rectangle rect = mgr.getRect(selObj);
		     int deltax = x - startX;
		     int deltay = y - startY;
		     startX = x;
		     startY = y;
		     rect.x += deltax;
		     rect.y += deltay;

		     if (e.isControlDown()) {
                         moveSelectedObjects(selObjects, deltax, deltay);
		     }

		     if (selObj.parent != null) {
                         Node node = selObj.parent.getNode(selObj);
			 if (node != null) {
                             node.destPt.x = rect.x + rect.width;
                             node.destPt.y = rect.y + rect.height/2;
			 }
		     }

                     Vector<ManagedObject> children = selObj.getChildren();
		     if (children == null || children.size() == 0) {
                         mgr.updateDrawingArea(selObj);
                         Dimension dim = new Dimension(mgr.drawingAreaX, mgr.drawingAreaY);
                         ERPanel.this.setPreferredSize(dim);
                         ERPanel.this.revalidate();
			 ERPanel.this.scrollRectToVisible(rect);
                         ERPanel.this.repaint();
		         return;
		     }
		     Rectangle childRect = null;
		     for (ManagedObject child : children) {
                          Node node = selObj.getNode(child);
			  if (node != null) {
                              node.srcPt.x = rect.x + rect.width/2;
                              node.srcPt.y = rect.y + rect.height;
			  }
			  if (e.isShiftDown()) {
			      childRect = mgr.getRect(child);
		              childRect.x += deltax;
		              childRect.y += deltay;
			      node.destPt.x = childRect.x + childRect.width;
			      node.destPt.y = childRect.y + childRect.height/2;
			      moveChildren(child, deltax, deltay);
			  }
		     }
                     mgr.updateDrawingArea(selObj);
                     Dimension dim = new Dimension(mgr.drawingAreaX, mgr.drawingAreaY);
                     ERPanel.this.setPreferredSize(dim);
                     ERPanel.this.revalidate();
                     ERPanel.this.scrollRectToVisible(rect);
                     ERPanel.this.repaint();
		}

		public void moveChildren(ManagedObject mObj, int deltax, int deltay) {
                     Vector<ManagedObject> children = mObj.getChildren();
		     if (children == null || children.size() == 0) return;
		     for (ManagedObject child : children) {
                          Node node = mObj.getNode(child);
			  Rectangle rect = mgr.getRect(mObj);
			  if (node != null) {
                              node.srcPt.x = rect.x + rect.width/2;
                              node.srcPt.y = rect.y + rect.height;
			  }
                          rect = mgr.getRect(child);
		          rect.x += deltax;
		          rect.y += deltay;
			  if (node != null) {
			      node.destPt.x = rect.x + rect.width;
			      node.destPt.y = rect.y + rect.height/2;
			  }
                          mgr.updateDrawingArea(child);
			  moveChildren(child, deltax, deltay);
		     }
		}

		public void moveSelectedObjects(Vector<ManagedObject> objs, int deltax, int deltay) {
                     if (objs == null || objs.size() == 0) {
                         return;
		     }
		     for (ManagedObject obj : objs) {
                          Rectangle rect = mgr.getRect(obj);
		          rect.x += deltax;
		          rect.y += deltay;
			  if (obj.parent != null) {
                              Node node = obj.parent.getNode(obj);
			      node.destPt.x = rect.x + rect.width;
			      node.destPt.y = rect.y + rect.height/2;
			  }
		     }
		}

                public void mouseMoved(MouseEvent e) { 
	             ManagedObject obj = mgr.hitTest(e.getX(),e.getY());
		     if (obj != null) {
                         ERPanel.this.setCursor(handCursor);
			 setToolTipText(obj.getName());
		     } else {
                         ERPanel.this.setCursor(defaultCursor);
		     }
		}

	    });

            String url = "http://i1-news.softpedia-static.com/images/news2/VMware-Launches-vSphere-Client-for-iPad-Free-App-2.png";
	    PopupMenu pmenu = new PopupMenu();
	    MenuItem mitem1 = new MenuItem("About");
	    pmenu.add(mitem1);

	    trayUtility = new SystemTrayUtility("VI Client", url, pmenu);
	    mgr.addListener(new ERListener() {
                public void stateChanged(EREvent e){
		    ManagedObject root = mgr.getRootObject();
		    ManagedObject child = new ManagedObject();
		    child.setName("New VM!");
		    child.setType("Virtual Machine");
		    root.addChild(child);
		    repaint();
		    highlight(child);
                    trayUtility.showInfo("Alert", "New VM Added");
		}
	    });

	    requestFocus();
	}

	public void selectAllChildren(ManagedObject mobj, boolean flag) {
            Vector <ManagedObject> children = mobj.getChildren();
	    if (children != null && children.size() != 0) {
                for (ManagedObject child : children) {
                     mgr.updateDrawingArea(child);
	             child.bSelected = flag;
		     selectAllChildren(child, flag);
                }
	    }
	}

	public void deSelectAll() {
	   for (ManagedObject obj : selObjects) {
                obj.bSelected = false;
	   }
           selObjects.removeAllElements();
	   if (selObj != null) {
               selObj.bSelected = false;
	       selObj = null;
	   }
	}

	public boolean isManagedObjectVisible(ManagedObject obj) {
           Rectangle vRect = getVisibleRect();
	   Rectangle oRect = mgr.getRect(obj);
	   if (oRect == null) return false;
	   return vRect.contains(oRect);
	}

	public void highlight(ManagedObject mobj) {
            if (mobj == null) return;
	    final ManagedObject obj = mobj;
            Rectangle rect = mgr.getRect(obj);
            scrollRectToVisible(rect);
	    if (ht == null) {
                ht = new HighlightThread ();
		ht.setPanel(ERPanel.this);
		ht.setManagedObject(mobj);
	        ht.start();
	    } else {
		ht.setManagedObject(mobj);
                ht.wakeUpThread();
	    }
	}

	public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
	    if (false) {
	    if (offscreenImage == null) {
                offscreenImage = createVolatileImage(mgr.drawingAreaX, mgr.drawingAreaY);
	        offscreenG = ((VolatileImage)offscreenImage).createGraphics();
	    }
	    if (offscreenImage.getWidth(null) < mgr.drawingAreaX || offscreenImage.getHeight(null) < mgr.drawingAreaY) {
                offscreenImage = createVolatileImage(mgr.drawingAreaX, mgr.drawingAreaY);
	        offscreenG = ((VolatileImage)offscreenImage).createGraphics();

	    }
	    }
	    drawManagedObjects(mobj, g2d);
	    //super.paintComponent(offscreenG);
	    //drawManagedObjects(mobj, offscreenG);
	    //g2d.drawImage(offscreenImage, null, null);
	}

	public void drawManagedObjects(ManagedObject root, Graphics2D g) {
            if (root == null) return;
            g.setColor(Color.blue);
	    ManagedObjectManager mgr = ManagedObjectManager.getInstance();
	    Rectangle rect = mgr.getRect(root);
	    if (rect == null)return;
	    root.draw(rect, g);
	    Vector<ManagedObject> children = root.getChildren();
	    if (children == null || children.size() == 0) return;
	    for (ManagedObject child : children) {
                 rect = mgr.getRect(child);
		 if (rect == null) continue;
		 Node node = root.getNode(child);
		 if (child != null) {
                     node.draw(g);
		 }
		 drawManagedObjects(child, g);
	    }
	}
}

class HighlightThread extends Thread {

     Object htLock = new Object();
     ManagedObject mobj = null;
     JPanel panel = null;
     int DELAY = 100;

     public HighlightThread() {
        setDaemon(true);
     }

     public void setPanel(JPanel p) {
        panel = p;
     }

     public void setManagedObject(ManagedObject obj) {
        mobj = obj;
     }

     public void run() {
        while(true) {
              try {
	            for (int i=0; i<8; i++) {
	                 mobj.setHighlight(true);
	                 panel.repaint();
                         Thread.sleep(DELAY);
	                 mobj.setHighlight(false);
	                 panel.repaint();
                         Thread.sleep(DELAY);
		    }
		    synchronized(htLock) {
                        htLock.wait();  //suspend thread
		    }
              } catch (Exception e) {

	      }
        }
     }

     public void wakeUpThread() {
	synchronized(htLock) {
            htLock.notifyAll();  //resume thread
	}
     }
}


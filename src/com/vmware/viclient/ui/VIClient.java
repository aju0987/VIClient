
/*
 * File: VIClient.java
 * Author: Kannan Balasubramanian
 * Creation Date: 03-Aug-2012
 * Last Updated Date: 06-Aug-2012
 */

package com.vmware.viclient.ui;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.viclient.managedentities.ManagedEntities;
import com.vmware.viclient.ui.graphics.ManagedObjectManager;
import com.vmware.viclient.ui.graphics.ManagedObject;
import com.vmware.viclient.ui.graphics.ERPanel;

import com.vmware.viclient.managedentities.PerformanceMetrics; //kannan-test

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import java.util.Vector;

public class VIClient extends JFrame {

	private ConnectionManager cMgr = ConnectionManager.getInstance();
	private JScrollPane scrollPane = null;
	private JTabbedPane tabPane = null;
	private PerformancePanel perfPanel = null;
	private ERPanel erPanel = null;
	
    private ManagedEntities mgrEnt = null;
	public VIClient(String serverName) {

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem item1 = new JMenuItem("Reconnect");
		item1.setMnemonic('C');
		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		
		mgrEnt = new ManagedEntities();
		
		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int retval = JOptionPane.showConfirmDialog(VIClient.this, "Are you sure you want to reconnect ?", "Reconnect", JOptionPane.YES_NO_OPTION);
				if (retval == JOptionPane.YES_OPTION) {
				    System.err.println("reconnecting ...");
				    cMgr.reconnect();
				    resetAll();
				}
			}
		});
		fileMenu.add(item1);

		JMenuItem item2 = new JMenuItem("Exit");
		item2.setMnemonic('E');
		item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VIClient.this.dispose();
			}
		});
		fileMenu.add(item2);

		JMenuItem hitem1 = new JMenuItem("About");
		hitem1.setMnemonic('A');
		hitem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		hitem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			     final JDialog dlg = new JDialog(VIClient.this, "VI Client");
		             dlg.setContentPane(new AboutPanel(dlg));
		             dlg.addWindowListener(new WindowAdapter() {
                                 public void windowClosing(WindowEvent e) {
				       dlg.dispose();
				 }
			     });
			     dlg.setSize(600, 600);
			     dlg.setResizable(false);
			     dlg.setVisible(true);
			}
		});
		helpMenu.add(hitem1);

		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		tabPane = new JTabbedPane();
		//System.out.println("is connected: " + cMgr.isServerConnectionAvailable());
		if (cMgr.isServerConnectionAvailable()) {
System.err.println("connection avaialble.....:");
		    CoreModulePanel cPanel = new CoreModulePanel();
		    tabPane.addTab("Core Modules", cPanel);
		    tabPane.setToolTipTextAt(0, "Select a host on Tree Panel to view this");
		    tabPane.addTab("VirtualDevices", new CustomTablePanel());
		    tabPane.setToolTipTextAt(1, "Select a virtual machine on Tree Panel to view this");
		    cPanel = new CoreModulePanel();
		    cPanel.updateCategories(CoreModulePanel.datastoreList);
		    tabPane.addTab("Datastore", cPanel);
		    tabPane.setToolTipTextAt(2, "Select a datastore on Tree Panel to view this");
		    perfPanel = new PerformancePanel();
                    tabPane.addTab("PerformanceManager", perfPanel);
		    tabPane.setToolTipTextAt(3, "Performance Metrics");

		    Thread t = new Thread(new Runnable() {
			    public void run() {
                                ManagedObject root = mgrEnt.loadInventory();
		                ManagedObjectManager mgr = ManagedObjectManager.getInstance();
                                Dimension dim = new Dimension(mgr.drawingAreaX, mgr.drawingAreaY);
		                erPanel = new ERPanel(root);
                                erPanel.setPreferredSize(dim);
                                tabPane.addTab("Inventory", new JScrollPane(erPanel));
		                tabPane.setToolTipTextAt(4, "Graphical form of inventory"); 
		             }
                    });
		    t.start();
		}

		TreePanel sPanel = TreePanel.getInstance();
		sPanel.setParentContainer(this);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(sPanel), tabPane);
		Vector nodes = new Vector();
		//String [] entities = cMgr.getAllManagedEntities();
		 
		 String [] entities = mgrEnt.getAllManagedEntities();
		if (entities != null) {
		    for (int i=0; i<entities.length; i++) {
		         nodes.addElement(entities[i]);
		    }
		}
		sPanel.refresh(nodes);  //comment from kannan
		add(splitPane, BorderLayout.CENTER);

		VIClientEventHandler.getInstance().addListener(
				new VIClientListener() {
                        public void eventUpdate(Object source, VIEvent event) {
                             if (source instanceof ResourceTree) {
				     Object entity = event.getEntity();
				 if (entity instanceof ResourceTreeNode) {
				     Object type = ((ResourceTreeNode)entity).getEntity();
				     //if (type instanceof com.vmware.vim25.mo.ManagedEntity) {  //commentedK
				         ConnectionManager.getInstance().setManagedEntity((com.vmware.vim25.ManagedObjectReference)type);
					 //perfPanel.refresh();
				     //}
                                     if ( type.equals("VirtualMachine")) {
					     tabPane.setSelectedIndex(1);
				     } else if (type.equals("HostSystem")) {
					     tabPane.setSelectedIndex(0);
				     } else if (type.equals("Datastore")) {
					     tabPane.setSelectedIndex(2);
				     }
			          }
			     }
			}
		});

		if (serverName == null || serverName.trim().length() == 0){
			serverName = "";
		}
		setTitle("VI Client 1.0 [ " + serverName + " ]   [" + cMgr.getServerType() + "]");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			   Window [] windows = Window.getWindows();
			   for (int i=0; i<windows.length; i++) {
				   if (windows[i] != VIClient.this) {
					   windows[i].dispose();
				   }
			   }
			}
			public void windowClosed(WindowEvent e) {

			}
		});
		pack();
		setVisible(true);
	}

	public void updateHostTitleOnTab(String hName) {
                tabPane.setTitleAt(0, "Core Modules - " + hName);
	}

	public void resetAll() {
		if (cMgr.isServerConnectionAvailable()) {
		    tabPane.removeAll();
		    tabPane.addTab("Core Modules", new CoreModulePanel());
		}
		Vector nodes = new Vector();
		String [] entities = mgrEnt.getAllManagedEntities();
		if (entities != null) {
		    for (int i=0; i<entities.length; i++) {
		         nodes.addElement(entities[i]);
		    }
		}
		TreePanel sPanel = TreePanel.getInstance();
		sPanel.refresh(nodes);
	}
}

class VIClientEventHandler {

	private static VIClientEventHandler eventHandler = null;
	private Vector<VIClientListener> listeners = new Vector<VIClientListener>();

	private VIClientEventHandler() {

	}

	public static VIClientEventHandler getInstance() {
             if (eventHandler == null) {
                 eventHandler = new VIClientEventHandler();
	     }
	     return eventHandler;
	}

        public void addListener(VIClientListener l) {
             listeners.add(l);
	}

        public void removeListener(VIClientListener l) {
             listeners.remove(l);
	}

        public void notifyListeners(Object source, VIEvent event) {
             int size = listeners.size();
	     for (int i=0; i<size; i++) {
                  listeners.elementAt(i).eventUpdate(source, event);
	     }
	}
}

interface VIClientListener {
	public void eventUpdate(Object source, VIEvent event);
}

class VIEvent {
    private Object type = null;
    private Object entity = null;

    public VIEvent(Object t, Object e) {
         type = t;
         entity = e;
    }

    public Object getType() {
         return type;
    }

    public Object getEntity() {
         return entity;
    }

    public String toString() {
         return entity.toString();
    }
}



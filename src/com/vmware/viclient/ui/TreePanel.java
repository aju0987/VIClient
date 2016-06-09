
/*
 * Summary: Tree displaying component hierarchy in the targetted application
 * Author: Kannan Balasubramanian
 * Creation Date: 06-Aug-2012
 * Last updated date: 08-Aug-2012
 */

package com.vmware.viclient.ui;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.AWTEventListener;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.ToolTipManager;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import java.lang.reflect.Method;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.HostSystemPowerState;
import com.vmware.vim25.HostRuntimeInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.ManagedObjectReference;

import com.vmware.viclient.helper.Utilities;
import com.vmware.viclient.helper.VimUtil;

public class TreePanel extends JPanel {
    
    private JTree tree = null;
    private Vector<String> v = new Vector<String>();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Managed Entities");
    int rowIndex = 0;
    int rowCount = 0;
    boolean bTreeExpandAll = true;
    private static TreePanel treePanel = new TreePanel();
    private JScrollPane scrollpane = null;
    private VIClient parentContainer = null;
    private VimUtil vimUtil = null;
    
    private TreePanel() {
        root.setUserObject(new ResourceTreeNode(null, "Managed Entities"));
	vimUtil = ConnectionManager.getInstance().getVimUtil();
        tree = new ResourceTree(root, this);
        scrollpane = new JScrollPane(tree);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(tree);
    }
    
    //Make class as a singleton
    public static TreePanel getInstance() {
        if (treePanel == null) {
            treePanel = new TreePanel();
        }
        return treePanel;
    }

    public void setParentContainer(VIClient p) {
        parentContainer = p;
    }

    public VIClient getParentContainer() {
        return parentContainer;
    }

    //Update tree's vector containing Window objects
    public void setVector(Vector<String> vector) {
        v = vector;
    }

    //Refresh tree with the vector containing Window objects
    public void refresh(Vector<String> vector) {
        v = vector;
        refresh();
    }
    
    //Set this flag if the tree needs to be in expanded mode by default
    public void setExpandAll(boolean bFlag) {
        bTreeExpandAll = bFlag;
    }

    public boolean isTreeExpanded() {
        return bTreeExpandAll;
    }
    
    //Update tree font with the font selected using FontDialog UI
    public void setFont(Font f){
       super.setFont(f);
       if (tree != null) {
           tree.setFont(f);
       }
    }
    
    public JComponent getPane() {
       return scrollpane;
    }

    //Refresh tree with the vector containing entity objects
    public void refresh() {
        root.removeAllChildren();
        rowCount = 1;
        for (String node : v) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
            childNode.setUserObject(node);
            addChildNodes(node, childNode);
            root.add(childNode);
            rowCount++;
        }
        try {
            tree.updateUI();
        } catch (Exception e) {

        }
        if (bTreeExpandAll) {
            int row = 0;
            while (row < rowCount) {
                tree.expandRow(row);
                row++;
            }
        }
    }
    
    public DefaultMutableTreeNode addChildNodes(String mentity, DefaultMutableTreeNode node) {
	ManagedObjectReference [] entities = null;
        try{
	    entities = vimUtil.getManagedEntities(mentity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
	}
        for (ManagedObjectReference entity : entities) {
            String entityName = "";
	    if (mentity.equals("ResourcePool")) {
                entityName = entity.getValue();
	    } else {
                entityName = (String)vimUtil.getProperty(entity, "name");
	    }
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(entityName);
	    ResourceTreeNode entityNode = new ResourceTreeNode(entity, entityName);
            childNode.setUserObject(entityNode);
            if (node != null) {
                node.add(childNode);
                rowCount++;
            }

            /*if (entities[i] instanceof ManagedEntity) {
                addChildNodes(entityNode, childNode);
            }*/
        }
        node.setUserObject(node.getUserObject() + " (" + entities.length + ")");
        return node;
    }
   
    //Expand tree until the requested Component is made visible and scroll to it
    public void showComponentNode(ManagedObjectReference entity) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)tree.getModel().getRoot();
        rowIndex = -1;
        boolean bNodeFound = showComponentNode(entity, rootNode);
        if (!bNodeFound) {
            JOptionPane.showMessageDialog(this, "Search not found!  Please refresh Component Tree and try again!");
            return;
        }
        validate();
    }
    
    //Expand tree until the requested Component is made visible and scroll to it
    public boolean showComponentNode(ManagedObjectReference entity, DefaultMutableTreeNode node) {
        rowIndex++;
        ResourceTreeNode searchNode = (ResourceTreeNode)node.getUserObject();
        if (searchNode.getEntity() == entity) {
            int row = 0;
            while (row < rowIndex) {
                tree.expandRow(row);
                row++;
            }
            tree.scrollRowToVisible(rowIndex);
            tree.setSelectionRow(rowIndex);
            return true;
        }
        int count = node.getChildCount();
        DefaultMutableTreeNode childNode = null;
        for (int i=0; i<count; i++) {
            childNode = (DefaultMutableTreeNode)node.getChildAt(i);
            if (showComponentNode(entity, childNode)) {
                return true;
            }
        }
        return false;
    }

    //Return the currently selected component in the tree
    public Object getCurrentSelectedComponent() {
           DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
           if (currentNode == null) {
               return null;
           }
           ResourceTreeNode node = (ResourceTreeNode)currentNode.getUserObject();
           if (node == null) {
               return null;
           }
           return node.getEntity();
    }
}

class ResourceTree extends JTree {
    
    private JPopupMenu menu = null;
    private TreePanel parentPanel = null;
    private DefaultMutableTreeNode currentNode = null;
    private static AWTEventListener eventListener = null;
    private VimUtil vimUtil = null;
    
    public ResourceTree(TreeNode node, TreePanel panel) {
        super(node);
	vimUtil = ConnectionManager.getInstance().getVimUtil();
        parentPanel = panel;
	createPopupMenu();
        addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                int rowsel = ResourceTree.this.getRowForLocation(e.getX(), e.getY());
                if (rowsel != -1) {
                    if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                        ResourceTree.this.setSelectionRow(rowsel);
                        currentNode = (DefaultMutableTreeNode)ResourceTree.this.getLastSelectedPathComponent();
                        ResourceTree.this.menu.show(ResourceTree.this, e.getX(), e.getY());
                    }
                }
            }
        });
	addTreeSelectionListener(new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
                     DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)getLastSelectedPathComponent();
		     if (currentNode != null && currentNode.getParent() != null) {
                         DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)currentNode.getParent();
			 Object userObj = parentNode.getUserObject();
			 if (userObj != null && userObj instanceof String && ((String)userObj).startsWith("HostSystem")) {
			     ResourceTreeNode node = (ResourceTreeNode)currentNode.getUserObject();
                             ConnectionManager.getInstance().setEsxHost(node.getDetailedString());
			     parentPanel.getParentContainer().updateHostTitleOnTab(node.getDetailedString());
			 }
		     }
		     if (currentNode != null && currentNode.getUserObject()!= null) {
		         VIClientEventHandler.getInstance().notifyListeners(ResourceTree.this, new VIEvent(null, currentNode.getUserObject()));
		     }
		}
	});
	setCellRenderer(new DefaultTreeCellRenderer() {
                  public Component getTreeCellRendererComponent(JTree pTree,
	                  Object pValue, boolean pIsSelected, boolean pIsExpanded, boolean pIsLeaf, int pRow, boolean pHasFocus)
	              {
                         DefaultMutableTreeNode node = (DefaultMutableTreeNode)pValue;
			 setTextNonSelectionColor(Color.black);
			 Object tNode = node.getUserObject();
			 if (tNode instanceof ResourceTreeNode) {
                             ManagedObjectReference entity = ((ResourceTreeNode)tNode).getEntity();
			     setNodeColor(entity, this);
			 }
                         return super.getTreeCellRendererComponent(pTree, pValue, pIsSelected, pIsExpanded, pIsLeaf, pRow, pHasFocus);
		      }
	});
	ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void setNodeColor(ManagedObjectReference entity, DefaultTreeCellRenderer renderer) {
        if (entity != null && entity.getType().equals("HostSystem")) {
	    HostRuntimeInfo runtime = (HostRuntimeInfo)vimUtil.getProperty(entity, "runtime");
	    if (runtime == null) return;
            HostSystemPowerState state = runtime.getPowerState();
	    if (state != null) {
	        if (state == state.POWERED_OFF || state == state.UNKNOWN ) {
                    renderer.setTextNonSelectionColor(Color.red);
	        } else if (state == state.STAND_BY) {
                    renderer.setTextNonSelectionColor(Color.yellow);
	        } else {
                    renderer.setTextNonSelectionColor(Color.green);
	        }
            }

        } else if (entity != null && entity.getType().equals("VirtualMachine")) {
            try {
	         VirtualMachineRuntimeInfo runtime = (VirtualMachineRuntimeInfo)vimUtil.getProperty(entity, "runtime");
                 VirtualMachinePowerState state = runtime.getPowerState();
		 if (state != null) {
		     if (state == state.POWERED_OFF) {
                         renderer.setTextNonSelectionColor(Color.red);
		     } else if (state == state.SUSPENDED) {
                         renderer.setTextNonSelectionColor(Color.yellow);
		     } else {
                         renderer.setTextNonSelectionColor(Color.green);
		     }
	         }
	     } catch (Exception e) {

	     }
        }
    }
    
    @Override
    public Insets getInsets() {
        return new Insets(5,5,5,5);
    }

    public void createPopupMenu() {
        
        if (menu == null) {
            menu = new ResourcePopupMenu();
        }
    }

    public void updateSelectedComponent() {
        currentNode = (DefaultMutableTreeNode)ResourceTree.this.getLastSelectedPathComponent();
    }

    //Get text of the selected node and all its child nodes (recursively)
    public String getData(DefaultMutableTreeNode node, String tabStr, boolean bIncludeRoot) {
        StringBuffer buffer = new StringBuffer();
        int childCount = node.getChildCount();
        ResourceTreeNode rNode = null;
        if (bIncludeRoot) {
             rNode = (ResourceTreeNode)node.getUserObject();
             buffer.append(tabStr + rNode.getDetailedString() + "\n");
        }
        DefaultMutableTreeNode childNode = null;
        tabStr = tabStr + "   ";
        for (int i=0; i<childCount; i++) {
             childNode = (DefaultMutableTreeNode)node.getChildAt(i);
             rNode = (ResourceTreeNode)childNode.getUserObject();
             buffer.append(tabStr + rNode.getDetailedString() + "\n");
             buffer.append(getData(childNode, tabStr, false));
        } 
        return buffer.toString();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
       TreePath selectedPath = getPathForLocation(event.getX(), event.getY());
       if (selectedPath != null) {
	   Object selectedNode = ((DefaultMutableTreeNode)selectedPath.getLastPathComponent()).getUserObject();
	   if (selectedNode != null && selectedNode instanceof ResourceTreeNode && ((ResourceTreeNode)selectedNode).getEntity() != null) {
               return "<html>" + Utilities.getClassHierarchy(((ResourceTreeNode)selectedNode).getEntity().getClass()) + "</html>";
	   }
       }
       return null;
    }
}

class ResourceTreeNode {
    
    private ManagedObjectReference entity = null;
    private String nodestr = "";

    public ResourceTreeNode(ManagedObjectReference obj, String node) {
        entity = obj;
	nodestr = node;
    }
    
    public ResourceTreeNode(ManagedObjectReference obj) {
        entity = obj;
    }

    public ManagedObjectReference getEntity() {
        return entity;
    }
    
    //Return the non truncated node text 
    public String getDetailedString() {
       return entity != null ? nodestr : "";
    }

    //Return the truncated node text if length exceeds 80 chars
    public String toString() {
        String str = entity != null ? nodestr : "";
        if (str != null && str.length() > 80) {
            return str.substring(0, 80) + "...";
        }
        return str != null ? str : "" ;
    }
}

//Create a JPopupMenu subclass to distinguish it from the regular class
class ResourcePopupMenu extends JPopupMenu {

}


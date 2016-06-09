/*
 * File: CoreModulePanel.java
 * Author: Kannan Balasubramanian
 * Creation Date: 06-Aug-2012
 * Last Updated Date: 08-Aug-2012
 */

package com.vmware.viclient.ui;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.RowFilter;
import javax.swing.JSplitPane;

import java.awt.FlowLayout;
import java.awt.BorderLayout;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.viclient.helper.TableData;
import com.vmware.viclient.managedentities.Hostsystem;
import com.vmware.viclient.managedentities.NetworkSystem;
import com.vmware.viclient.managedentities.DatastoreSystem;

import java.util.HashMap;

public class CoreModulePanel extends JPanel {

      private JPanel topPanel = null;
      private TablePanel tPanel = null;
      private PropertiesPanel pPanel = null;
      private JLabel statusLabel = new JLabel("<html><font color=\"red\">Select a hostsystem on left pane.   </font></html>");
      private JLabel catLabel = new JLabel("Choose category");
      private JSplitPane splitPane = null;
      private JComboBox catCombo = new JComboBox();
      public final static String [] hostSystemList = {"Kernel Info", "Datastore", "Hardware Status", "System Health", "Diagnostic", "Capabilities", "Network", "Firewall", "VMotion"};
      public final static String [] datastoreList = {"Datastore Info", "IORM Info", "Datastore Capabilities"};
      public final static String [] networkList = {"Network Summary"};
      public final static String [] dvsList = {"DVS Info", "DVPort Info", "DVPort Group Info"};

      public CoreModulePanel() {
		topPanel = new JPanel(new FlowLayout());
		topPanel.add(statusLabel);
		topPanel.add(catLabel);
		updateCategories(hostSystemList);
		catCombo.setSelectedIndex(-1);

                catCombo.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e){
			    //ConnectionManager cmgr = ConnectionManager.getInstance();
                 Hostsystem hSystem = new Hostsystem();
		 NetworkSystem nSystem = new NetworkSystem();
		 DatastoreSystem dSystem = new DatastoreSystem();
			    String item = (String)e.getItem();
			    if(item.equals("Kernel Info")) {
			        updateProperties(hSystem.getKernelModuleInfo());

			    } else if (item.equals("Datastore")) {
			        updateProperties(hSystem.getDatastoreInfo());
			    } else if (item.equals("Hardware Status")) {
			        updateProperties(hSystem.getHealthStatusInfo());
			    } else if (item.equals("System Health")) {
			        updateProperties(hSystem.getSystemHealthInfo());
			    } else if (item.equals("Diagnostic")) {
			        updateProperties(hSystem.getHostDiagnosticSystemInfo());
			    } else if (item.equals("Capabilities")) {
			        updateProperties(hSystem.getCapabilitiesInfo());
			    } else if (item.equals("Network")) {
			        updateProperties(nSystem.getSummaryInfo());
			    } else if (item.equals("Firewall")) {
			        updateProperties(hSystem.getFirewallInfo());
			    } else if (item.equals("Datastore Info")) {
			        updateProperties(dSystem.getDatastoreInfo());
			    } else if (item.equals("IORM Info")) {
			        updateProperties(dSystem.getStorageIORMInfo());
			    } else if (item.equals("Datastore Capabilities")) {
			        updateProperties(dSystem.getDatastoreCapability());
			    } else {
				updateProperties(null);
			    }
		    }
                });
		topPanel.add(catCombo);

		setLayout(new BorderLayout());

		tPanel = new TablePanel();

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tPanel);
		add(splitPane);
		System.err.println("100");

		String serverType = ConnectionManager.getInstance().getServerType();
		if (serverType.startsWith("VMware ESX")) {
                    statusLabel.setText("<html><font color=\"red\"> " + ConnectionManager.getInstance().getEsxHost() + " </font></html>");
		}

		VIClientEventHandler.getInstance().addListener(
				new VIClientListener() {
		       @Override
                       public void eventUpdate(Object source, VIEvent event) {
                            if (source instanceof ResourceTree) {
                                statusLabel.setText("<html><font color=\"red\">" + event.toString() + "</font>");
			    }
		       }
		});
        }

        public void updateCategories(String [] categories) {
		catCombo.removeAllItems();
		for (int i=0; i<categories.length; i++) {
		     catCombo.addItem(categories[i]);
		}
		//catCombo.setSelectedIndex(-1); //comment from kannan
	}

        public void updateProperties(Object data) {
		if (data == null) {
		    splitPane.setBottomComponent(null);
                    return;
		}
		if (data instanceof TableData) {
                    updateTable((TableData)data);
		    splitPane.setBottomComponent(tPanel);
		} else if (data instanceof HashMap) {
                    pPanel = new PropertiesPanel((HashMap)data);
		    splitPane.setBottomComponent(pPanel);
		}
	}

	public void updateTable(TableData data) {
	     tPanel.updateTable((TableData)data);
	}
}

        class TablePanel extends JPanel {

            private JLabel rowsLabel = new JLabel("");
	    private JLabel label = new JLabel("Filter by: ");
	    private JComboBox filterCombo = new JComboBox();
	    private JTextField filtertf = new JTextField();
            private JTable table = new CustomTable();
            private TableRowSorter sorter = null;
            private JScrollPane scrollPane = null;
	    private DocumentListener curDocListener = null;

            public TablePanel() {
		setLayout(new BorderLayout());
		JPanel tPanel = new JPanel(new SpringLayout());
		tPanel.add(rowsLabel);
		tPanel.add(label);
		tPanel.add(filterCombo);
		tPanel.add(filtertf);
		label.setLabelFor(filterCombo);
                SpringUtilities.makeCompactGrid(tPanel, 1, 4, 50, 6, 15, 6);

		scrollPane = new JScrollPane(table);
                add(tPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
            }

	    public void updateTable(TableData data) {
		 if (data == null) {
		     System.err.println("No table data to update...");
		     if (table != null) {
			 TableModel model = table.getModel();
			 if (model != null && model instanceof DefaultTableModel) {
                             ((DefaultTableModel)model).setColumnCount(0);
			 }
		     }
		     if (curDocListener != null) {
			 filtertf.getDocument().removeDocumentListener(curDocListener);
		         curDocListener = null;
		     }
		     filterCombo.removeAllItems();
		     rowsLabel.setText("");
		     filtertf.setText("");
		     remove(scrollPane);
		     invalidate();
		     validate();
		     repaint();
		     return;
		 }

		 filterCombo.removeAllItems();
                 rowsLabel.setText("");
	         filtertf.setText("");
                 if (curDocListener != null) {
                      filtertf.getDocument().removeDocumentListener(curDocListener);
		      curDocListener = null;
                 }
		 remove(scrollPane);
		 TableModel model = new CustomTableModel(data.rowData, data.colNames);
		 table = new CustomTable(model);
		 table.setModel(model);
		 sorter = new TableRowSorter(model);
		 table.setRowSorter(sorter);

		 int colsize = data.colNames.size();
		 for (int i=0; i<colsize; i++) {
                      filterCombo.addItem(data.colNames.elementAt(i));
		 }

		 rowsLabel.setText("Total rows: " + data.rowData.size());

                 filtertf.getDocument().addDocumentListener(
                     curDocListener = new DocumentListener() {
                       public void changedUpdate(DocumentEvent e) {
                           updateTableFilter(filtertf, filterCombo.getSelectedIndex());
		       }
                       public void insertUpdate(DocumentEvent e) {
                           updateTableFilter(filtertf, filterCombo.getSelectedIndex());
		       }
                       public void removeUpdate(DocumentEvent e) {
                           updateTableFilter(filtertf, filterCombo.getSelectedIndex());
		       }
		 });

		 scrollPane = new JScrollPane(table);
		 add(scrollPane, BorderLayout.CENTER);
		 scrollPane.updateUI();
	   }

	   public void updateTableFilter(JTextField tf, int col) {
               try {
		    if (col == -1) return;
                    RowFilter<CustomTableModel, Object> rf = null;
                    rf = RowFilter.regexFilter(tf.getText(), col);
                    sorter.setRowFilter(rf);
               } catch (java.util.regex.PatternSyntaxException e) {
                    return;
               }
	   }

	}



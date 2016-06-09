/*
 * File: CustomTablePanel.java
 * Author: Kannan Balasubramanian
 * Creation Date: 16-Aug-2012
 * Last Updated Date: 16-Aug-2012
 */

package com.vmware.viclient.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.vmware.viclient.helper.TableData;
import com.vmware.viclient.managedentities.VirtualMachineSystem;
import com.vmware.vim25.ManagedObjectReference;

public class CustomTablePanel extends JPanel {

	private ManagedObjectReference vm = null;
	private TablePanel tPanel = new TablePanel();

	public CustomTablePanel() {
		setLayout(new BorderLayout());
		add(tPanel, BorderLayout.CENTER);

		VIClientEventHandler.getInstance().addListener(
				new VIClientListener() {
                        public void eventUpdate(Object source, VIEvent event) {
                             if (source instanceof ResourceTree) {
				 if (event.getEntity() instanceof ResourceTreeNode) {

				     ResourceTreeNode node = (ResourceTreeNode)event.getEntity();
				     ManagedObjectReference entity = (ManagedObjectReference)node.getEntity(); 
				     
				     if (entity.getType().equals("VirtualMachine")) {
                                        setVirtualMachine(entity);
				     }
			          }
			     }
			}
		});

	}

	public void setVirtualMachine(ManagedObjectReference VM) {
            vm = VM;
	    TableData data = VirtualMachineSystem.getInstance().getVirtualDeviceInfo(vm);
	    tPanel.updateTable(data);
	}
}

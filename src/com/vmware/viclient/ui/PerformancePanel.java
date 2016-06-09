/*
 * File: PerformancePanel.java
 * Author: Kannan Balasubramanian
 * Creation Date: 16-Mar-2013
 * Last Updated Date: 16-Mar-2013
 */

package com.vmware.viclient.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.vmware.viclient.helper.TableData;
import com.vmware.viclient.managedentities.VirtualMachineSystem;

import com.vmware.viclient.managedentities.PerformanceMetrics;

public class PerformancePanel extends JPanel {
	private TablePanel tPanel = new TablePanel();
        private PerformanceMetrics metrics = new PerformanceMetrics();

	public PerformancePanel() {
		setLayout(new BorderLayout());
		add(tPanel, BorderLayout.CENTER);
        }

	public void refresh() {
                TableData data = metrics.getPerfMetrics();
                tPanel.updateTable(data);
	}
}

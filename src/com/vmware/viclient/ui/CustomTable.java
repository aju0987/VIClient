/*
 * File: CustomTable.java
 * Author: Kannan Balasubramanian
 * Creation Date: 03-Aug-2012
 * Last Updated Date: 06-Aug-2012
 */

package com.vmware.viclient.ui;

import javax.swing.JPanel;
import javax.swing.JTable;

import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Color;
import java.awt.Component;

import java.awt.event.MouseEvent;

import java.util.Vector;

public class CustomTable extends JTable {

	public CustomTable() {
             setDefaultRenderer(Object.class, new CustomTableCellRenderer());
	}

	public CustomTable(TableModel model) {
	     super(model);
             setDefaultRenderer(Object.class, new CustomTableCellRenderer());
	}

	@Override
	public String getToolTipText(MouseEvent event) {
	     int row = rowAtPoint(event.getPoint());
	     int col = columnAtPoint(event.getPoint());
	     if (row != -1 && col != -1) {
                 Object val = getValueAt(row, col);
		 if (val != null) {
		     return val.toString();
		 }
	     }
             return "";
	}
}

class CustomTableModel extends DefaultTableModel {

    public CustomTableModel(Vector data, Vector colNames) {
	    super(data, colNames);
    }

    @Override
    public Class getColumnClass(int colIndex) {
	 return super.getColumnClass(colIndex);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
       return false;
    }
}

class CustomTableCellRenderer extends DefaultTableCellRenderer 
{
    @Override
    public Component getTableCellRendererComponent
       (JTable table, Object value, boolean isSelected,
       boolean hasFocus, int row, int column) 
    {
        Component cell = super.getTableCellRendererComponent
           (table, value, isSelected, hasFocus, row, column);
        if( value instanceof String)
        {
	    if (value != null) {
		String cellvalue = ((String)value).toLowerCase();
                if (cellvalue.equals("green")) {
                    cell.setBackground(Color.GREEN);
		} else if (cellvalue.equals("red")) {
                    cell.setBackground(Color.RED);
		} else if (cellvalue.equals("yellow")) {
                    cell.setBackground(Color.YELLOW);
		} else {
                    cell.setBackground(Color.WHITE);
		}
	    }
        } else {
            cell.setBackground(Color.WHITE);
	}
        return cell;
    }
}

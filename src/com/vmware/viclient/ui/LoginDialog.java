/*
 * File: LoginDialog.java
 * Author: Kannan Balasubramanian
 * Creation Date: 05-Aug-2012
 * Last Updated Date: 06-Aug-2012
 */

package com.vmware.viclient.ui;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Dimension;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.viclient.helper.Utilities;

import java.net.URL;
import java.util.StringTokenizer;

public class LoginDialog extends JDialog {

	LoginPanel loginPanel = null;
	PersistUtil persistance = new PersistUtil(LoginDialog.class);
	String serverNames = null;
	final String SERVER_NAME = "Server Name";

	public LoginDialog() {

		serverNames = (String)persistance.getPersistedData(SERVER_NAME, "");
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		VMwareImagePanel imgPanel = new VMwareImagePanel();
		imgPanel.setPreferredSize(imgPanel.getDimension());
		p.add(imgPanel, BorderLayout.NORTH);
		loginPanel = new LoginPanel(this);
		p.add(loginPanel, BorderLayout.CENTER);
		add(p, BorderLayout.CENTER);

		setTitle("VMware VI Client");
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(imgPanel.getWidth(), 380);
		setResizable(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - imgPanel.getWidth() )/ 2;
		int y = (screenSize.height - 380 )/ 2;
		setLocation(x, y);
		setVisible(true);
	}

	public static void main (String [] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		           new LoginDialog();
			}
		});
	}
}

class VMwareImagePanel extends JPanel {

	private BufferedImage vmwareImg = null;
	public VMwareImagePanel() {
             try {
		   URL fileURL = getClass().getResource("/JARs/VMwareClientLogo.png");
		   System.err.println("image url: " + fileURL);
                   vmwareImg = ImageIO.read(fileURL);
	     } catch (Exception e) {
		     e.printStackTrace();
	     }   
	}

	@Override
	public int getWidth() {
             return (vmwareImg != null ? vmwareImg.getWidth()+10 : 400);
	}

	public Dimension getDimension() {
             return new Dimension(vmwareImg.getWidth(), vmwareImg.getHeight());
	}

	@Override
	public void paint (Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
	    if (vmwareImg != null) {
	        g2d.drawImage(vmwareImg, null, 0, 0);
	    }
	}
}

class LoginPanel extends JPanel {

	private LoginDialog parent = null;
	private final String desc = "<html>To directly manage a single host, enter the IP address or host name.To manage multiple hosts, enter the IP address or name of a vCenter Server.</html>";

	private Object connection = null;
	public LoginPanel(LoginDialog dialog) {
            parent = dialog;
            setLayout(new BorderLayout());

            JLabel label = new JLabel(desc, JLabel.TRAILING);
	    JPanel lPanel = new JPanel(new SpringLayout());
	    lPanel.add(label);
	    SpringUtilities.makeCompactGrid(lPanel, 1,1,25,10,0,50);
	    add(lPanel, BorderLayout.NORTH);

	    JLabel ipLabel = new JLabel("IP address / Name:");
	    ipLabel.setDisplayedMnemonic('N');
	    JLabel userLabel = new JLabel("User name:");
	    userLabel.setDisplayedMnemonic('U');
	    JLabel pwdLabel = new JLabel("Password:");
	    pwdLabel.setDisplayedMnemonic('P');

	    final JComboBox ipCombo = new JComboBox();
	    ipCombo.setEditable(true);
            if (parent.serverNames != null) {
		StringTokenizer tokens = new StringTokenizer(parent.serverNames, ";");
		int nTokens = tokens.countTokens();
		for (int i=0; i<nTokens; i++) {
                     ipCombo.addItem(tokens.nextElement());
		}
	    }

	    final JTextField userTextField = new JTextField();
	    userTextField.setDocument(new CustomDocument());

	    final JPasswordField pwdTextField = new JPasswordField();
	    pwdTextField.setEchoChar('*');

	    JPanel panel = new JPanel(new SpringLayout());
	    panel.add(ipLabel);
	    ipLabel.setLabelFor(ipCombo);
	    panel.add(ipCombo);
	    panel.add(new JLabel());
	    panel.add(userLabel);
	    userLabel.setLabelFor(userTextField);
	    panel.add(userTextField);
	    panel.add(new JLabel());
	    panel.add(pwdLabel);
	    pwdLabel.setLabelFor(pwdTextField);
	    panel.add(pwdTextField);
	    panel.add(new JLabel());
	    SpringUtilities.makeCompactGrid(panel, 3,3,50,6,15,6);
	    add(panel, BorderLayout.CENTER);

	    final JLabel statusLabel = new JLabel("");

	    JPanel btnPanel = new JPanel();
	    btnPanel.setLayout(new FlowLayout());

	    final JButton loginBtn = new JButton("Login");
	    final JButton closeBtn = new JButton("Close");

            Utilities.handleEscKey(closeBtn, new Runnable() {
                public void run() {
		    parent.dispose();
                }
            });

	    loginBtn.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
		      final ConnectionManager mgr = ConnectionManager.getInstance();
		           if ( (ipCombo.getSelectedItem() == null || ((String)ipCombo.getSelectedItem()).trim().length() == 0)  && ipCombo.getSelectedIndex() == -1) {
			       JOptionPane.showMessageDialog(null, "Enter a valid server name.", "Could Not Connect", JOptionPane.OK_OPTION);
                               return;
			   }
		           if (userTextField.getText().trim().length() == 0) {
			       JOptionPane.showMessageDialog(null, "A user name is required", "Could Not Connect", JOptionPane.OK_OPTION);
                               return;
			   }
			   statusLabel.setText("Connecting...");
			   loginBtn.setEnabled(false);
			   closeBtn.setText("Cancel");

			   SwingWorker worker = new SwingWorker() {
				   public Object doInBackground() {
		                      try {
					    System.err.println("worker1 : " + connection);
                                            connection = mgr.connect((String)ipCombo.getSelectedItem(), userTextField.getText(), new String(pwdTextField.getPassword()));
		                      } catch (Exception ex) {
					    ex.printStackTrace();
                                            JOptionPane.showMessageDialog(null, "Connection Failed! Please try again!");
		                      }
                                      return connection;
				   }

				   protected void done() {
			              if (connection != null) {
			                    statusLabel.setText("Connection successful.");
					    if (ipCombo.getSelectedIndex() == -1) {

						    System.err.println("AAA: " + ipCombo.getSelectedItem() + " : " + parent.serverNames);
					        parent.serverNames = parent.serverNames.concat((String)ipCombo.getSelectedItem() + ";");
					        parent.persistance.persistData(parent.SERVER_NAME, parent.serverNames);
					    }
				            parent.dispose();
		                            new VIClient((String)ipCombo.getSelectedItem());
				      } else {
			                    statusLabel.setText("Connection failed.");
				      }
				      closeBtn.setText("Close");
				      loginBtn.setEnabled(true);
				   }
			   };
			   worker.execute();
		 }
	    });
	    loginBtn.setMnemonic('L');
	    closeBtn.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
			 if (e.getActionCommand().equals("Cancel")) {
                             closeBtn.setText("Close");
			 } else {
			     parent.dispose();
			 }
		 }
	    });
	    closeBtn.setMnemonic('C');

	    JButton helpBtn = new JButton("Help");
	    helpBtn.setMnemonic('H');

	    btnPanel.add(statusLabel);
	    btnPanel.add(loginBtn);
	    btnPanel.add(closeBtn);
	    btnPanel.add(helpBtn);

	    add(btnPanel, BorderLayout.SOUTH);
	    parent.getRootPane().setDefaultButton(loginBtn);
	}

        class CustomDocument extends PlainDocument {
              public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                     if (str == null) {
                         return;
                     }
                     str = str.trim();
                     super.insertString(offs, str, a);
               }
        }
}

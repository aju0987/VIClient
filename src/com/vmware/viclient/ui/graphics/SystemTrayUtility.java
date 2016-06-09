package com.vmware.viclient.ui.graphics;

import java.awt.Frame;
import java.awt.Button;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.awt.SystemTray;
import java.awt.Image;
import java.awt.AWTException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.imageio.ImageIO;

import java.net.URL;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.InetSocketAddress;

public class SystemTrayUtility {
    
    Frame f;
    Image img;
    PopupMenu menu;
    String label;
    SystemTray sysTray = null;
    TrayIcon icon = null;
    
    public SystemTrayUtility(String title, String url, PopupMenu pMenu) {
        f = null;
        img = loadImage(url);
        label = title;
        menu = pMenu;
        addToSystemTray();
    }
    
    public SystemTrayUtility(Frame frame, Image im, String lab, PopupMenu pMenu) {
        f = frame;
        img = im;
        label = lab;
        menu = pMenu;
        addToSystemTray();
    }
    
    public SystemTrayUtility(Frame frame, String urlStr, String lab, PopupMenu p) {
        this(frame, loadImage(urlStr), lab, p);
    }
    
    static Image loadImage(String urlStr) {
        URL url = null;
        Image im = null;
        try {
            url = new URL(urlStr);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.vmware.com", 3128));
            URLConnection connection = url.openConnection(proxy);
            im = ImageIO.read(connection.getInputStream());
        } catch (Exception e) { e.printStackTrace(); };
        return im;
    }
    
    public void changeImage(Image img) {
        icon.setImage(img);
    }
    
    public void showWarning(String caption, String text) {
        icon.displayMessage(caption, text, TrayIcon.MessageType.WARNING);
    }
    
    public void showInfo(String caption, String text) {
        icon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
    }
    
    public void showError(String caption, String text) {
        icon.displayMessage(caption, text, TrayIcon.MessageType.ERROR);
    }
    
    public void showMessage(String caption, String text) {
        icon.displayMessage(caption, text, TrayIcon.MessageType.NONE);
    }
    
    public void dispose() {
        sysTray.remove(icon);
        icon = null;
    }
    
    public static void main (String args[]) {
        final Frame f = new Frame("vCenter Client");        
        Button b = new Button("Show Notification");
        f.add("South", b);
        PopupMenu menu = new PopupMenu();
        MenuItem exit = new MenuItem("exit");
        MenuItem restore = new MenuItem("restore");
        menu.add(restore);
        menu.add(exit);
        
        String url = "http://i1-news.softpedia-static.com/images/news2/VMware-Launches-vSphere-Client-for-iPad-Free-App-2.png";
        final SystemTrayUtility util = new SystemTrayUtility(f, url, "VCenter Client", menu);
        
        ActionListener def = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (ae.getActionCommand().equals("exit")) {
                    f.dispose();
                    util.dispose();
                } else if (ae.getActionCommand().equals("restore")) {
                    f.setExtendedState(Frame.NORMAL);
                    f.setVisible(true);
                } else if (ae.getActionCommand().equals("Show Notification")) {
                    util.showInfo("Alert", "New VM Added");
                }
            }
        };
        restore.addActionListener(def);
        exit.addActionListener(def);
        b.addActionListener(def);
        f.setSize(200, 200);
        f.setVisible(true);
    }
    
    private void addToSystemTray() {
        if (!SystemTray.isSupported()) {
            throw new RuntimeException("System Tray not supported on your system");
        }
        
        sysTray = SystemTray.getSystemTray();
        icon = new TrayIcon(img, label, menu);
        icon.setImageAutoSize(true);
        icon.setToolTip("vCenter Java Client");
        ActionListener def = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
		if (f!= null) {
                f.setExtendedState(Frame.NORMAL);
                f.setVisible(true);
		}
            }
        };
        icon.addActionListener(def);
	if (f!= null) {
            f.addWindowListener(new WindowAdapter() {
               public void windowIconified(WindowEvent we) {
                we.getWindow().setVisible(false);
            }
            
            public void windowClosing(WindowEvent we) {
                we.getWindow().setVisible(false);
            }
            });
	}
        try {
            sysTray.add(icon);
        } catch (AWTException ae) {
            ae.printStackTrace();
        }
    }
}


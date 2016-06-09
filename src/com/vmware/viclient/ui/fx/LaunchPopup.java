package com.vmware.viclient.ui.fx;

import java.awt.Frame;
import java.awt.Button;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javafx.application.Application;
import javafx.application.Platform;

public class LaunchPopup extends Frame {

    VcenterUI app = null;
    Props p = new Props();

    public LaunchPopup(String str) {
        Thread t = new Thread(new Runnable() { //kannan
            public void run() {
                Application.launch(VcenterUI.class);
            }
        });
        t.start();
        int iter = 1;
        while (app == null && iter < 200) {
            try {
                Thread.sleep(50);
                app = VcenterUI.getInstance();
                iter ++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (app == null) {
            //throw new RuntimeException("Popup App is null. Some problem must have occured."); //commentedK   remove the comment later
	    System.err.println("Error: VcenterUI app is null!  Fix it !!!"); 
        }

    }

    public Props getProps() {
        return p;
    }

    public LaunchPopup() {
        setSize(200, 200);
        setLocation(100, 100);
        Button b = new Button("Show Popup");
        Thread t = new Thread(new Runnable() { //kannan
            public void run() {
                Application.launch(VcenterUI.class);
            }
        });
        t.start();
        // First time, it will be slightly expensive. We need to get the APP handle to proceed further.
        // But this will only initialize and will not show the UI. So can be placed in your app initialization.
        int iter = 1;
        while (app == null && iter < 200) {
            try {
                Thread.sleep(50);
                app = VcenterUI.getInstance();
                iter ++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (app == null) {
            throw new RuntimeException("Popup App is null. Some problem must have occured.");
        }
        add("South", b);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                invokePopup(p, 200, 200);
            }
        });
        setVisible(true);
    }

    public void invokePopup(final Props p, final int locX, final int locY) {
        if (app == null) {
            System.out.println("App is null. Doing nothing");
            return;
        }
        Platform.runLater(new Runnable() {
            public void run() {
                app.constructUI(p, locX, locY);
            }
        });
    }

    public static void main (String args[]) {
        LaunchPopup p = new LaunchPopup();
    }
}


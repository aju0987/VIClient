/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmware.viclient.ui.fx;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 *
 * @author praveen
 */
public class ListBuilder {
    
    public static ListView getList(String[] props) {
        List l = new ArrayList();
        for (String str: props) {
            l.add(str);
        }
        ObservableList<String> llist = FXCollections.observableList(l);
        ListView view = new ListView(llist);
        view.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> list) {
                return new CustomCell();
            }
        });
        String id = String.valueOf(System.currentTimeMillis());
        view.setId(id);
        return view;
    }
}

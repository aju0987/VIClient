/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmware.viclient.ui.fx;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

/**
 *
 * @author admin
 */
public class CustomCell extends ListCell<String>{
   @Override protected void updateItem(String item, boolean empty) {
         super.updateItem(item, empty);
         setText(item == null ? "" : item);

         if (item != null) {
             setTextFill(isSelected() ? Color.WHITE : Color.GREEN);
         }
     }
 }

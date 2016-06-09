/*
 * Summary: A class library of methods 
 * Author: Kannan Balasubramanian
 * Creation Date: 06-Aug-2012
 */

package com.vmware.viclient.helper;

import javax.swing.JComponent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.List;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.DynamicData;

public class Utilities {

    public static final String VOID = "void";
    public static final String GET = "get";
    public static final String ARRAY = "[L";

    //A method for taking an action when a Esc key is pressed in a dialog
    public static void handleEscKey(JComponent c, Runnable task) {
        final Runnable runnable = task;
        Action cancelKeyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
        KeyStroke cancelKeyStroke =
            KeyStroke.getKeyStroke((char)KeyEvent.VK_ESCAPE, 0, false);
        InputMap inputMap =
            c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = c.getActionMap();

        if (inputMap != null && actionMap != null) {
            inputMap.put(cancelKeyStroke, "cancel");
            actionMap.put("cancel", cancelKeyAction);
        }
    }

    //A method that returns all the super classes of a given class name in a tree form using html break tag
    public static String getClassHierarchy(Class cName) {
          String allClasses = cName.toString();
	  while ((cName = cName.getSuperclass()) != null) {
               allClasses = allClasses + ("<br/>" + cName.toString());
	  }
          return allClasses;
    }

    public static void printDynamicData(DynamicData data, int ... levels) {
          try {
               if (data == null) return;
	       System.out.println("DD Type: " + data.getDynamicType());
               List<DynamicProperty> props = data.getDynamicProperty();
	       printDynamicProperties(props);
	  } catch (Exception e) {
               e.printStackTrace();
	  }
    }

    public static void printDynamicProperties(List<DynamicProperty> props, int ... levels) {

          try {
		if (props == null || props.isEmpty()) {
			return;
		}
		for (DynamicProperty prop : props) {
		     printDynamicProperty(prop, levels);
                }
	  } catch (Exception e) {
               e.printStackTrace();
	  } 
    }

    public static void printDynamicProperty(DynamicProperty prop, int ... levels) {
          try {
               if (prop == null) return;
               System.out.println("DP Name: " + prop.getName() + " DP Value: " + prop.getVal());
               System.out.println("(====================================================");
	       Object obj = prop.getVal();
               if (obj == null) return;
               if (obj.getClass() == DynamicData.class) {
                   printDynamicData((DynamicData)(obj));
                   return;
               } else if (obj.getClass() == DynamicProperty.class) {
                   printDynamicProperty((DynamicProperty)(obj));
                   return;
               }
	       invokeObjectMethods(obj, levels);
               System.out.println("====================================================)");
	  } catch (Exception e) {
               e.printStackTrace();
	  }
    }

    private static void invokeObjectMethods(Object obj, int ... levels) {
          try {
	       int totalLevels = -1;
	       int curLevel = -1;
	       if (levels.length > 0) {
                   totalLevels = levels[0];
		   curLevel = 1;
	       }
	       if (levels.length == 2) {
                   curLevel = levels[1];
	       }
	       Class objClass = obj.getClass();
	       Method [] methods = objClass.getMethods();
	       if (methods == null) return;
	       for (Method method : methods) {
                   if (method.getModifiers() == Modifier.PUBLIC && method.getName().startsWith(GET) && (!method.getReturnType().getName().equals(VOID)) && method.getParameterTypes().length == 0 ) {
			Object resultObj = method.invoke(obj);
			System.out.println(objClass + " method: " + method.getName() + " value: " + resultObj);
			if (resultObj == null) continue;
			if (curLevel != -1 && totalLevels != -1 && curLevel >= totalLevels) continue;
			if (method.getReturnType().getName().startsWith(ARRAY)) { // array
			    Object [] objs = (Object[]) resultObj;
			    System.out.println("\nIterating through " + resultObj + " array of size: " + objs.length);
			    System.out.println("[------------------------------------------------------------------\n");
			    ++curLevel;
			    for (Object o : objs) {
                                 invokeObjectMethods(o, totalLevels, curLevel);
			    }
			    System.out.println("------------------------------------------------------------------]\n");
			} else {
                                 invokeObjectMethods(resultObj, totalLevels, ++curLevel);
			}
		   } 
	       }
	  } catch (Exception e) {
               e.printStackTrace();
	  }
    }

}

/*
 * PersistUtil.java
 * Author: Kannan Balasubramanian
 * Created Date: 07-Aug-2012
 * Last updated date: 08-08-2012
 */

package com.vmware.viclient.ui;

import java.util.prefs.Preferences;

public class PersistUtil {
    
    private Preferences persistRoot = null;
    
    /** Creates a new instance of PersistUtil */
    private PersistUtil() {
    }
    
    public PersistUtil(Class className) {
        persistRoot = Preferences.userNodeForPackage(className);
    }
    
    public boolean getBoolPersistedData(String perfKey) {
        try {
            return persistRoot.getBoolean(perfKey,false);
        } catch (Exception be){
            be.printStackTrace();
        }
        return false;
    }
    
    public boolean getBoolPersistedData(String perfKey, boolean def) {
        try {
            return persistRoot.getBoolean(perfKey,def);
        } catch (Exception be){
            be.printStackTrace();
        }
        return def;
    }
    
    public void persistBoolData(String key, boolean value) {
        try {
            persistRoot.putBoolean(key,value);
        } catch (Exception be){
            be.printStackTrace();
        }
    }
    
    public void persistIntData(String key, int value) {
        try {
            persistRoot.putInt(key,value);
        } catch (Exception be){
            be.printStackTrace();
        }
    }

    public int getIntPersistedData(String perfKey) {
        try {
            return persistRoot.getInt(perfKey,0);
        } catch (Exception be){
            be.printStackTrace();
        }
        return 0;
    }

    public int getIntPersistedData(String perfKey, int def) {
        try {
            return persistRoot.getInt(perfKey,def);
        } catch (Exception be){
            be.printStackTrace();
        }
        return def;
    }
    
    public Object getPersistedData(String perfKey) {
        try {
            return persistRoot.get(perfKey,null);
        } catch (Exception be){
            be.printStackTrace();
        }
        return null;
    }
    
    public Object getPersistedData(String perfKey, String def) {
        try {
            return persistRoot.get(perfKey,def);
        } catch (Exception be){
            be.printStackTrace();
        }
        return def;
    }

    public void persistData(String key, Object value) {
        try {
            persistRoot.put(key,value.toString());
        } catch (Exception be){
            be.printStackTrace();
        }
    }
    
}

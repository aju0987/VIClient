package com.vmware.viclient.ui.graphics;

import java.awt.Rectangle;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

public class ManagedObjectManager {

        private static ManagedObjectManager mgr = new ManagedObjectManager();
	final int MIN_STARTX = 20;
	final int MIN_STARTY = 20;
        int startX = 500;
        int startY = 50;
        int width = 150;
        int height = 130;
        int yoffset = height/2;
        int xoffset = width/4;
	public int drawingAreaX = startX + xoffset;
	public int drawingAreaY = startY + yoffset;
	ManagedObject root = null;
	ManagedObject foundObj = null;
	Vector <ERListener> listeners = new Vector <ERListener>();

	HashMap<ManagedObject, Rectangle> map = new HashMap<ManagedObject, Rectangle>();

	private ManagedObjectManager() {

	}
	
	public ManagedObject getRootObject() {
             return root;
	}

	public static ManagedObjectManager getInstance() {
            return mgr;
	}

	public Rectangle getRect(ManagedObject obj) {
            return map.get(obj);
	}

	public void addListener(ERListener l) {
            listeners.add(l);
	}

	public boolean removeListener(ERListener l) {
            if (listeners.contains(l)) {
                listeners.remove(l);
		return true;
	    }
	    return false;
	}

	public void removeAllListeners() {
            listeners.removeAllElements();
	}

	public void fireStateChanged(EREvent e) {
            for (ERListener l : listeners) {
                 l.stateChanged(e);
	    }
	}

	public ManagedObject hitTest(int x, int y) {
	       ManagedObject mobj = null;
	       int size = map.size();
	       Iterator iter = map.keySet().iterator();
	       while (iter.hasNext()) {
		      ManagedObject obj = (ManagedObject)iter.next();
		      Rectangle rect = map.get(obj);
		      Rectangle newRect = new Rectangle(rect);
		      newRect.height = mobj.titleHeight;
                      if (newRect.contains(x, y)) {
                          mobj = obj;
	              }
	       }
	       return mobj;
	}

	public void updateDrawingArea(ManagedObject obj) {
            Rectangle nRect = map.get(obj);
	    if (nRect == null) return;
            drawingAreaY = drawingAreaY < nRect.y + nRect.height ? nRect.y + nRect.height + 30 : drawingAreaY;
            drawingAreaX = drawingAreaX < nRect.x + nRect.width ? nRect.x + nRect.width + 30 : drawingAreaX;
	}

	public void add(ManagedObject obj) {
            if (obj.parent == null) {  //root object
		root = obj;
                Rectangle rect = new Rectangle(startX, startY, width, height);
		map.put(obj, rect);
		drawingAreaX = startX + width + 30;
		drawingAreaY = startY + height + 30;
                return;   
	    }
	    Vector<ManagedObject> children = obj.parent.getChildren();
	    if (children == null || children.size() == 0) {
		Rectangle pRect = map.get(obj.parent);
		if (pRect == null) return; //should never happen !
		Rectangle nRect = new Rectangle();
		nRect.x = pRect.x - width - xoffset;
		if (nRect.x < MIN_STARTX) {
                    nRect.x = MIN_STARTX;
		}
		nRect.y = pRect.y + height + yoffset;
		nRect.width = pRect.width;
		nRect.height = pRect.height;
		drawingAreaY = drawingAreaY < nRect.y + nRect.height ? nRect.y + nRect.height + 30 : drawingAreaY;
		drawingAreaX = drawingAreaX < nRect.x + nRect.width ? nRect.x = nRect.width + 30 : drawingAreaX;
		map.put(obj, nRect);

		Node node = obj.parent.getNode(obj);
		if (node == null) return;
		node.srcPt.x = pRect.x + pRect.width/2;
		node.srcPt.y = pRect.y + pRect.height;
		node.destPt.x = nRect.x + nRect.width;
		node.destPt.y = nRect.y + nRect.height/2;
		return;
	    }
	    ManagedObject lastChild = children.elementAt(children.size() - 1);
	    if (lastChild == null) return;
            Rectangle pRect = map.get(lastChild);
            if (pRect == null) return; //should never happen !
            Rectangle nRect = new Rectangle();
            //nRect.x = pRect.x + width + xoffset;
            nRect.x = pRect.x + xoffset * (ManagedObject.totalLevel - obj.level) ;
            nRect.y = pRect.y;
            nRect.width = pRect.width;
            nRect.height = pRect.height;
	    drawingAreaY = drawingAreaY < nRect.y + nRect.height ? nRect.y + nRect.height + 30 : drawingAreaY;
            drawingAreaX = drawingAreaX < nRect.x + nRect.width ? nRect.x + nRect.width + 30 : drawingAreaX;
            map.put(obj, nRect);

            Node node = obj.parent.getNode(obj);
            if (node == null) return;
	    pRect = map.get(obj.parent);
            node.srcPt.x = pRect.x + pRect.width/2;
            node.srcPt.y = pRect.y + pRect.height;
            node.destPt.x = nRect.x + nRect.width;
            node.destPt.y = nRect.y + nRect.height/2;
	}

	public ManagedObject findObject(ManagedObject mobj, String title) {
	    foundObj = null;
            title = title.trim();
            if (mobj.getName().trim().equals(title)) {
                foundObj = mobj;
                return mobj;
	    } 
	    Vector <ManagedObject> children = mobj.getChildren();
	    if (children == null || children.size() == 0) {
                foundObj = null;
                return null;
	    }
            for (ManagedObject child : children) {
                 if (child.getName().startsWith(title)) {
                     foundObj = child;
                     return child;
	         } 
		 if (foundObj != null) {
		     return foundObj;
		 }
                 findObject(child, title);
	    }
	    return foundObj;
	}
}

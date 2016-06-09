package com.vmware.viclient.ui.graphics;

import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Graphics2D;

import java.util.HashMap;
import java.util.Vector;

public class ManagedObject extends DrawingObject {
        private String name = null;
	private String type = "kannan";
	private String [] properties = null;
	private Vector<ManagedObject> children = null;
	public static int titleHeight = 40;
	private HashMap<ManagedObject, Node> map = new HashMap<ManagedObject, Node>();
	public ManagedObject parent = null;
	private Object entity = null;
	private ManagedObjectManager mgr = ManagedObjectManager.getInstance();
	public  static Color titleColor = new Color(255, 210, 210);
	public static Color propColor = new Color(210, 210, 255);
	public boolean bSelected = false;
	public int level = -1;
	public static int totalLevel = 0;
	private boolean bHighlight = false;
	public static Color highlightColor = Color.RED;
        public static Stroke selStroke = new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
        public static Stroke hStroke = new BasicStroke(8);
	public static boolean b3d = false;

	public ManagedObject() {

	}

	public ManagedObject(String nm, String [] prop) {
           name = nm;
	   properties = prop;
	}

	public void setManagedEntity(Object e) {
           entity = e;
	}

	public Object getManagedEntity() {
           return entity;
	}

	public void setName(String nm) {
           name = nm;
	}

	public void setType(String tp) {
           type = tp;
	}

	public void setProperties(String[] prop) {
           properties = prop;
	}

	public String getName() {
           return name;
	}

	public String getType() {
           return type;
	}

	public String[] getProperties() {
           return properties;
	}

	public Vector<ManagedObject> getChildren() {
           return children;
	}

	public void addChild(ManagedObject child) {
           if (children == null) {
               children = new Vector<ManagedObject>();
	   }
	   child.parent = this;
	   checkLevel(this);
	   Node node = new Node(this, child);
	   map.put(child, node);
	   mgr.add(child);
           children.add(child);
	}

	public void checkLevel(ManagedObject obj) {
           level = 0;
	   ManagedObject p = obj;
	   while ( (p = p.parent) != null) {
		   level++;
	   }
	   totalLevel = totalLevel <= level ? level+1 :  totalLevel;
	}

	public Node getNode(ManagedObject obj) {
           return map.get(obj);
	}

	public void flushAllChildren() {
           flushChildren(this);
	}

	public void flushChildren(ManagedObject self) {
           Vector<ManagedObject> childV = self.getChildren();
	   if (childV != null && childV.size() >= 0) {
               for (ManagedObject obj : childV) {
	            flushChildren(obj);
	       }
               self.children.removeAllElements();
               //self.map.removeAllElements();
	   }
	}

	public void setHighlight(boolean flag) {
           bHighlight = flag;
	}

	public void draw(Rectangle rect, Graphics2D g) {
	   int x = rect.x;
	   int y = rect.y;
	   int width = rect.width;
	   int height = rect.height;
	   Stroke oldStroke = g.getStroke();
	   Color oldColor = g.getColor();
	   if (bHighlight) {
	       g.setStroke(hStroke);
               g.setColor(highlightColor);
	   }
	   if (bSelected) {
	       g.setStroke(selStroke);
	   }
	   Composite oldComposite = g.getComposite();
	   //g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, (float)0.7));
	   
	   if (b3d) {
	       g.draw3DRect(rect.x, rect.y, rect.width, rect.height, true);
	   } else {
	       g.draw(rect);
	   }
	   g.drawLine(x, y+titleHeight, x+width, y+titleHeight);
	   g.setColor(titleColor);
	   if (b3d) {
	       g.fill3DRect(x+1, y+1, width-1, titleHeight-1, true);
	       g.setColor(propColor);
	       g.fill3DRect(x+1, y+titleHeight+1, width-1, height-titleHeight-1, true);
	   } else {
	       g.fillRect(x+1, y+1, width-1, titleHeight-1);
	       g.setColor(propColor);
	       g.fillRect(x+1, y+titleHeight+1, width-1, height-titleHeight-1);
	   }
	   g.setColor(oldColor);
	   FontMetrics metrics = g.getFontMetrics();
	   String tname = getText(metrics, name, mgr.width);

	   g.drawString(tname, x+10, y + titleHeight/3);
	   g.drawString(type, x+10, y + titleHeight/3 + metrics.getHeight());
	   if (properties == null || properties.length == 0) return;
	   int startY = y + titleHeight + 20;
	   int yoffset = 20;
	   for (String prop : properties) {
		if (startY + yoffset >= y + height) break;
                g.drawString(prop, x+10, startY);
		startY += yoffset;
	   }
	   g.setComposite(oldComposite);
	   g.setStroke(oldStroke);
	   g.setColor(oldColor);
	}

	public String getText(FontMetrics metrics, String str, int maxWidth) {
           String tname = str;
	   int charWidth = metrics.charWidth('A');
	   int tchars = maxWidth / charWidth;
	   if (tname.length() > tchars) {
               tname = tname.substring(0, tchars);
	   }
	   return tname;
	}
}

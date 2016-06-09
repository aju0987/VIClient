package com.vmware.viclient.ui.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;

import java.awt.geom.Ellipse2D;

public class Node extends DrawingObject {

	ManagedObject srcObj = null;
	ManagedObject destObj = null;
	Point srcPt = new Point();
	Point destPt = new Point();
	static Color lineColor = Color.RED;
        ManagedObjectManager mgr = ManagedObjectManager.getInstance();
	Point firstPt = new Point();
	Point secondPt = new Point();
	static boolean bDirectLine = false;

        public Node(ManagedObject src, ManagedObject dest) {
           srcObj = src;
	   destObj = dest;
	}

	public void draw(Graphics2D g) {
           if (srcPt == null || destPt == null) return;
           g.setColor(lineColor);
	   validateCoordinates();
	   if (!bDirectLine && firstPt != null && secondPt != null) {
               g.drawLine(srcPt.x, srcPt.y, firstPt.x, firstPt.y);
               g.drawLine(firstPt.x, firstPt.y, secondPt.x, secondPt.y);
               g.drawLine(secondPt.x, secondPt.y, destPt.x, destPt.y);
	   } else {
	       g.drawLine(srcPt.x, srcPt.y, destPt.x, destPt.y); 
	   }
           g.setColor(Color.WHITE);
	   Ellipse2D.Double circle = new Ellipse2D.Double(srcPt.x-3, srcPt.y-3, 5,5);
	   g.fill(circle);
	   circle = new Ellipse2D.Double(destPt.x-3, destPt.y-3, 5,5);
	   g.fill(circle);
	}

	public void validateCoordinates() {
           Rectangle srcRect = mgr.getRect(srcObj);
	   Rectangle destRect = mgr.getRect(destObj);
           if (srcRect.intersects(destRect)) {
               return;
	   }
	   checkTopBottom(srcRect, destRect);
	   checkTopBottom(destRect, srcRect);
	   checkLeftRight(srcRect, destRect);
	   checkLeftRight(destRect, srcRect);
	}

	public void checkTopBottom(Rectangle top, Rectangle bottom) {
           if (top.y + top.height < bottom.y) {
               srcPt.x = top.x + top.width/2;
	       srcPt.y = top.y + top.height;
	       destPt.x = bottom.x + bottom.width/2;
	       destPt.y = bottom.y;
	       int heightDiff = destPt.y - srcPt.y;
	       firstPt.x = srcPt.x;
	       firstPt.y = srcPt.y + heightDiff/2;
	       secondPt.x = destPt.x;
	       secondPt.y = firstPt.y;
	       return;
	   }
	}

	public void checkLeftRight(Rectangle left, Rectangle right) {
	   if (left.x + left.width < right.x) {
               srcPt.x = left.x + left.width;
	       srcPt.y = left.y + left.height/2;
	       destPt.x = right.x;
	       destPt.y = right.y + right.height/2;
	       int heightDiff = destPt.x - srcPt.x;
	       firstPt.x = srcPt.x + heightDiff/2;
	       firstPt.y = srcPt.y;
	       secondPt.x = firstPt.x;
	       secondPt.y = destPt.y;
	       return;
	   }
	}
}

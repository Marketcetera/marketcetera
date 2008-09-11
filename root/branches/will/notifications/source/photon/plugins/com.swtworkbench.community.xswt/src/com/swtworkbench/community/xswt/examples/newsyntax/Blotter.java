package com.swtworkbench.community.xswt.examples.newsyntax;

/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * A SWT desktop blotter component based on Michael Isbell's desktop blotter 
 * component for Delphi.
 * 
 * Provided by www.swtworkbench.com
 * Michael Isbell maintains a weblog at: http://radio.weblogs.com/0117185/
 * 
 * @author djo - David J. Orme
 */
public class Blotter extends Canvas implements PaintListener {
    
    public Blotter(Composite parent, int style) {
        super(parent, style);
        
        // We'll handle our own painting
        addPaintListener(this);
        
        // Set a default background color
        display = Display.getCurrent();
        Color background = display.getSystemColor(SWT.COLOR_DARK_GREEN);
        setBackground(background);
    }

    // Keep track of the system Display object
    private Display display;

    /**
	 * @see org.eclipse.swt.events.PaintListener#paintControl(PaintEvent)
	 */
	public void paintControl(PaintEvent e) {
        GC gc = e.gc;

        Color black = display.getSystemColor(SWT.COLOR_BLACK);
        Color gray = display.getSystemColor(SWT.COLOR_GRAY);
        Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
        Color darkYellow = display.getSystemColor(SWT.COLOR_DARK_YELLOW);

        Rectangle bounds = getBounds();
        int width = bounds.width;
        int height = bounds.height;

        // ***Draw border lines
        
        // Draw vertical lines on left side
        gc.setForeground(black);
        gc.drawLine(0, 0, 0, height);
        
        gc.setForeground(gray);
        gc.drawLine(1, 0, 1, height);
        
        gc.setForeground(black);
        gc.drawLine(4, 0, 4, height-4);
        
        // Draw vertical lines on right side
        gc.setForeground(gray);
        gc.drawLine(width-4, 0, width-4, height-4);
        
        gc.setForeground(black);
        gc.drawLine(width-1, 0, width-1, height);
        
        // Draw horizontal lines at top
        //gc.setForeground(black);
        gc.drawLine(0, 0, width, 0);
        
        gc.setForeground(gray);
        gc.drawLine(0, 1, width, 1);
        
        gc.setForeground(black);
        gc.drawLine(0, 4, width, 4);
        
        // Draw horizontal lines at bottom
        gc.setForeground(gray);
        gc.drawLine(5, height-4, width-4, height-4);
        
        gc.setForeground(black);
        gc.drawLine(0, height-1, width, height-1);
        
        // ***Draw outer corners
        gc.setForeground(yellow);
        
        // Upper left vertical and horizontal
        gc.drawLine(1, 1, 1, 15);
        gc.drawLine(1, 1, 15, 1);
        
        // Lower left vertical only
        gc.drawLine(1, height-1, 1, height-15);

        // Lower right
        gc.setForeground(black);
        
        gc.drawLine(width-2, height-1, width-15, height-1);
        gc.drawLine(width-1, height-1, width-1, height-15);
        
        gc.setForeground(yellow);
        
        gc.drawLine(width-15, height-1, width-16, height-1);
        gc.drawLine(width-1, height-15, width-1, height-16);
        
        // Upper right horizontal only
        gc.drawLine(width-14, 1, width-1, 1);
        
        // ***Draw blotter inner corners
        gc.setForeground(black);
        
        // Upper left
        gc.drawLine(5, 5, 5, 11);
        gc.drawLine(5, 5, 11, 5);
        
        // Lower left
        gc.drawLine(5, height-5, 5, height-5-6);
        
        gc.setForeground(yellow);
        gc.drawLine(6, height-5, 11, height-5);
        
        // Lower right
        gc.setForeground(yellow);
        gc.drawLine(width-5, height-5, width-5, height-11);
        gc.drawLine(width-5, height-5, width-11, height-5);
        
        // Upper right
        gc.setForeground(black);
        gc.drawLine(width-10, 5, width-5, 5);
        
        gc.setForeground(yellow);
        gc.drawLine(width-5, 5, width-5, 10);
        
        // ***Draw the staircase pixels
        gc.setForeground(black);
        
        // Upper left, lower pixels
        gc.drawLine(1, 15, 4, 12);
        
        gc.drawLine(2, height-13, 3, height-12);
        gc.drawLine(4, height-11, 4, height-11);
        
        // Upper left, upper pixels
        gc.drawLine(15, 1, 12, 4);
        
        // Lower left, upper pixels
        gc.setForeground(yellow);
        gc.drawLine(1, height-15, 5, height-11);
        
        // Lower left, lower pixels
        gc.setForeground(black);
        gc.drawLine(11, height-5, 15, height-1);
        
        // Lower right
        gc.setForeground(yellow);
        gc.drawLine(width-15, height-1, width-11, height-5);
        gc.drawLine(width-1, height-15, width-5, height-11);
        
        // Upper right
        gc.setForeground(black);
        gc.drawLine(width-1, 15, width-5, 11);
        gc.drawLine(width-15, 1, width-11, 5);
        
        // ***Fill in "brass" areas for corners
        gc.setForeground(darkYellow);
        gc.setBackground(darkYellow);
        
        // Upper left
        
        // Fill in large areas first
        gc.fillRectangle(2, 2, 3, 10);
        gc.fillRectangle(2, 2, 10, 3);
        
        // Fill in upper pixels
        gc.drawLine(12, 2, 13, 2);
        gc.drawLine(12, 3, 12, 3);
        
        // Fill in lower pixels
        gc.drawLine(2, 12, 2, 13);
        gc.drawLine(3, 12, 3, 12);
        
        // Lower left
        
        // Fill in large areas first
        gc.fillRectangle(2, height-4, 9, height-1);
        gc.fillRectangle(2, height-10, 3, height-2);

        // Fill in upper pixels
        gc.drawLine(2,height-12, 2, height-12);
        gc.drawLine(2,height-11, 3, height-11);
        
        // Fill in lower pixels
        gc.drawLine(13,height-2, 13, height-2);
        gc.drawLine(12,height-2, 12, height-3);
        gc.drawLine(11,height-2, 11, height-4);

        // Lower right
        
        // Fill in large areas first
        gc.fillRectangle(width-11,height-4,width-1, height-1);
        gc.fillRectangle(width-4,height-11,width-1,height-1);
    
        // Fill in upper pixels
        gc.drawLine(width-3,height-12, width-1, height-12);
        gc.drawLine(width-2,height-13, width-1, height-13);
    
        // Fill in lower pixels
        gc.drawLine(width-12,height-3, width-12, height-1);
        gc.drawLine(width-13,height-2, width-13, height-1);

        // Upper right

        // Fill in large areas
        gc.fillRectangle(width-11,2, width-1,3);
        gc.fillRectangle(width-4,2, 4, 10);

        // Fill in upper pixels
        gc.drawLine(width-12,2, width-12,3);
        gc.drawLine(width-13,2, width-13,2);

        // Fill in lower pixels
        gc.drawLine(width-3,12, width-2,12);
        gc.drawLine(width-2,13, width-2,13);
        
        // ***Clean up corner pixels

        // Upper left
        gc.setForeground(black);
        gc.drawLine(0,0, 0,10);
    
        // Lower Left
        gc.drawLine(0,height-1, 13,height-1);
        gc.drawLine(0,height-1, 0,height-14);
    
        // Upper Right
        gc.drawLine(width-1,0, width-14,0);
        gc.drawLine(width-1,0, width-1,13);
    
        // Lower Right
        gc.drawLine(width-1,height-1, width-14,height-1);
        gc.drawLine(width-1,height-1, width-1,height-14);
	}
}

package org.marketcetera.photon.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @see org.eclipse.swt.widgets.Display 
 */
public class SortIndicatorHelper {
	private Display display;

	private Image upArrow;
	
	private Image downArrow;

	public SortIndicatorHelper(Display display) {
		this.display = display;
	}

	public void dispose() {
		if (upArrow != null) {
			upArrow.dispose();
		}
		if (downArrow != null) {
			downArrow.dispose();
		}
	}

	// todo: This code copies win32 Display to provide a temporary Image until we replace it with our own.
	public Image getSortImage(int direction) {
		switch (direction) {
		case SWT.UP: {
			if (upArrow != null)
				return upArrow;
			Color c1 = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			Color c2 = display
					.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
			Color c3 = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			PaletteData palette = new PaletteData(new RGB[] { c1.getRGB(),
					c2.getRGB(), c3.getRGB() });
			ImageData imageData = new ImageData(8, 8, 4, palette);
			imageData.transparentPixel = 2;
			upArrow = new Image(display, imageData);
			GC gc = new GC(upArrow);
			gc.setBackground(c3);
			gc.fillRectangle(0, 0, 8, 8);
			gc.setForeground(c1);
			int[] line1 = new int[] { 0, 6, 1, 6, 1, 4, 2, 4, 2, 2, 3, 2, 3, 1 };
			gc.drawPolyline(line1);
			gc.setForeground(c2);
			int[] line2 = new int[] { 0, 7, 7, 7, 7, 6, 6, 6, 6, 4, 5, 4, 5, 2,
					4, 2, 4, 1 };
			gc.drawPolyline(line2);
			gc.dispose();
			return upArrow;
		}
		case SWT.DOWN: {
			if (downArrow != null)
				return downArrow;
			Color c1 = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			Color c2 = display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
			Color c3 = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			PaletteData palette = new PaletteData(new RGB[] { c1.getRGB(),
					c2.getRGB(), c3.getRGB() });
			ImageData imageData = new ImageData(8, 8, 4, palette);
			imageData.transparentPixel = 2;
			downArrow = new Image(display, imageData);
			GC gc = new GC(downArrow);
			gc.setBackground(c3);
			gc.fillRectangle(0, 0, 8, 8);
			gc.setForeground(c1);
			int[] line1 = new int[] { 7, 0, 0, 0, 0, 1, 1, 1, 1, 3, 2, 3, 2, 5,
					3, 5, 3, 6 };
			gc.drawPolyline(line1);
			gc.setForeground(c2);
			int[] line2 = new int[] { 4, 6, 4, 5, 5, 5, 5, 3, 6, 3, 6, 1, 7, 1 };
			gc.drawPolyline(line2);
			gc.dispose();
			return downArrow;
		}
		}
		return null;
	}
}

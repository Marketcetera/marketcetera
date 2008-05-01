package com.swtworkbench.community.xswt.dataparser.parsers.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.dataparser.parsers.StaticFieldsParser;

public class CSSColorsDataParser extends StaticFieldsParser {

	public CSSColorsDataParser() {
		super(CSSColors.class, RGB.class, null, true);
	}

	public Object parse(String source) {
		RGB rgb = (RGB)super.parse(source);
		if (rgb == null) {
			return null;
		}
		isDisposable = true;
		return new Color(display, rgb);
	}
	
	private Display display = Display.getDefault();

	/**
	 * Method setDisplay. Sets the Display on which to operate.
	 * 
	 * @param display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}
}

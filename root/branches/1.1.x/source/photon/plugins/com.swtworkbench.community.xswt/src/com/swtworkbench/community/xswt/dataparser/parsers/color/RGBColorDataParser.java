package com.swtworkbench.community.xswt.dataparser.parsers.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class RGBColorDataParser extends NonDisposableDataParser {

	public Object parse(String source, IDataParserContext context) throws XSWTException {
		RGB rgb = (RGB)context.parse(source, RGB.class);
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

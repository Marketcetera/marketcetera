package com.swtworkbench.community.xswt.dataparser.parsers.color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.dataparser.parsers.StaticFieldsParser;

public class SWTColorsDataParser extends StaticFieldsParser {

	public SWTColorsDataParser() {
		super(SWT.class, Integer.TYPE, "COLOR_", true);
	}

	public Object parse(String source) {
		if (source.startsWith("SWT.")) {
			source = source.substring(4);
		}
		Integer rgb = (Integer)super.parse(source);
		if (rgb == null) {
			return null;
		}
		isDisposable = false;
		return display.getSystemColor(rgb.intValue());
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

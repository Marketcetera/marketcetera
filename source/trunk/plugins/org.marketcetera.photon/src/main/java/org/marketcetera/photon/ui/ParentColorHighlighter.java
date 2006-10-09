package org.marketcetera.photon.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class ParentColorHighlighter implements IControlHighlighter {

	Color initialColor;
	Color errorColor;
	Control referenceControl;
	Composite parentComposite;
	
	public ParentColorHighlighter(Control control) {
		this.referenceControl = control;
		this.parentComposite = control.getParent();
		initialColor = parentComposite.getBackground();
		errorColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		control.setData(IControlValidator.CONTROL_HIGHLIGHTER_KEY, this);
	}

	public void clearHighlight() {
		parentComposite.setBackground(initialColor);
	}

	public void highlightError() {
		parentComposite.setBackground(errorColor);
	}

}

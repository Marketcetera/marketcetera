package org.marketcetera.photon.ui.validation;

import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class ParentColorHighlighter implements IControlHighlighter {

	ControlDecoration cd;

	public ParentColorHighlighter(Control control) {
		control.setData(IControlValidator.CONTROL_HIGHLIGHTER_KEY, this);
		cd = new ControlDecoration(control, SWT.LEFT | SWT.UP);
		cd.setMarginWidth(0);
		FieldDecoration deco = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		cd.setImage(deco.getImage());
		cd.hide();
	}

	public void clearHighlight() {
		cd.hide();
		cd.getControl().setBackground(null);
		cd.getControl().setToolTipText("");
	}

	public void highlightError(String errorMessage) {
		cd.setDescriptionText(errorMessage);
		cd.getControl().setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		cd.getControl().setToolTipText(errorMessage);
		cd.show();
	}

}

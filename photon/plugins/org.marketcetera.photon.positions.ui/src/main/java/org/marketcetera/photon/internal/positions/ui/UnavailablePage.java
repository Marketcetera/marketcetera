package org.marketcetera.photon.internal.positions.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.Page;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Page to show when the PositionEngine is unavailable.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class UnavailablePage extends Page {

	private Control mControl;

	@Override
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout());
		new Label(c, SWT.WRAP).setText(Messages.UNAVAILABLE_PAGE_DESCRIPTION.getText());
		mControl = c;
	}

	@Override
	public Control getControl() {
		return mControl;
	}

	@Override
	public void setFocus() {
		if (mControl != null && !mControl.isFocusControl()) {
			mControl.setFocus();
		}
	}

}

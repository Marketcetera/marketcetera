package org.marketcetera.photon.internal.positions.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Hierarchical tree based positions view page.
 * 
 * TODO: implement this
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionsViewTreePage extends PositionsViewPage {

	/**
	 * Constructor.
	 * 
	 * @param view
	 *            the view this page is part of
	 */
	public PositionsViewTreePage(PositionsView view) {
		super(view);
	}

	@Override
	protected Control doCreateControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		Label label = new Label(composite, SWT.NONE);
		label.setText("Not implemented"); //$NON-NLS-1$
		return composite;
	}

	@Override
	public void setFilterText(String filterText) {
		// TODO: implement
	}

}

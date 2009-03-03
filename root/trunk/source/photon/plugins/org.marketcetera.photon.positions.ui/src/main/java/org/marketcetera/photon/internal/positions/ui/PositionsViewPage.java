package org.marketcetera.photon.internal.positions.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Abstract base class of pages in the Positions view.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class PositionsViewPage extends Page {

	private final PositionsView mView;

	private Control mControl;

	/**
	 * Constructor.
	 * 
	 * @param view
	 *            the view this page is part of
	 */
	public PositionsViewPage(PositionsView view) {
		mView = view;
	}

	@Override
	public void createControl(Composite parent) {
		mControl = doCreateControl(parent);
		// apply filter
		setFilterText(mView.getFilterText());
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

	/**
	 * Returns the view this page is a part of.
	 * 
	 * @return returns the view
	 */
	public PositionsView getView() {
		return mView;
	}

	/**
	 * Creates the control for this positions view page.
	 * 
	 * @param parent
	 *            the parent composite in which to create the control
	 * @return the new page control
	 */
	protected abstract Control doCreateControl(Composite parent);

	/**
	 * Applies a filter text to this page. The page should reduce the number of items displayed
	 * according to the filter text.
	 * 
	 * @param filterText
	 *            the filter text
	 */
	public abstract void setFilterText(String filterText);
}

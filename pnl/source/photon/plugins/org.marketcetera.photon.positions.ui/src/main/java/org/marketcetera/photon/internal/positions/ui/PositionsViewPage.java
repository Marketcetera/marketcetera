package org.marketcetera.photon.internal.positions.ui;

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

	/**
	 * Constructor.
	 * 
	 * @param view the view this page is part of
	 */
	public PositionsViewPage(PositionsView view) {
		mView = view;
	}

	/**
	 * Returns the view this page is a part of.
	 * 
	 * @return returns the view
	 */
	public PositionsView getView() {
		return mView;
	}
}

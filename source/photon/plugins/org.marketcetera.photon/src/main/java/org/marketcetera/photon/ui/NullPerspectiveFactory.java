package org.marketcetera.photon.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.marketcetera.core.ClassVersion;

/**
 * A perspective factory that creates no initial layout.  This is useful for situations
 * in which you want to organize a perspective entirely declaratively in plugin.xml.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class NullPerspectiveFactory implements IPerspectiveFactory {

	/**
	 * Does nothing.
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
	}

}

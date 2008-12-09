package org.marketcetera.photon.ui;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Workaround for Eclipse bug <a href="http://bugs.eclipse.org/bugs/show_bug.cgi?id=253082">253082</a>.
 * 
 * Provides missing dispose functionality.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public abstract class Workaround253082ContributionItem extends
		WorkbenchWindowControlContribution {

	/**
	 * Subclasses must override to create the control, but must also call this
	 * {@link StatusIndicatorContributionItem} implementation in the process.
	 * This method returns <code>null</code>.
	 * 
	 * @see ControlContribution#createControl(Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		// Workaround for http://bugs.eclipse.org/bugs/show_bug.cgi?id=253082
		// The contribution item dispose method is never called
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				doDispose();
			}
		});
		return null;
	}

	@Override
	public final void dispose() {
		// Never called! http://bugs.eclipse.org/bugs/show_bug.cgi?id=253082
	}

	/**
	 * Dispose the contribution item. Replacement for the non-functioning
	 * {@link #dispose()} method.
	 * 
	 * Subclasses can override to provide additional cleanup.
	 */
	protected void doDispose() {
	}

}

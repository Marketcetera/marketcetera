package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Enables or disables the remote strategy agent depending on whether the context menu item was
 * checked or unchecked.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class EnableRemoteAgentHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (((MenuItem) ((Event) event.getTrigger()).widget).getSelection()) {
			StrategyManager.getCurrent().enableRemoteAgent();
		} else {
			StrategyManager.getCurrent().disableRemoteAgent();
		}
		return null;
	}

}

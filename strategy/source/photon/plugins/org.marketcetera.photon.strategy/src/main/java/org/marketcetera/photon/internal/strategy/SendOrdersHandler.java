package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Opens the trade suggestion in the ticket view to be modified.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SendOrdersHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO: implement this for a later milestone
		MessageDialog.openInformation(HandlerUtil.getActiveShellChecked(event),
				"Not yet implemented", "Feature not yet implemented"); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}

}

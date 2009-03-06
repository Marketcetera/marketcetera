package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Delete handler for {@link TradeSuggestion}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TradeSuggestionDeleteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		for (Object obj : selection.toArray()) {
			TradeSuggestionManager.getCurrent().removeSuggestion((TradeSuggestion) obj);
		}
		return null;
	}

}

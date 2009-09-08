package org.marketcetera.photon.internal.strategy;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler that changes the state of selected {@link Strategy} objects. It must be configured
 * through plug-in extension to specify the state. The state must be specified
 * after the class name, e.g.
 * <p>
 * <code>class="org.marketcetera.photon.internal.strategy.ChangeSelectedStateHandler:STOPPED"</code>
 * <p>
 * See
 * {@link IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)}
 * for more information about parameterizing an extension.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ChangeSelectedHandler extends ChangeStrategyHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Collection<Strategy> strategies = new ArrayList<Strategy>();
		for (Object obj : selection.toArray()) {
			strategies.add((Strategy) obj);
		}
		changeState(strategies);
		return null;
	}

}

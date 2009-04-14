package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler that changes the state of the remote strategy agent. It must be configured
 * through plug-in extension to specify the state. The state must be specified
 * after the class name, e.g.
 * <p>
 * <code>class="org.marketcetera.photon.internal.strategy.ChangeRemoteAgentHandler:STOPPED"</code>
 * <p>
 * See
 * {@link IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)}
 * for more information about parameterizing an extension.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ChangeRemoteAgentHandler extends ChangeStateHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		final RemoteStrategyAgent agent = (RemoteStrategyAgent) selection.getFirstElement();
		validateState(agent);
		BusyIndicator.showWhile(null, new Runnable() {
			@Override
			public void run() {
				switch (getNewState()) {
				case RUNNING:
					StrategyManager.getCurrent().connect(agent);
					break;
				case STOPPED:
					StrategyManager.getCurrent().disconnect(agent);
					break;
				}
			}
		});
		return null;
	}

}

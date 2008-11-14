package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.internal.strategy.Strategy.State;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler that changes the state of a {@link Strategy}. It must be configured
 * through plug-in extension to specify the state. The state must be specified
 * after the class name, e.g.
 * <p>
 * <code>class="org.marketcetera.photon.internal.strategy.ChangeStateHandler:STOPPED"</code>
 * <p>
 * See
 * {@link IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)}
 * for more information about parameterizing an extension.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ChangeStateHandler extends AbstractHandler implements IHandler,
		IExecutableExtension {

	private State mNewState;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			for (Object obj : sselection.toArray()) {
				if (obj instanceof Strategy) {
					Strategy strategy = (Strategy) obj;
					switch (mNewState) {
					case RUNNING:
						StrategyManager.getCurrent().start(strategy);
						break;
					case STOPPED:
						StrategyManager.getCurrent().stop(strategy);
						break;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		mNewState = State.valueOf((String) data);
	}

}

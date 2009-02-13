package org.marketcetera.photon.internal.strategy;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.custom.BusyIndicator;
import org.marketcetera.photon.internal.strategy.Strategy.State;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for handlers that change the state of {@link Strategy} objects.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public abstract class ChangeStateHandler extends AbstractHandler implements
		IHandler, IExecutableExtension {

	private State mNewState;

	/**
	 * Changes the state of the the provided strategies based on how this object
	 * was configured.
	 * 
	 * @param strategies
	 *            strategies to change
	 */
	protected void changeState(final Collection<Strategy> strategies) {
		switch (mNewState) {
		case RUNNING:
			BusyIndicator.showWhile(null, new Runnable() {
				@Override
				public void run() {
					for (Strategy strategy : strategies) {
						StrategyManager.getCurrent().start(strategy);
					}
				}
			});
			break;
		case STOPPED:
			BusyIndicator.showWhile(null, new Runnable() {
				@Override
				public void run() {
					for (Strategy strategy : strategies) {
						StrategyManager.getCurrent().stop(strategy);
					}
				}
			});
			break;
		}
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		mNewState = State.valueOf((String) data);
	}

}

package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for handlers that change the state of {@link AbstractStrategyConnection} objects.
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
	 * Returns the new state.
	 *  
	 * @return the new state
	 */
	protected State getNewState() {
		return mNewState;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		mNewState = State.valueOf((String) data);
	}

}

package org.marketcetera.photon.internal.strategy;

import java.util.Collection;

import org.eclipse.swt.custom.BusyIndicator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for handlers that change the state of {@link Strategy} objects.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class ChangeStrategyHandler extends ChangeStateHandler {

	/**
	 * Changes the state of the the provided strategies based on how this object
	 * was configured.
	 * 
	 * @param strategies
	 *            strategies to change
	 */
	protected void changeState(final Collection<Strategy> strategies) {
		switch (getNewState()) {
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

}

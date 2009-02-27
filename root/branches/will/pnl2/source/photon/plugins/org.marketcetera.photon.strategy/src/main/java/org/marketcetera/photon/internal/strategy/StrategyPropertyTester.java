package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Assert;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A {@link PropertyTester} that supports the "state" attribute of a
 * {@link Strategy}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class StrategyPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		Strategy strategy = (Strategy) receiver;
		Assert.isLegal("state".equals(property), property); //$NON-NLS-1$
		return strategy.getState().name().equals(expectedValue);
	}

}

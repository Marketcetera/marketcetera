package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Assert;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Allows {@link DeployedStrategy} properties to be queried declaratively.
 * Supported properties are:
 * <ul>
 * <li>{@link StrategyEngineWorkbenchUI#DEPLOYED_STRATEGY_STATE_PROPERTY}</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DeployedStrategyPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        DeployedStrategy strategy = (DeployedStrategy) receiver;
        Assert.isLegal(
                StrategyEngineWorkbenchUI.DEPLOYED_STRATEGY_STATE_PROPERTY
                        .equals(property), property);
        return strategy.getState().name().equals(expectedValue);
    }
}

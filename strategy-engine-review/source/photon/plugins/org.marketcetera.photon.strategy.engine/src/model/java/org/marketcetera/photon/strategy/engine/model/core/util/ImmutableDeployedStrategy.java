package org.marketcetera.photon.strategy.engine.model.core.util;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An immutable version of {@link Strategy}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ImmutableDeployedStrategy extends ImmutableStrategy {

    private final ModuleURN mURN;
    private final StrategyState mState;

    /**
     * Constructor.
     * 
     * @param deployedStrategy
     *            the mutable Strategy from which to initialize the object
     */
    public ImmutableDeployedStrategy(DeployedStrategy deployedStrategy) {
        super(deployedStrategy);
        mURN = deployedStrategy.getUrn();
        mState = deployedStrategy.getState();
    }

    /**
     * Returns the module URN.
     * 
     * @return the module URN
     */
    public final ModuleURN getUrn() {
        return mURN;
    }

    /**
     * Returns the state.
     * 
     * @return the state
     */
    public final StrategyState getState() {
        return mState;
    }
    
    @Override
    public DeployedStrategy fill(Strategy strategy) {
        DeployedStrategy deployedStrategy = (DeployedStrategy) strategy;
        super.fill(deployedStrategy);
        deployedStrategy.setUrn(mURN);
        deployedStrategy.setState(mState);
        return deployedStrategy;
    }
}

package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.photon.internal.strategy.engine.ui.DeployedStrategyConfigurationComposite;
import org.marketcetera.photon.internal.strategy.engine.ui.StrategyEngineIdentificationComposite;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;

public final class StrategyEngineUI {

    public final static String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.ui"; //$NON-NLS-1$

    public static Composite createStrategyEngineIdentificationComposite(
            Composite parent, DataBindingContext dataBindingContext,
            StrategyEngine strategyEngine) {
        return new StrategyEngineIdentificationComposite(parent, dataBindingContext,
                strategyEngine);
    }
    
    public static Composite createDeployedStrategyConfigurationComposite(
            Composite parent, DataBindingContext dataBindingContext,
            DeployedStrategy strategy) {
        return new DeployedStrategyConfigurationComposite(parent, dataBindingContext,
                strategy);
    }

    private StrategyEngineUI() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }

}

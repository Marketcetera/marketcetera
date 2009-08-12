package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.photon.internal.strategy.engine.ui.DeployedStrategyConfigurationComposite;
import org.marketcetera.photon.internal.strategy.engine.ui.StrategyEngineIdentificationComposite;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Exposes services offered by this plugin.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StrategyEngineUI {

    /**
     * The unique plug-in ID.
     */
    public final static String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.ui"; //$NON-NLS-1$

    /**
     * Creates a widget that supports editing of basic {@link StrategyEngine} information.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param dataBindingContext
     *            the data binding context to use for model-UI bindings
     * @param strategyEngine
     *            the strategy engine model object
     * @return the widget
     */
    public static Composite createStrategyEngineIdentificationComposite(
            Composite parent, DataBindingContext dataBindingContext,
            StrategyEngine strategyEngine) {
        return new StrategyEngineIdentificationComposite(parent, dataBindingContext,
                strategyEngine);
    }
    
    /**
     * Creates a widget that supports editing of {@link DeployedStrategy} configuration details.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param dataBindingContext
     *            the data binding context to use for model-UI bindings
     * @param strategy
     *            the strategy model object
     * @return the widget
     */
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

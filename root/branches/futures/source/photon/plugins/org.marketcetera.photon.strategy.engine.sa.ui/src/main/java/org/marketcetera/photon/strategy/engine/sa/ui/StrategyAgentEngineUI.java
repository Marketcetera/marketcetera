package org.marketcetera.photon.strategy.engine.sa.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.photon.internal.strategy.engine.sa.ui.StrategyAgentConnectionComposite;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Exposes services offered by this plug-in.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyAgentEngineUI {

    /**
     * The unique plug-in ID.
     */
    public final static String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.sa.ui"; //$NON-NLS-1$

    /**
     * Creates a widget that supports editing of {@link StrategyAgentEngine}
     * connection details.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param dataBindingContext
     *            the data binding context to use for model-UI bindings
     * @param strategyAgentEngine
     *            the strategy agent engine model object
     * @return the widget
     */
    public static Composite createStrategyAgentConnectionComposite(
            Composite parent, DataBindingContext dataBindingContext,
            StrategyAgentEngine strategyAgentEngine) {
        return new StrategyAgentConnectionComposite(parent, dataBindingContext,
                strategyAgentEngine);
    }

    private StrategyAgentEngineUI() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}

package org.marketcetera.photon.strategy.engine.ui.workbench;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to the constants and functionality offered by this bundle.
 * Platform extensions in this bundle's plugin.xml file are considered API only
 * if there are declared here.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEngineWorkbenchUI {

    /**
     * The plug-in id / bundle symbolic name.
     */
    public static final String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.ui.workbench"; //$NON-NLS-1$

    /**
     * The id of the strategy engines view.
     */
    public static final String STRATEGY_ENGINES_VIEW_ID = PLUGIN_ID
            + ".StrategyEnginesView"; //$NON-NLS-1$

    /**
     * The id of the strategy engines status decorator.
     */
    public static final String STRATEGY_ENGINES_STATUS_DECORATOR_ID = PLUGIN_ID
            + ".statusDecorator"; //$NON-NLS-1$

    /**
     * The context id of the context that is globally active when the Strategy
     * Engines view is open and connected to the model. It can be used in
     * Eclipse core expressions to contribute/enable UI contributions.
     */
    public static final String STRATEGY_ENGINES_VIEW_READY_CONTEXT_ID = PLUGIN_ID
            + ".strategyEnginesViewReady"; //$NON-NLS-1$

    /**
     * The id of the command category used for strategy engine related commands.
     */
    public static final String STRATEGY_ENGINES_COMMAND_CATEGORY_ID = PLUGIN_ID
            + ".engineCommands"; //$NON-NLS-1$

    /**
     * The id of the connect command.
     */
    public static final String CONNECT_COMMAND_ID = PLUGIN_ID + ".connect"; //$NON-NLS-1$

    /**
     * The id of the disconnect command.
     */
    public static final String DISCONNECT_COMMAND_ID = PLUGIN_ID
            + ".disconnect"; //$NON-NLS-1$

    /**
     * The id of the deploy command.
     */
    public static final String DEPLOY_COMMAND_ID = PLUGIN_ID + ".deploy"; //$NON-NLS-1$

    /**
     * The id of the undeploy command.
     */
    public static final String UNDEPLOY_COMMAND_ID = PLUGIN_ID + ".undeploy"; //$NON-NLS-1$

    /**
     * The id of the start command.
     */
    public static final String START_COMMAND_ID = PLUGIN_ID + ".start"; //$NON-NLS-1$

    /**
     * The id of the stop command.
     */
    public static final String STOP_COMMAND_ID = PLUGIN_ID + ".stop"; //$NON-NLS-1$

    /**
     * The id of the restart command.
     */
    public static final String RESTART_COMMAND_ID = PLUGIN_ID + ".restart"; //$NON-NLS-1$

    /**
     * The id of the start all command.
     */
    public static final String START_ALL_COMMAND_ID = PLUGIN_ID + ".startAll"; //$NON-NLS-1$

    /**
     * The id of the stop all command.
     */
    public static final String STOP_ALL_COMMAND_ID = PLUGIN_ID + ".stopAll"; //$NON-NLS-1$

    /**
     * The declarative property for strategy engine connection state.
     */
    public static final String STRATEGY_ENGINE_CONNECTION_STATE_PROPERTY = "connectionState"; //$NON-NLS-1$

    /**
     * The declarative property for deployed strategy state.
     */
    public static final String DEPLOYED_STRATEGY_STATE_PROPERTY = "state"; //$NON-NLS-1$

    /**
     * The id of the "Identification" property page for engines.
     */
    public static final String STRATEGY_ENGINE_IDENTIFICATION_PROPERTY_PAGE_ID = PLUGIN_ID
            + ".strategyEngineIdentificationPropertyPage"; //$NON-NLS-1$

    /**
     * The id of the "Configuration" property page for strategies.
     */
    public static final String DEPLOYED_STRATEGY_CONFIGURATION_PROPERTY_PAGE_ID = PLUGIN_ID
            + ".strategyConfigurationPropertyPage"; //$NON-NLS-1$

    private StrategyEngineWorkbenchUI() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}

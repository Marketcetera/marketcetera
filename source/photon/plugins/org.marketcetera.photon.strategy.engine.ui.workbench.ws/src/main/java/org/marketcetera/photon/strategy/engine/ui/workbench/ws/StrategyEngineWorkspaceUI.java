package org.marketcetera.photon.strategy.engine.ui.workbench.ws;

import java.util.List;

import org.marketcetera.photon.internal.strategy.engine.ui.workbench.ws.WorkspaceScriptSelectionButton;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.DeployStrategyWizard;
import org.marketcetera.photon.strategy.engine.ui.FileDialogButton;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides API access to this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEngineWorkspaceUI {

    /**
     * The plug-in id/bundle symbolic name.
     */
    public static final String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.ui.workbench.ws"; //$NON-NLS-1$

    /**
     * Creates a {@link DeployStrategyWizard} that is aware of the workspace and
     * can select scripts from either the workspace or the file system.
     * 
     * @param strategy
     *            seeded strategy values, can be null
     * @param engine
     *            seeded engine selection, can be null
     * @param availableEngines
     *            the available engines, must be non-null and non-empty
     * @throws IllegalArgumentException
     *             if availableEngines is null or has null elements
     */
    public static DeployStrategyWizard createDeployStrategyWizard(Strategy strategy,
            StrategyEngine engine, List<StrategyEngine> availableEngines) {
        return new DeployStrategyWizard(
                strategy,
                engine,
                availableEngines,
                new WorkspaceScriptSelectionButton(),
                new FileDialogButton(
                        Messages.STRATEGY_ENGINE_WORKSPACE_UI_FILE_DIALOG_BUTTON__LABEL
                                .getText()));
    }

    private StrategyEngineWorkspaceUI() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}

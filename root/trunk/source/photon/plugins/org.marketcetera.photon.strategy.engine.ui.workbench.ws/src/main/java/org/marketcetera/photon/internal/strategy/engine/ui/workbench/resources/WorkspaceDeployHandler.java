package org.marketcetera.photon.internal.strategy.engine.ui.workbench.resources;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.DeployStrategyWizard;
import org.marketcetera.photon.strategy.engine.ui.workbench.AbstractDeployWizardHandler;
import org.marketcetera.photon.strategy.engine.ui.workbench.resources.StrategyEngineWorkspaceUI;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;
import com.google.inject.Provider;

/* $License$ */

/**
 * Handler for use when the workspace is available.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class WorkspaceDeployHandler extends AbstractDeployWizardHandler {

    /*
     * Manually tested since ExecutionEvent can't be mocked.
     */
    
    /**
     * Constructor.
     * 
     * @param enginesProvider
     *            the object that will provide the {@link IStrategyEngines} used
     *            to obtain the list of available engines
     */
    @Inject
    public WorkspaceDeployHandler(Provider<IStrategyEngines> enginesProvider) {
        super(enginesProvider);
    }

    @Override
    protected DeployStrategyWizard createWizard(ExecutionEvent event,
            List<StrategyEngine> availableEngines) throws ExecutionException {
        StrategyEngine engine = (StrategyEngine) ((IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event)).getFirstElement();
        return StrategyEngineWorkspaceUI.createDeployStrategyWizard(null,
                engine, availableEngines);
    }
}

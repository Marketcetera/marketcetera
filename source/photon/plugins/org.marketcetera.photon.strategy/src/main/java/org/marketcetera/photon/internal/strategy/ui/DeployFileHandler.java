package org.marketcetera.photon.internal.strategy.ui;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.ui.DeployStrategyWizard;
import org.marketcetera.photon.strategy.engine.ui.workbench.AbstractDeployWizardHandler;
import org.marketcetera.photon.strategy.engine.ui.workbench.ws.StrategyEngineWorkspaceUI;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;
import com.google.inject.Provider;

/* $License$ */

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.deploy} command that
 * deploys the selected {@link IFile}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class DeployFileHandler extends AbstractDeployWizardHandler {

    /**
     * The prefix used to convert an IFile path to a platform resource URL.
     */
    private static final String PLATFORM_RESOURCE_URL_PREFIX = "platform:/resource"; //$NON-NLS-1$

    /**
     * Constructor.
     * 
     * @param enginesProvider
     *            the object that will provide the {@link IStrategyEngines} used
     *            to obtain the list of available engines
     */
    @Inject
    public DeployFileHandler(Provider<IStrategyEngines> enginesProvider) {
        super(enginesProvider);
    }

    @Override
    protected DeployStrategyWizard createWizard(ExecutionEvent event,
            List<StrategyEngine> availableEngines) throws ExecutionException {
        IFile script = (IFile) ((IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event)).getFirstElement();
        Strategy strategy = StrategyEngineCoreFactory.eINSTANCE
                .createStrategy();
        strategy.setScriptPath(PLATFORM_RESOURCE_URL_PREFIX
                + script.getFullPath().toString());
        return StrategyEngineWorkspaceUI.createDeployStrategyWizard(strategy,
                null, availableEngines);
    }

}

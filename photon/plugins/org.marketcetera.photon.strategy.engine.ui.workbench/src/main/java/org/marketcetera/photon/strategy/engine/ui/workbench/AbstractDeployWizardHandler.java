package org.marketcetera.photon.strategy.engine.ui.workbench;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEnginesView;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.DeployStrategyWizard;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;

/* $License$ */

/**
 * Abstract handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.deploy} that launches
 * the {@link DeployStrategyWizard}. Concrete subclasses must override
 * {@link #createWizard(ExecutionEvent, List)} to configure the wizard.
 * <p>
 * If the wizard is successful, and the
 * {@link StrategyEngineWorkbenchUI#STRATEGY_ENGINES_VIEW_ID strategy engines
 * view} is open, this will try to select the new strategy in the view.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class AbstractDeployWizardHandler extends SafeHandler {

    private final Provider<IStrategyEngines> mEnginesProvider;

    /**
     * Constructor.
     * 
     * @param enginesProvider
     *            the object that will provide the {@link IStrategyEngines} used
     *            to obtain the list of available engines
     */
    @Inject
    protected AbstractDeployWizardHandler(
            Provider<IStrategyEngines> enginesProvider) {
        mEnginesProvider = enginesProvider;
    }

    @Override
    public final void executeSafely(ExecutionEvent event)
            throws ExecutionException {
        List<StrategyEngine> engines = getEngines();
        if (engines != null) {
            DeployStrategyWizard newWizard = createWizard(event, engines);
            WizardDialog wizardDialog = new WizardDialog(HandlerUtil
                    .getActiveShellChecked(event), newWizard);
            if (wizardDialog.open() == Window.OK) {
                IWorkbenchWindow window = HandlerUtil
                        .getActiveWorkbenchWindow(event);
                if (window != null) {
                    IWorkbenchPage page = window.getActivePage();
                    if (page != null) {
                        StrategyEnginesView view = (StrategyEnginesView) page
                                .findView(StrategyEngineWorkbenchUI.STRATEGY_ENGINES_VIEW_ID);
                        view.selectReveal(new StructuredSelection(newWizard
                                .getResult()));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<StrategyEngine> getEngines() {
        return ImmutableList.copyOf(Collections2.filter(mEnginesProvider.get()
                .getStrategyEngines(), new Predicate<StrategyEngine>() {
            @Override
            public boolean apply(StrategyEngine input) {
                return input.getConnectionState() == ConnectionState.CONNECTED;
            }
        }));
    }

    /**
     * Creates the wizard.
     * 
     * @param event
     *            An event containing all the information about the current
     *            state of the application, will not be null
     * @param availableEngines
     *            the available engines, will not be null
     * @return the wizard to run
     * @throws ExecutionException
     *             if an exception occurred during execution
     */
    protected abstract DeployStrategyWizard createWizard(ExecutionEvent event,
            List<StrategyEngine> availableEngines) throws ExecutionException;

}

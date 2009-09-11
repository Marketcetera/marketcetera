package org.marketcetera.photon.strategy.engine.strategyagent.ui;

import java.util.concurrent.Callable;

import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.wizard.Wizard;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.NewStrategyAgentWizardPage;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngineFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Wizard for creating a new {@link StrategyAgentEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class NewStrategyAgentWizard extends Wizard {

    private final StrategyAgentEngine mEngine;
    private final IStrategyEngines mEngines;
    private volatile StrategyAgentEngine mResult;

    /**
     * Constructor.
     * 
     * @param engines
     *            the service to use to add the new engine
     * @param engine
     *            seeded values for the wizard, can be null
     * @throws IllegalArgumentException
     *             if engines is null
     */
    public NewStrategyAgentWizard(IStrategyEngines engines,
            StrategyAgentEngine engine) {
        Validate.notNull(engines, "engines"); //$NON-NLS-1$
        setWindowTitle(Messages.NEW_STRATEGY_AGENT_WIZARD__TITLE.getText());
        mEngines = engines;
        mEngine = engine == null ? StrategyAgentEngineFactory.eINSTANCE
                .createStrategyAgentEngine() : engine;
    }

    @Override
    public void addPages() {
        addPage(new NewStrategyAgentWizardPage(mEngine));
    }

    @Override
    public boolean performFinish() {
        Callable<Boolean> operation = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mResult = (StrategyAgentEngine) mEngines.addEngine(mEngine);
                return true;
            }
        };
        return JFaceUtils
                .runWithErrorDialog(new SameShellProvider(getShell()),
                        operation,
                        Messages.NEW_STRATEGY_AGENT_WIZARD_ADD_ENGINE_FAILED);
    }

    /**
     * Returns the result of the wizard.
     * 
     * @return the resulting engine
     */
    public StrategyAgentEngine getResult() {
        return mResult;
    }

}

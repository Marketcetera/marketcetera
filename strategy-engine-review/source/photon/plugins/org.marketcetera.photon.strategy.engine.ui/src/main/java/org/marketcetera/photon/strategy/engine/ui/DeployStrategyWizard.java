package org.marketcetera.photon.strategy.engine.ui;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * A wizard that collect strategy deployment parameters and deploys the strategy
 * to a strategy engine.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class DeployStrategyWizard extends Wizard {

    private final Strategy mStrategy;
    private final IObservableValue mEngine;
    private final DeployStrategyWizardPage mPage;
    private DeployedStrategy mResult;

    /**
     * Constructor that provides a default {@link FileDialogButton} for script
     * selection.
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
    public DeployStrategyWizard(Strategy strategy, StrategyEngine engine,
            List<StrategyEngine> availableEngines) {
        this(strategy, engine, availableEngines, new FileDialogButton(
                Messages.DEPLOY_STRATEGY_WIZARD_DEFAULT_BUTTON__TEXT.getText()));
    }

    /**
     * Constructor.
     * 
     * @param strategy
     *            seeded strategy values, can be null
     * @param engine
     *            seeded engine selection, can be null
     * @param availableEngines
     *            the available engines, must be non-null and non-empty
     * @param buttons
     *            the buttons used to assist in strategy script selection
     * @throws IllegalArgumentException
     *             if availableEngines or buttons is null or has null elements
     */
    public DeployStrategyWizard(Strategy strategy, StrategyEngine engine,
            List<StrategyEngine> availableEngines,
            ScriptSelectionButton... buttons) {
        Validate.nonNullElements(availableEngines, "availableEngines"); //$NON-NLS-1$
        Validate.nonNullElements(buttons, "buttons"); //$NON-NLS-1$
        setWindowTitle(Messages.DEPLOY_STRATEGY_WIZARD_WINDOW__TITLE.getText());
        mStrategy = strategy == null ? StrategyEngineCoreFactory.eINSTANCE
                .createStrategy() : (Strategy) EcoreUtil.copy(strategy);
        mEngine = new WritableValue(engine, StrategyEngine.class);
        mPage = new DeployStrategyWizardPage(mStrategy, mEngine, ImmutableList
                .copyOf(availableEngines), buttons);
    }

    @Override
    public void addPages() {
        addPage(mPage);
    }

    @Override
    public boolean performFinish() {
        final StrategyEngine engine = (StrategyEngine) mEngine.getValue();
        final String name = engine.getName();
        final StrategyEngineConnection connection = engine.getConnection();
        final AtomicReference<DeployedStrategy> result = new AtomicReference<DeployedStrategy>();
        IRunnableWithProgress operation = JFaceUtils.wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                result.set(connection.deploy(mStrategy));
                return null;
            }
        }, Messages.DEPLOY_STRATEGY_WIZARD_DEPLOY__TASK_NAME.getText(name));
        final boolean success = JFaceUtils.runModalWithErrorDialog(
                getContainer(), operation, false, new I18NBoundMessage1P(
                        Messages.DEPLOY_STRATEGY_WIZARD_DEPLOY_FAILED, name));
        if (success) {
            mResult = result.get();
        }
        return success;
    }

    @Override
    public void dispose() {
        super.dispose();
        mEngine.dispose();
    }

    /**
     * Returns the DeployedStrategy that was created by the wizard. It will be
     * non-null only if the wizard finished successfully.
     * 
     * @return the result, or null if the wizard was canceled
     */
    public DeployedStrategy getResult() {
        return mResult;
    }

}

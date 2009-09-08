package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.databinding.CustomWizardPageSupport;
import org.marketcetera.photon.internal.strategy.engine.ui.DeployStrategyComposite;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * The wizard page that collects strategy deployment parameters.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class DeployStrategyWizardPage extends WizardPage {
    
    /*
     * unit tested with DeployStrategyWizard
     */

    private final Strategy mStrategy;
    private final IObservableValue mEngine;
    private final ImmutableList<StrategyEngine> mAvailableEngines;
    private final ScriptSelectionButton[] mButtons;
    private final DataBindingContext mDataBindingContext;
    private DialogPageSupport mWizardSupport;

    /**
     * Constructor.
     * 
     * @param strategy
     *            the strategy model
     * @param engine
     *            the engine model
     * @param availableEngines
     *            the available engines
     * @param buttons
     *            the buttons used to assist in strategy script selection
     * @throws IllegalArgumentException
     *             if any parameter is null, or if availableEngines or buttons
     *             contains null elements
     */
    public DeployStrategyWizardPage(Strategy strategy, IObservableValue engine,
            ImmutableList<StrategyEngine> availableEngines,
            ScriptSelectionButton... buttons) {
        super(DeployStrategyWizardPage.class.getName());
        Validate.notNull(strategy, "strategy", //$NON-NLS-1$ 
                engine, "engine"); //$NON-NLS-1$
        Validate.noNullElements(availableEngines, "availableEngines"); //$NON-NLS-1$
        Validate.noNullElements(buttons, "buttons"); //$NON-NLS-1$
        setImageDescriptor(StrategyEngineImage.DEPLOY_WIZARD_WIZBAN
                .getImageDescriptor());
        setTitle(Messages.DEPLOY_STRATEGY_WIZARD_PAGE__TITLE.getText());
        setDescription(Messages.DEPLOY_STRATEGY_WIZARD_PAGE__DESCRIPTION
                .getText());
        mStrategy = strategy;
        mEngine = engine;
        mAvailableEngines = availableEngines;
        mButtons = buttons;
        mDataBindingContext = new DataBindingContext();
    }

    @Override
    public void createControl(Composite parent) {
        mWizardSupport = CustomWizardPageSupport.create(this,
                mDataBindingContext);
        setControl(new DeployStrategyComposite(parent, mDataBindingContext,
                mStrategy, mAvailableEngines.toArray(new StrategyEngine[0]),
                mEngine, mButtons));
    }

    @Override
    public void dispose() {
        mWizardSupport.dispose();
        if (mDataBindingContext != null) {
            mDataBindingContext.dispose();
        }
    }
}

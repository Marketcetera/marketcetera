package org.marketcetera.photon.internal.strategy.engine.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.photon.commons.ui.databinding.ObservingComposite;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A control for capturing strategy engine name and description. The UI is bound
 * to model objects passed in the constructor.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyEngineIdentificationComposite extends ObservingComposite {

    /**
     * Constructor.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param dataBindingContext
     *            the data binding context to use for model-UI bindings
     * @param strategyEngine
     *            the strategy engine model object
     */
    public StrategyEngineIdentificationComposite(Composite parent,
            DataBindingContext dataBindingContext, StrategyEngine strategyEngine) {
        super(parent);
        GridLayoutFactory.swtDefaults().spacing(10, 5).numColumns(2).applyTo(
                this);
        int textStyle = strategyEngine.isReadOnly() ? SWT.READ_ONLY
                : SWT.BORDER;

        Messages.STRATEGY_ENGINE_IDENTIFICATION_COMPOSITE_NAME
                .createLabel(this);

        {
            Text nameText = new Text(this, textStyle);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(nameText);
            DataBindingUtils
                    .bindRequiredField(
                            dataBindingContext,
                            SWTObservables.observeText(nameText, SWT.Modify),
                            DataBindingUtils
                                    .observeAndTrack(
                                            getObservablesManager(),
                                            strategyEngine,
                                            StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__NAME),
                            Messages.STRATEGY_ENGINE_IDENTIFICATION_COMPOSITE_NAME
                                    .getRawLabel());
        }

        Messages.STRATEGY_ENGINE_IDENTIFICATION_COMPOSITE_DESCRIPTION
                .createLabel(this);

        {
            Text descriptionText = new Text(this, textStyle);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(descriptionText);
            DataBindingUtils
                    .bindValue(
                            dataBindingContext,
                            SWTObservables.observeText(descriptionText,
                                    SWT.Modify),
                            DataBindingUtils
                                    .observeAndTrack(
                                            getObservablesManager(),
                                            strategyEngine,
                                            StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__DESCRIPTION));
        }
    }
}

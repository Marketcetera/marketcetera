package org.marketcetera.photon.internal.strategy.engine.ui;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.photon.commons.ui.databinding.ObservingComposite;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * A control for configuring a deployed strategy. The UI is bound to model
 * objects passed in the constructor.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DeployedStrategyConfigurationComposite extends ObservingComposite {

    private final Strategy mStrategy;

    /**
     * Constructor. Creates the UI widgets and binds them to the provided model.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param dataBindingContext
     *            the data binding context to use for model-UI bindings
     * @param strategy
     *            the strategy model object
     */
    public DeployedStrategyConfigurationComposite(Composite parent,
            DataBindingContext dataBindingContext,
            final DeployedStrategy strategy) {
        super(parent);
        mStrategy = strategy;

        GridLayoutFactory.swtDefaults().spacing(10, 5).numColumns(2).applyTo(
                this);
        List<Control> tabControls = Lists.newLinkedList();

        Messages.DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_INSTANCE_NAME
                .createLabel(this);

        {
            Text instanceNameText = new Text(this, SWT.READ_ONLY);
            DataBindingUtils
                    .bindValue(
                            dataBindingContext,
                            SWTObservables.observeText(instanceNameText,
                                    SWT.Modify),
                            observe(StrategyEngineCorePackage.Literals.STRATEGY__INSTANCE_NAME));
        }

        Messages.DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_CLASS
                .createLabel(this);

        {
            Text classText = new Text(this, SWT.READ_ONLY);
            DataBindingUtils
                    .bindValue(
                            dataBindingContext,
                            SWTObservables.observeText(classText, SWT.Modify),
                            observe(StrategyEngineCorePackage.Literals.STRATEGY__CLASS_NAME));
        }

        Messages.DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_LANGUAGE
                .createLabel(this);

        {
            Text languageText = new Text(this, SWT.READ_ONLY);
            DataBindingUtils
                    .bindValue(
                            dataBindingContext,
                            SWTObservables
                                    .observeText(languageText, SWT.Modify),
                            observe(StrategyEngineCorePackage.Literals.STRATEGY__LANGUAGE));
        }

        Messages.DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_SCRIPT
                .createLabel(this);

        {
            Text scriptText = new Text(this, SWT.READ_ONLY);
            DataBindingUtils
                    .bindValue(
                            dataBindingContext,
                            SWTObservables.observeText(scriptText, SWT.Modify),
                            observe(StrategyEngineCorePackage.Literals.STRATEGY__SCRIPT_PATH));
        }

        {
            Button routeButton = new Button(this, SWT.CHECK);
            routeButton.setText(Messages.STRATEGY_DEPLOYMENT_COMPOSITE_ROUTE
                    .getRawLabel());
            routeButton
                    .setToolTipText(Messages.STRATEGY_DEPLOYMENT_COMPOSITE_ROUTE
                            .getTooltip());
            dataBindingContext
                    .bindValue(
                            SWTObservables.observeSelection(routeButton),
                            observe(StrategyEngineCorePackage.Literals.STRATEGY__ROUTE_ORDERS_TO_SERVER));
            GridDataFactory.swtDefaults().span(2, 1).applyTo(routeButton);
            if (strategy.getState() == StrategyState.RUNNING) {
                routeButton.setEnabled(false);
                tabControls.add(routeButton);
            }
        }
        
        setTabList(tabControls.toArray(new Control[tabControls.size()]));
    }

    private IObservableValue observe(EStructuralFeature feature) {
        return DataBindingUtils.observeAndTrack(getObservablesManager(),
                mStrategy, feature);
    }
}

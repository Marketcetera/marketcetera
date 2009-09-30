package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.photon.commons.ui.databinding.ObservingComposite;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport;
import org.marketcetera.photon.commons.ui.databinding.UpdateStrategyFactory;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEnginePackage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A control for capturing remote strategy engine connection parameters.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentConnectionComposite extends ObservingComposite {

    /**
     * Constructor.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param engine
     *            the model to bind to the control
     */
    public StrategyAgentConnectionComposite(Composite parent,
            DataBindingContext dataBindingContext,
            final StrategyAgentEngine engine) {
        super(parent);
        GridLayoutFactory.swtDefaults().spacing(10, 5).numColumns(2).applyTo(
                this);
        int textStyle;
        if (engine.getConnectionState() == ConnectionState.DISCONNECTED) {
            textStyle = SWT.BORDER;
        } else {
            textStyle = SWT.READ_ONLY;
            Label label = new Label(this, SWT.WRAP);
            label
                    .setText(Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_READ_ONLY__LABEL
                            .getText());
            GridDataFactory.defaultsFor(label).span(2, 1).applyTo(label);
        }

        Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_JMSURL.createLabel(this);

        {
            Text urlText = new Text(this, textStyle);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(urlText);
            final ISWTObservableValue target = SWTObservables.observeText(
                    urlText, SWT.Modify);
            Binding binding = dataBindingContext
                    .bindValue(
                            target,
                            DataBindingUtils
                                    .observeAndTrack(
                                            getObservablesManager(),
                                            engine,
                                            StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE__JMS_URL),
                            UpdateStrategyFactory
                                    .createEMFUpdateValueStrategyWithEmptyStringToNull()
                                    .setBeforeSetValidator(new IValidator() {
                                        @Override
                                        public IStatus validate(Object value) {
                                            final String string = (String) value;
                                            if (StringUtils.isEmpty(string)) {
                                                // return ok here so
                                                // RequiredFieldSupport kicks in
                                                return ValidationStatus.ok();
                                            }
                                            try {
                                                new URI(string);
                                            } catch (URISyntaxException e) {
                                                return ValidationStatus
                                                        .error(
                                                                Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_INVALID_JMSURL
                                                                        .getText(),
                                                                e);
                                            }
                                            return ValidationStatus.ok();
                                        }
                                    }), null);
            RequiredFieldSupport.initFor(dataBindingContext, target,
                    Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_JMSURL
                            .getRawLabel(), binding);
        }

        Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_HOSTNAME.createLabel(this);

        {
            Text hostnameText = new Text(this, textStyle);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(hostnameText);
            DataBindingUtils
                    .bindRequiredField(
                            dataBindingContext,
                            SWTObservables
                                    .observeText(hostnameText, SWT.Modify),
                            DataBindingUtils
                                    .observeAndTrack(
                                            getObservablesManager(),
                                            engine,
                                            StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME),
                            Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_HOSTNAME
                                    .getRawLabel());
        }

        Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_PORT.createLabel(this);

        {
            Text portText = new Text(this, textStyle);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(portText);
            final IObservableValue target = SWTObservables.observeText(
                    portText, SWT.Modify);
            Binding binding = dataBindingContext
                    .bindValue(
                            target,
                            DataBindingUtils
                                    .observeAndTrack(
                                            getObservablesManager(),
                                            engine,
                                            StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT),
                            UpdateStrategyFactory
                                    .withConvertErrorMessage(
                                            UpdateStrategyFactory
                                                    .createEMFUpdateValueStrategyWithEmptyStringToNull(),
                                            Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_INVALID_PORT
                                                    .getText())
                                    .setAfterConvertValidator(new IValidator() {
                                        @Override
                                        public IStatus validate(Object value) {
                                            if (value == null) {
                                                // return ok here so
                                                // RequiredFieldSupport kicks in
                                                return ValidationStatus.ok();
                                            }
                                            int intValue = (Integer) value;
                                            if (intValue < 1
                                                    || intValue > 65535) {
                                                return ValidationStatus
                                                        .error(Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_INVALID_PORT
                                                                .getText());
                                            }
                                            return ValidationStatus.ok();
                                        }
                                    }), new EMFUpdateValueStrategy());
            RequiredFieldSupport.initFor(dataBindingContext, target,
                    Messages.STRATEGY_AGENT_CONNECTION_COMPOSITE_PORT
                            .getRawLabel(), binding);
        }
    }
}

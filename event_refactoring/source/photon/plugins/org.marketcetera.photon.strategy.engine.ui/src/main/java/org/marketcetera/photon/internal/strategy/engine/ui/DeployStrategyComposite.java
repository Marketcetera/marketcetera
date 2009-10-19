package org.marketcetera.photon.internal.strategy.engine.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerObservable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.photon.commons.ui.databinding.ObservingComposite;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.strategy.engine.ui.ScriptSelectionButton;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesLabelProvider;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * A control for capturing strategy deployment parameters. The UI is bound to
 * model objects passed in the constructor.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DeployStrategyComposite extends ObservingComposite {

    private final Strategy mStrategy;
    private final DataBindingContext mDataBindingContext;

    /**
     * Constructor. Creates the UI widgets and binds them to the provided model.
     * 
     * @param parent
     *            parent composite in which to create the widgets
     * @param dataBindingContext
     *            the data binding context to use for model-UI bindings
     * @param strategy
     *            the strategy model object
     * @param availableEngines
     *            the engines available for selection
     * @param selectedEngine
     *            the selected engine model
     * @param scriptSelectionButtons
     *            controls the creation of script selection buttons
     */
    public DeployStrategyComposite(Composite parent,
            DataBindingContext dataBindingContext, final Strategy strategy,
            StrategyEngine[] availableEngines,
            final IObservableValue selectedEngine,
            ScriptSelectionButton... scriptSelectionButtons) {
        super(parent);
        mDataBindingContext = dataBindingContext;
        mStrategy = strategy;

        GridLayoutFactory.swtDefaults().spacing(10, 5).numColumns(3).applyTo(
                this);

        Messages.STRATEGY_DEPLOYMENT_COMPOSITE_SCRIPT.createLabel(this);

        final IObservableValue script = observe(StrategyEngineCorePackage.Literals.STRATEGY__SCRIPT_PATH);
        {
            Text text = new Text(this, SWT.BORDER);
            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(
                    true, false).applyTo(text);
            bindRequiredField(SWTObservables.observeText(text, SWT.Modify),
                    script, Messages.STRATEGY_DEPLOYMENT_COMPOSITE_SCRIPT
                            .getRawLabel());
            script.addValueChangeListener(new IValueChangeListener() {
                @Override
                public void handleValueChange(ValueChangeEvent event) {
                    scriptValueChanged();
                }
            });
            scriptValueChanged();
        }

        {
            final Composite buttons = new Composite(this, SWT.NONE);
            for (ScriptSelectionButton scriptSelectionButton : scriptSelectionButtons) {
                final ScriptSelectionButton current = scriptSelectionButton;
                Button button = new Button(buttons, SWT.NONE);
                button.setText(scriptSelectionButton.getText());
                button.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        final String selected = current.selectScript(
                                getShell(), (String) script.getValue());
                        if (StringUtils.isNotBlank(selected)) {
                            script.setValue(selected.trim());
                        }
                    }
                });
            }
            GridDataFactory.fillDefaults().applyTo(buttons);
            GridLayoutFactory.swtDefaults().numColumns(
                    scriptSelectionButtons.length).generateLayout(buttons);
        }

        Messages.STRATEGY_DEPLOYMENT_COMPOSITE_LANGUAGE.createLabel(this);

        {
            Combo combo = new Combo(this, SWT.NONE);
            combo.setItems(Language.getValues());
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(100,
                    SWT.DEFAULT).applyTo(combo);
            bindRequiredField(
                    SWTObservables.observeText(combo),
                    observe(StrategyEngineCorePackage.Literals.STRATEGY__LANGUAGE),
                    Messages.STRATEGY_DEPLOYMENT_COMPOSITE_LANGUAGE
                            .getRawLabel());
        }

        new Label(this, SWT.NONE);

        Messages.STRATEGY_DEPLOYMENT_COMPOSITE_CLASS.createLabel(this);

        {
            Text classText = new Text(this, SWT.BORDER);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(classText);
            bindRequiredField(
                    SWTObservables.observeText(classText, SWT.Modify),
                    observe(StrategyEngineCorePackage.Literals.STRATEGY__CLASS_NAME),
                    Messages.STRATEGY_DEPLOYMENT_COMPOSITE_CLASS.getRawLabel());
        }

        new Label(this, SWT.NONE);

        Messages.STRATEGY_DEPLOYMENT_COMPOSITE_INSTANCE_NAME.createLabel(this);

        {
            Text instanceNameText = new Text(this, SWT.BORDER);
            GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(180,
                    SWT.DEFAULT).applyTo(instanceNameText);
            bindRequiredField(
                    SWTObservables.observeText(instanceNameText, SWT.Modify),
                    observe(StrategyEngineCorePackage.Literals.STRATEGY__INSTANCE_NAME),
                    Messages.DEPLOYED_STRATEGY_CONFIGURATION_COMPOSITE_INSTANCE_NAME.getRawLabel());
        }

        new Label(this, SWT.NONE);

        {
            Group configurationGroup = new Group(this, SWT.NONE);
            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(
                    true, false).indent(5, SWT.DEFAULT).span(3, 1).applyTo(
                    configurationGroup);
            GridLayoutFactory.swtDefaults().applyTo(configurationGroup);
            configurationGroup
                    .setText(Messages.STRATEGY_DEPLOYMENT_COMPOSITE_CONFIGURATION_GROUP__LABEL
                            .getText());

            {
                Button routeButton = new Button(configurationGroup, SWT.CHECK);
                routeButton
                        .setText(Messages.STRATEGY_DEPLOYMENT_COMPOSITE_ROUTE
                                .getRawLabel());
                routeButton
                        .setToolTipText(Messages.STRATEGY_DEPLOYMENT_COMPOSITE_ROUTE
                                .getTooltip());
                dataBindingContext
                        .bindValue(
                                SWTObservables.observeSelection(routeButton),
                                observe(StrategyEngineCorePackage.Literals.STRATEGY__ROUTE_ORDERS_TO_SERVER));
            }
        }

        {
            Label selectionEnginesLabel = new Label(this, SWT.NONE);
            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(
                    true, false).span(3, 1).applyTo(selectionEnginesLabel);
            selectionEnginesLabel
                    .setText(Messages.STRATEGY_DEPLOYMENT_COMPOSITE_ENGINE_SELECTION_TABLE__LABEL
                            .getText());
        }

        {
            final CheckboxTableViewer selectEnginesTableViewer = CheckboxTableViewer
                    .newCheckList(this, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, true).indent(6,
                    SWT.DEFAULT).span(3, 1).applyTo(
                    selectEnginesTableViewer.getTable());
            selectEnginesTableViewer
                    .setContentProvider(new ArrayContentProvider());
            selectEnginesTableViewer
                    .setLabelProvider(new StrategyEnginesLabelProvider());
            selectEnginesTableViewer.setInput(availableEngines);
            /*
             * Typically, checkbox tables allow multiple selection. In this
             * case, we are using the widget for a single selection so we need a
             * custom listener. It is also a WritableValue and IViewerObservable
             * for data binding and RequiredFieldSupport.
             */
            class FirstAndOnly extends WritableValue implements
                    IViewerObservable, ICheckStateListener {
                public FirstAndOnly() {
                    super(selectedEngine.getValue(), StrategyEngine.class);
                }

                @Override
                public Viewer getViewer() {
                    return selectEnginesTableViewer;
                }

                @Override
                public void checkStateChanged(CheckStateChangedEvent event) {
                    // simulate single selection
                    if (event.getChecked()) {
                        for (Object object : selectEnginesTableViewer
                                .getCheckedElements()) {
                            if (object != event.getElement()) {
                                selectEnginesTableViewer.setChecked(object,
                                        false);
                            } else {
                                setValue(object);
                            }
                        }
                    } else {
                        setValue(null);
                    }
                }
            }
            // if an engine has been pre-selected, check it and disable the
            // table
            if (selectedEngine.getValue() != null) {
                selectEnginesTableViewer.getTable().setEnabled(false);
                selectEnginesTableViewer.setChecked(selectedEngine.getValue(),
                        true);
            }
            FirstAndOnly firstAndOnly = new FirstAndOnly();
            selectEnginesTableViewer.addCheckStateListener(firstAndOnly);
            getObservablesManager().addObservable(firstAndOnly);
            bindRequiredField(firstAndOnly, selectedEngine,
                    Messages.STRATEGY_DEPLOYMENT_COMPOSITE_ENGINE__LABEL
                            .getText());
        }
    }

    private void bindRequiredField(IObservableValue target,
            final IObservableValue model, final String description) {
        DataBindingUtils.bindRequiredField(mDataBindingContext, target, model,
                description);
    }

    private IObservableValue observe(EStructuralFeature feature) {
        return DataBindingUtils.observeAndTrack(getObservablesManager(),
                mStrategy, feature);
    }

    private void scriptValueChanged() {
        final String newValue = mStrategy.getScriptPath();
        if (newValue != null) {
            /*
             * Split on '.' to get the extension
             */
            String[] split1 = newValue.split("\\.", -1); //$NON-NLS-1$
            if (split1.length > 1) {
                Language language = Language
                        .getLanguageFor(split1[split1.length - 1]);
                if (language != null) {
                    mStrategy.setLanguage(language.getValue());
                    /*
                     * Split on common path separators, '\' or '/' to try to get
                     * the file name. Don't just use File.separatorChar since
                     * there is no guarantee that this is a native file path.
                     */
                    String[] split2 = newValue.substring(0,
                            newValue.lastIndexOf('.')).split("[\\\\\\/]", -1); //$NON-NLS-1$
                    if (split2.length > 1) {
                        String className = language
                                .getClassFor(split2[split2.length - 1]);
                        if (className != null) {
                            mStrategy.setClassName(className);
                        }
                    }
                }
            }
        }
    }

    /**
     * Encapsulates some script language related logic/heuristics.
     */
    private enum Language {
        /**
         * Java
         */
        Java("JAVA", "java"), //$NON-NLS-1$ //$NON-NLS-2$

        /**
         * Ruby
         */
        Ruby("RUBY", "rb") { //$NON-NLS-1$ //$NON-NLS-2$
            @Override
            public String getClassFor(String fileNameExtensionStripped) {
                String className = super.getClassFor(fileNameExtensionStripped);
                /*
                 * retains letters and numbers and capitalizes the first in a
                 * word, e.g. my_strategy -> MyStrategy
                 */
                StringBuilder builder = new StringBuilder();
                boolean caps = true;
                for (char c : className.toCharArray()) {
                    if (Character.isLetterOrDigit(c)) {
                        builder.append(caps ? Character.toUpperCase(c) : c);
                        caps = false;
                    } else {
                        caps = true;
                    }
                }
                return StringUtils.defaultIfEmpty(builder.toString(), null);
            }
        };

        private final String mValue;
        private final String mExtension;

        private static final ImmutableMap<String, Language> sExtensionToLanguage;
        private static final ImmutableList<String> sValues;

        static {
            ImmutableMap.Builder<String, Language> mapBuilder = ImmutableMap
                    .builder();
            ImmutableList.Builder<String> valuesBuilder = ImmutableList
                    .builder();
            for (Language language : Language.values()) {
                mapBuilder.put(language.mExtension, language);
                valuesBuilder.add(language.mValue);
            }
            sExtensionToLanguage = mapBuilder.build();
            sValues = valuesBuilder.build();
        }

        /**
         * Returns the language associated with an extension.
         * 
         * @param extension
         *            the extension
         * @return the language
         */
        public static Language getLanguageFor(String extension) {
            return sExtensionToLanguage.get(extension);
        }

        /**
         * Returns all language values.
         * 
         * @return the values
         */
        public static String[] getValues() {
            return sValues.toArray(new String[sValues.size()]);
        }

        private Language(String value, String extension) {
            mValue = value;
            mExtension = extension;
        }

        /**
         * Returns a best-guess class name for a given file name. The base
         * implementation discards characters as needed to make the string a
         * valid Java identifier.
         * 
         * @param fileNameExtensionStripped
         *            the file name without its extension
         * @return the guessed class name
         * @throws IllegalArgumentException
         *             if fileNameExtensionStripped is null
         */
        public String getClassFor(String fileNameExtensionStripped) {
            Validate.notNull(fileNameExtensionStripped,
                    "fileNameExtensionStripped"); //$NON-NLS-1$
            StringBuilder builder = new StringBuilder();
            boolean started = false;
            for (char c : fileNameExtensionStripped.toCharArray()) {
                if (started) {
                    if (Character.isJavaIdentifierPart(c)) {
                        builder.append(c);
                    }
                } else if (Character.isJavaIdentifierStart(c)) {
                    builder.append(c);
                    started = true;
                }
            }
            return builder.toString();
        }

        /**
         * Returns the value (in the data model domain) for this language.
         * 
         * @return the value
         */
        public String getValue() {
            return mValue;
        }

    }

}

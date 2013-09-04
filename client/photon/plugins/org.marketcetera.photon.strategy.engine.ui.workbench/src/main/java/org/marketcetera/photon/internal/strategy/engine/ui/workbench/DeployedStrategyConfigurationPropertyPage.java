package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.commons.ui.workbench.DataBindingPropertyPage;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineImage;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineUI;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link PropertyPage} to edit {@link DeployedStrategy} properties.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class DeployedStrategyConfigurationPropertyPage extends
        DataBindingPropertyPage {

    private DeployedStrategy mOriginalStrategy;

    private DeployedStrategy mNewStrategy;

    private PropertySheetPage mPage;

    /**
     * Constructor.
     */
    public DeployedStrategyConfigurationPropertyPage() {
        setImageDescriptor(StrategyEngineImage.STRATEGY_OBJ
                .getImageDescriptor());
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        mOriginalStrategy = (DeployedStrategy) getElement().getAdapter(
                DeployedStrategy.class);
        // make a copy so cancel works as expected
        mNewStrategy = (DeployedStrategy) EcoreUtil.copy(mOriginalStrategy);

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        StrategyEngineUI.createDeployedStrategyConfigurationComposite(
                composite, getDataBindingContext(), mNewStrategy);
        addSeparator(composite);
        addParametersSection(composite);
        if (mOriginalStrategy.getState().equals(StrategyState.RUNNING)) {
            ControlEnableState.disable(composite);
        }
        return composite;
    }

    private EMap<String, String> getParameters() {
        return mNewStrategy.getParameters();
    }

    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    private void addParametersSection(Composite parent) {
        Font font = parent.getFont();
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.swtDefaults().applyTo(composite);

        Label label = new Label(composite, SWT.NONE);
        label.setFont(font);
        label
                .setText(Messages.STRATEGY_PROPERTY_PAGE_PARAMETERS_DESCRIPTION__LABEL
                        .getText());
        GridDataFactory.defaultsFor(label).applyTo(label);

        // Nest the property sheet page used by the Properties view.
        mPage = new PropertySheetPage();
        mPage.setPropertySourceProvider(new IPropertySourceProvider() {
            @Override
            public IPropertySource getPropertySource(Object object) {
                if (object instanceof IPropertySource)
                    return (IPropertySource) object;
                return null;
            }
        });
        mPage.createControl(composite);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(
                mPage.getControl());

        // Simulate selection of a root element
        mPage.selectionChanged(null, new StructuredSelection(
                new StrategyPropertySource(getParameters()))); //$NON-NLS-1$

        initPopupMenu();
    }

    private void initPopupMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                final TreeItem[] selection = ((Tree) mPage.getControl())
                        .getSelection();
                // Add
                manager.add(new Action(
                        Messages.STRATEGY_PROPERTY_PAGE_ADD_MENU_ITEM__TEXT
                                .getText()) {
                    @Override
                    public void run() {
                        addNewProperty();
                    }
                });
                // Delete
                if (selection.length >= 1) {
                    manager
                            .add(new Action(
                                    Messages.STRATEGY_PROPERTY_PAGE_DELETE_MENU_ITEM__TEXT
                                            .getText()) {
                                @Override
                                public void run() {
                                    for (int i = 0; i < selection.length; i++) {
                                        final PropertySheetEntry entry = (PropertySheetEntry) selection[i]
                                                .getData();
                                        try {
                                            Method method = PropertySheetEntry.class
                                                    .getDeclaredMethod("getDescriptor"); //$NON-NLS-1$
                                            method.setAccessible(true);
                                            IPropertyDescriptor descriptor = (IPropertyDescriptor) method
                                                    .invoke(entry);
                                            getParameters().removeKey(
                                                    descriptor.getId());
                                        } catch (Exception e) {
                                            throw ExceptUtils.wrapRuntime(e);
                                        }
                                    }
                                    mPage.refresh();
                                }
                            });
                }
            }
        });
        Menu menu = menuMgr.createContextMenu(mPage.getControl());
        mPage.getControl().setMenu(menu);
    }

    @Override
    protected void contributeButtons(Composite parent) {
        // A button to add properties
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText(Messages.STRATEGY_PROPERTY_PAGE_ADD_BUTTON__TEXT
                .getText());
        GridDataFactory.defaultsFor(addButton).applyTo(addButton);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addNewProperty();
            }
        });
        ((GridLayout) parent.getLayout()).numColumns++;
        if (mOriginalStrategy.getState().equals(StrategyState.RUNNING)) {
            addButton.setEnabled(false);
        }
    }

    private void addNewProperty() {
        NewPropertyInputDialog dialog = new NewPropertyInputDialog(
                getShell());
        if (dialog.open() == IDialogConstants.OK_ID) {
            final String key = dialog.getPropertyKey();
            if (!getParameters().containsKey(key))
                getParameters().put(key, dialog.getPropertyValue());
            mPage.refresh();
        }
    }

    @Override
    public boolean performOk() {
        final StrategyEngine engine = mOriginalStrategy.getEngine();
        final String name = engine.getName();
        final StrategyEngineConnection connection = engine.getConnection();
        IRunnableWithProgress operation = JFaceUtils.wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                connection.update(mOriginalStrategy, mNewStrategy);
                return null;
            }
        }, Messages.STRATEGY_PROPERTY_PAGE_UPDATE__TASK_NAME.getText(name));
        final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                getShell());
        final boolean success = JFaceUtils.runModalWithErrorDialog(dialog,
                dialog, operation, false, new I18NBoundMessage1P(
                        Messages.STRATEGY_PROPERTY_PAGE_UPDATE_FAILED, name));
        return success;
    }

    /**
     * {@link IPropertySource} for adapting a DeployedStrategy parameters for
     * the standard property sheet.
     */
    @ClassVersion("$Id$")
    private final static class StrategyPropertySource implements
            IPropertySource {

        private final EMap<String, String> mProperties;

        /**
         * Constructor.
         * 
         * @param strategy
         *            the Strategy to adapt
         */
        public StrategyPropertySource(EMap<String, String> properties) {
            mProperties = properties;
        }

        @Override
        public Object getEditableValue() {
            return null;
        }

        @Override
        public IPropertyDescriptor[] getPropertyDescriptors() {
            List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
            for (Map.Entry<String, String> entry : mProperties) {
                descriptors.add(new TextPropertyDescriptor(entry.getKey(),
                        entry.getKey()));
            }
            return (IPropertyDescriptor[]) descriptors
                    .toArray(new IPropertyDescriptor[descriptors.size()]);
        }

        @Override
        public Object getPropertyValue(Object id) {
            return mProperties.get(id);
        }

        @Override
        public boolean isPropertySet(Object id) {
            // no defaults
            return false;
        }

        @Override
        public void resetPropertyValue(Object id) {
            // no defaults
        }

        @Override
        public void setPropertyValue(Object id, Object value) {
            mProperties.put((String) id, (String) value);
        }
    }
}
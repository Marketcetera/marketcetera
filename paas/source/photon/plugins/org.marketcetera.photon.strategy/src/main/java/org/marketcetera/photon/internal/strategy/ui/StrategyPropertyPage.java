package org.marketcetera.photon.internal.strategy.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.internal.strategy.Strategy;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.photon.internal.strategy.StrategyValidation;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;
import org.marketcetera.photon.module.ui.NewPropertyInputDialog;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link PropertyPage} to edit {@link Strategy} properties.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class StrategyPropertyPage extends PropertyPage {

	private Text mNameText;

	private Strategy mStrategy;

	private PropertySheetPage mPage;

	private Properties mProperties;

	private Button mRouteToServer;

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);

		addNameSection(composite);
		addSeparator(composite);
		addParametersSection(composite);
		if (mStrategy.getState().equals(State.RUNNING))
			ControlEnableState.disable(composite);
		return composite;
	}

	private void addNameSection(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		Text file = StrategyUI.createFileText(composite);
		file.setText(mStrategy.getFile().getFullPath().toString());

		final Text className = StrategyUI.createClassNameText(composite, true);
		className.setText(mStrategy.getClassName());
		mNameText = StrategyUI.createDisplayNameText(composite);
		mNameText.setText(mStrategy.getDisplayName());
		mNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		mRouteToServer = StrategyUI.createRoutingCheckBox(composite);
		mRouteToServer.setSelection(mStrategy.getRouteToServer());

		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}
	
	private void validate() {
		String name = mNameText.getText();
		IStatus status = StrategyValidation.validateDisplayNameNotBlank(name);
		if (!status.isOK()) {
			handleStatus(status);
			return;
		} 
		if (!mStrategy.getDisplayName().equals(name)) {
			status = StrategyValidation.validateDisplayNameUnique(name);
			if (!status.isOK()) {
				handleStatus(status);
				return;
			}
		}
		setValid(true);
		setErrorMessage(null);
	}
	
	private void handleStatus(IStatus status) {
		setValid(false);
		setErrorMessage(status.getMessage());
	}

	private void addParametersSection(Composite parent) {
		Font font = parent.getFont();
		Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
		GridLayoutFactory.swtDefaults().applyTo(composite);
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(font);
		label.setText(Messages.STRATEGY_PROPERTIES_PARAMETERS_DESCRIPTION.getText());
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
				new StrategyPropertySource())); //$NON-NLS-1$

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
				if (selection.length <= 1) {
					manager.add(new Action(Messages.STRATEGY_PROPERTIES_ADD_LABEL.getText()) {
						@Override
						public void run() {
							NewPropertyInputDialog dialog = new NewPropertyInputDialog(
									getShell(), false);
							if (dialog.open() == IDialogConstants.OK_ID) {
								final String key = dialog.getPropertyKey();
								if (!mProperties.containsKey(key))
									mProperties.put(key, dialog
											.getPropertyValue());
								mPage.refresh();
							}
						}
					});
				}
				// Delete
				if (selection.length >= 1) {
					manager.add(new Action(Messages.STRATEGY_PROPERTIES_DELETE_LABEL.getText()) {
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
									mProperties.remove(descriptor.getId());
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
		addButton.setText(Messages.STRATEGY_PROPERTIES_ADD_BUTTON_LABEL.getText());
		GridDataFactory.defaultsFor(addButton).applyTo(addButton);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewPropertyInputDialog dialog = new NewPropertyInputDialog(
						getShell(), false);
				if (dialog.open() == IDialogConstants.OK_ID) {
					final String key = dialog.getPropertyKey();
					if (!mProperties.containsKey(key))
						mProperties.put(key, dialog.getPropertyValue());
					mPage.refresh();
				}
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		mStrategy = (Strategy) getElement().getAdapter(Strategy.class);
		mProperties = mStrategy.getParameters();
	}

	@Override
	public boolean performOk() {
		StrategyManager.getCurrent().setDisplayName(mStrategy,
				mNameText.getText());
		StrategyManager.getCurrent().setParameters(mStrategy, mProperties);
		StrategyManager.getCurrent().setRouteToServer(mStrategy, mRouteToServer.getSelection());
		return true;
	}

	/**
	 * {@link IPropertySource} for adapting a {@link Strategy} for the standard
	 * property sheet.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")
	private final class StrategyPropertySource implements IPropertySource {
		/**
		 * Constructor.
		 * 
		 * @param strategy
		 *            the Strategy to adapt
		 */
		StrategyPropertySource() {
			super();
		}

		@Override
		public Object getEditableValue() {
			return null;
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
			for (Map.Entry<Object, Object> entry : mProperties.entrySet()) {
				descriptors.add(new TextPropertyDescriptor(entry.getKey(),
						(String) entry.getKey()));
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
			mProperties.put(id, value);
		}
	}

}
package org.marketcetera.photon.module.preferences;

import static org.marketcetera.photon.module.ModulePlugin.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.marketcetera.photon.module.ModulePlugin;
import org.marketcetera.photon.module.PropertiesTree;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The Module Properties preference page. All properties are stored in a single
 * Eclipse runtime preference.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public final class ModulePropertiesPreferencePage extends PreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Properties separator character.
	 */
	private static final String SEPARATOR = "."; //$NON-NLS-1$

	/**
	 * Pattern to split properties.
	 */
	private static final Pattern SEPARATOR_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$

	/**
	 * Holds the properties being edited
	 */
	private final PropertiesTree mProperties;

	/**
	 * Root of the UI
	 */
	private PropertySheetPage mPage;

	/**
	 * Default Constructor.
	 * 
	 * Initialized by extension point.
	 */
	public ModulePropertiesPreferencePage() {
		mProperties = ModulePlugin.getDefault().getModuleProperties();
		ModulePlugin.getDefault().seedKnownKeys(mProperties);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NO_FOCUS);
		GridLayoutFactory.fillDefaults().applyTo(composite);
		Label warningLabel = new Label(composite, SWT.WRAP);
		warningLabel.setText(Messages.MODULE_PROPERTIES_PREFERENCE_PAGE_RESTART_WARNING.getText());
		GridDataFactory.defaultsFor(warningLabel).applyTo(warningLabel);

		// Nest the property sheet page used by the Properties view.
		mPage = new PropertySheetPage();
		mPage.setPropertySourceProvider(new IPropertySourceProvider() {
			@Override
			public IPropertySource getPropertySource(Object object) {
				if (object instanceof IPropertySource)
					return (IPropertySource) object;
				// if (object instanceof NodeDescriptor)
				// return new ModulesPropertySource((NodeDescriptor) object);
				return null;
			}
		});
		mPage.createControl(composite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(
				mPage.getControl());

		// Simulate selection of a root property ""
		mPage.selectionChanged(null, new StructuredSelection(
				new ModulePropertyNode(""))); //$NON-NLS-1$

		// By default properties are lazily loaded when the user expands nodes,
		// but here we want the user can see all the properties right away.
		expandAll();

		// Right click actions
		initPopupMenu();

		return composite;
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
					manager.add(new Action(
							Messages.MODULE_PROPERTIES_PREFERENCE_PAGE_ADD_ACTION_LABEL
									.getText()) {
						@Override
						public void run() {
							String key = ""; //$NON-NLS-1$
							if (selection.length == 1)
								key = ((ModulePropertyNode) ((PropertySheetEntry) selection[0]
										.getData()).getValues()[0]).key
										+ SEPARATOR;
							// Show Instance Defaults if the selected property
							// is level 2, i.e. a module provider
							final boolean allowInstanceDefault = (SEPARATOR_PATTERN
									.split(key).length == 2);
							NewPropertyInputDialog dialog = new NewPropertyInputDialog(
									getShell(), allowInstanceDefault);
							if (dialog.open() == IDialogConstants.OK_ID) {
								if (dialog.isInstanceDefault())
									key += INSTANCE_DEFAULTS_INDICATOR
											+ SEPARATOR;
								key += dialog.getPropertyKey();
								if (!mProperties.containsKey(key))
									mProperties.put(key, dialog
											.getPropertyValue());
								mPage.refresh();
								expand(selection[0]);
							}
						}
					});
				}
				// Delete
				if (selection.length >= 1) {
					manager.add(new Action(
							Messages.MODULE_PROPERTIES_PREFERENCE_PAGE_DELETE_ACTION_LABEL
									.getText()) {
						@Override
						public void run() {
							for (int i = 0; i < selection.length; i++) {
								final String root = ((ModulePropertyNode) ((PropertySheetEntry) selection[i]
										.getData()).getValues()[0]).key;
								mProperties.remove(root);
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
		Button button = new Button(parent, SWT.PUSH);
		button.setText(Messages.MODULE_PROPERTIES_PREFERENCE_PAGE_ADD_BUTTON_LABEL
				.getText());
		GridDataFactory.defaultsFor(button).align(SWT.CENTER, SWT.CENTER)
				.applyTo(button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewPropertyInputDialog dialog = new NewPropertyInputDialog(
						getShell(), false);
				if (dialog.open() == IDialogConstants.OK_ID) {
					final String key = dialog.getPropertyKey();
					if (!mProperties.containsKey(key))
						mProperties.put(key, dialog.getPropertyValue());
					mPage.refresh();
					expandAll();
				}
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
	}

	/**
	 * Helper method to expand entire property tree
	 */
	private void expandAll() {
		Tree tree = (Tree) mPage.getControl();
		for (TreeItem item : tree.getItems()) {
			expand(item);
		}
	}

	/**
	 * Helper method to expand an item in the property tree.
	 * 
	 * @param item
	 *            tree item to expand.
	 */
	private void expand(TreeItem item) {
		// try to expand the tree using reflection
		try {
			Field field = mPage.getClass().getDeclaredField("viewer"); //$NON-NLS-1$
			field.setAccessible(true);
			Object viewer = field.get(mPage);
			Method method = viewer.getClass().getDeclaredMethod(
					"createChildren", Widget.class); //$NON-NLS-1$
			method.setAccessible(true);
			method.invoke(viewer, item);
		} catch (Exception e) {
			// something went wrong - user can still manually expand
			return;
		}
		item.setExpanded(true);
		// recurse
		for (TreeItem child : item.getItems()) {
			expand(child);
		}
	}

	@Override
	public boolean performOk() {
		ModulePlugin.getDefault().saveModuleProperties(mProperties);
		return true;
	}

	/**
	 * {@link IPropertySource} that for adapting a {@link PropertiesTree} for
	 * the standard property sheet.
	 * 
	 * This class also serves as the ID object for property descriptors.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private final class ModulePropertyNode implements IPropertySource {
		String key;

		/**
		 * Constructor.
		 * 
		 * @param key
		 *            key this node is rooted at
		 */
		ModulePropertyNode(String key) {
			super();
			this.key = key;
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return key.equals(((ModulePropertyNode) obj).key);
		}

		@Override
		public Object getEditableValue() {
			final String value = mProperties.get(key);
			if (value != null)
				return value;
			return null;
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
			for (String prefix : mProperties.getChildKeys(key)) {
				final String[] split = prefix.split("\\."); //$NON-NLS-1$
				final String display = split[split.length - 1];
				if (split.length <= 2)
					descriptors.add(new PropertyDescriptor(
							new ModulePropertyNode(prefix), display));
				else
					descriptors.add(new TextPropertyDescriptor(
							new ModulePropertyNode(prefix),
							display.equals(":") ? "*Instance Defaults*" //$NON-NLS-1$ //$NON-NLS-2$
									: display));
			}
			return (IPropertyDescriptor[]) descriptors
					.toArray(new IPropertyDescriptor[descriptors.size()]);
		}

		@Override
		public Object getPropertyValue(Object id) {
			// The id of a subtree is the subtree itself. By returning it here,
			// the properties viewer will recurse into it and use
			// getEditableValue
			if (id instanceof ModulePropertyNode)
				return id;
			else
				return mProperties.get(key);
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
			if (value == null)
				return;
			String key = ((ModulePropertyNode) id).key;
			mProperties.put(key, (String) value);
		}
	}

}
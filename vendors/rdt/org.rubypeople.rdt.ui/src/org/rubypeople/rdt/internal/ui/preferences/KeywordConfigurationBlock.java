/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
  */
public class KeywordConfigurationBlock extends OptionsConfigurationBlock {

	private static final Key PREF_COMPILER_TASK_TAGS= getRDTUIKey(PreferenceConstants.EDITOR_USER_KEYWORDS);
	
	private static final String ENABLED= RubyCore.ENABLED;
	private static final String DISABLED= RubyCore.DISABLED;	
	
	public static class Keyword {
		public String name;
	}
	
	private class KeywordLabelProvider extends LabelProvider implements ITableLabelProvider, IFontProvider {

		public KeywordLabelProvider() {
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			return null; // RubyPluginImages.get(RubyPluginImages.IMG_OBJS_REFACTORING_INFO);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			return getColumnText(element, 0);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			Keyword task= (Keyword) element;
			if (columnIndex == 0) {
				return task.name;
			} 
			return ""; //$NON-NLS-1$
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
		 */
		public Font getFont(Object element) {
			return null;
		}
	}
	
	private static class TodoTaskSorter extends ViewerSorter {
		public int compare(Viewer viewer, Object e1, Object e2) {
			return collator.compare(((Keyword) e1).name, ((Keyword) e2).name);
		}
	}
	
	private static final int IDX_ADD= 0;
	private static final int IDX_EDIT= 1;
	private static final int IDX_REMOVE= 2;
	
	private IStatus fTaskTagsStatus;
	private ListDialogField fTodoTasksList;


	public KeywordConfigurationBlock(IStatusChangeListener context, IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
						
		KeywordAdapter adapter=  new KeywordAdapter();
		String[] buttons= new String[] {
			PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_add_button, 
			PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_edit_button, 
			PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_remove_button
		};
		fTodoTasksList= new ListDialogField(adapter, buttons, new KeywordLabelProvider());
		fTodoTasksList.setDialogFieldListener(adapter);
		fTodoTasksList.setRemoveButtonIndex(IDX_REMOVE);
		
		String[] columnsHeaders= new String[] {
			PreferencesMessages.TodoTaskConfigurationBlock_markers_tasks_name_column
		};
		
		fTodoTasksList.setTableColumns(new ListDialogField.ColumnsDescription(columnsHeaders, true));
		fTodoTasksList.setViewerSorter(new TodoTaskSorter());
				
		unpackTodoTasks();
		if (fTodoTasksList.getSize() > 0) {
			fTodoTasksList.selectFirstElement();
		} else {
			fTodoTasksList.enableButton(IDX_EDIT, false);
		}
		
		fTaskTagsStatus= new StatusInfo();		
	}
	
	public void setEnabled(boolean isEnabled) {
		fTodoTasksList.setEnabled(isEnabled);
	}
	
	private static Key[] getKeys() {
		return new Key[] {
			PREF_COMPILER_TASK_TAGS
		};	
	}	
	
	public class KeywordAdapter implements IListAdapter, IDialogFieldListener {

		private boolean canEdit(List selectedElements) {
			return selectedElements.size() == 1;
		}

		public void customButtonPressed(ListDialogField field, int index) {
			doTodoButtonPressed(index);
		}

		public void selectionChanged(ListDialogField field) {
			List selectedElements= field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
		}
			
		public void doubleClicked(ListDialogField field) {
			if (canEdit(field.getSelectedElements())) {
				doTodoButtonPressed(IDX_EDIT);
			}
		}

		public void dialogFieldChanged(DialogField field) {
			updateModel(field);
		}			
		
	}
		
	protected Control createContents(Composite parent) {
		setShell(parent.getShell());
		
		Composite markersComposite= createMarkersTabContent(parent);
		
		validateSettings(null, null, null);
	
		return markersComposite;
	}

	private Composite createMarkersTabContent(Composite folder) {
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		
		PixelConverter conv= new PixelConverter(folder);
		
		Composite markersComposite= new Composite(folder, SWT.NULL);
		markersComposite.setLayout(layout);
		markersComposite.setFont(folder.getFont());
		
		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint= conv.convertWidthInCharsToPixels(50);
		Control listControl= fTodoTasksList.getListControl(markersComposite);
		listControl.setLayoutData(data);

		Control buttonsControl= fTodoTasksList.getButtonBox(markersComposite);
		buttonsControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));

		return markersComposite;
	}

	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (!areSettingsEnabled()) {
			return;
		}
		
		if (changedKey != null) {
			if (PREF_COMPILER_TASK_TAGS.equals(changedKey)) {
				fTaskTagsStatus= validateTaskTags();
			} else {
				return;
			}
		} else {
			fTaskTagsStatus= validateTaskTags();
		}		
		IStatus status= fTaskTagsStatus; //StatusUtil.getMostSevere(new IStatus[] { fTaskTagsStatus });
		fContext.statusChanged(status);
	}
	
	private IStatus validateTaskTags() {
		return new StatusInfo();
	}
	
	protected final void updateModel(DialogField field) {
		if (field == fTodoTasksList) {
			StringBuffer tags= new StringBuffer();
			List list= fTodoTasksList.getElements();
			for (int i= 0; i < list.size(); i++) {
				if (i > 0) {
					tags.append(',');
				}
				Keyword elem= (Keyword) list.get(i);
				tags.append(elem.name);
			}
			setValue(PREF_COMPILER_TASK_TAGS, tags.toString());
			validateSettings(PREF_COMPILER_TASK_TAGS, null, null);
		}
	}
	
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#updateControls()
	 */
	protected void updateControls() {
		unpackTodoTasks();
	}
	
	private void unpackTodoTasks() {
		String currTags= getValue(PREF_COMPILER_TASK_TAGS);
		String[] tags= getTokens(currTags, ","); //$NON-NLS-1$
		ArrayList elements= new ArrayList(tags.length);
		for (int i= 0; i < tags.length; i++) {
			Keyword task= new Keyword();
			task.name= tags[i].trim();
			elements.add(task);
		}
		fTodoTasksList.setElements(elements);
		
	}
	
	private void doTodoButtonPressed(int index) {
		Keyword edited= null;
		if (index != IDX_ADD) {
			edited= (Keyword) fTodoTasksList.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			KeywordInputDialog dialog= new KeywordInputDialog(getShell(), edited, fTodoTasksList.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					fTodoTasksList.replaceElement(edited, dialog.getResult());
				} else {
					fTodoTasksList.addElement(dialog.getResult());
				}
			}
		}
	}

}

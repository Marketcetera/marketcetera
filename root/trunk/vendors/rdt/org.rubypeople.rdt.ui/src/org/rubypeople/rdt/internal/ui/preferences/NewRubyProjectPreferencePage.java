/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 ********************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// AW
package org.rubypeople.rdt.internal.ui.preferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
	
/*
 * The page for defaults for loadpath entries in new ruby projects.
 * See PreferenceConstants to access or change these values through public API.
 */
public class NewRubyProjectPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID= "org.rubypeople.rdt.ui.preferences.BuildPathPreferencePage"; //$NON-NLS-1$
	
	private static final String SRCBIN_FOLDERS_IN_NEWPROJ= PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ;
	private static final String SRCBIN_SRCNAME= PreferenceConstants.SRCBIN_SRCNAME;

	private static final String LOADPATH_JRELIBRARY_INDEX= PreferenceConstants.NEWPROJECT_JRELIBRARY_INDEX;
	private static final String LOADPATH_JRELIBRARY_LIST= PreferenceConstants.NEWPROJECT_JRELIBRARY_LIST;

	
	private static String fgDefaultEncoding= System.getProperty("file.encoding"); //$NON-NLS-1$

	public static ILoadpathEntry[] getDefaultJRELibrary() {
		IPreferenceStore store= RubyPlugin.getDefault().getPreferenceStore();
		
		String str= store.getString(LOADPATH_JRELIBRARY_LIST);
		int index= store.getInt(LOADPATH_JRELIBRARY_INDEX);
		
		StringTokenizer tok= new StringTokenizer(str, ";"); //$NON-NLS-1$
		while (tok.hasMoreTokens() && index > 0) {
			tok.nextToken();
			index--;
		}
		
		if (tok.hasMoreTokens()) {
			ILoadpathEntry[] res= decodeJRELibraryLoadpathEntries(tok.nextToken());
			if (res.length > 0) {
				return res;
			}
		}
		return new ILoadpathEntry[] { getJREContainerEntry() };	
	}			
	
	// JRE Entry
	
	public static String decodeJRELibraryDescription(String encoded) {
		int end= encoded.indexOf(' ');
		if (end != -1) {
			return decode(encoded.substring(0, end));
		}
		return ""; //$NON-NLS-1$
	}
	
	private static String decode(String str) {
		try {
			return URLDecoder.decode(str, fgDefaultEncoding);
		} catch (UnsupportedEncodingException e) {
			RubyPlugin.log(e);
		}
		return ""; //$NON-NLS-1$
	}
	
	private static String encode(String str) {
		try {
			return URLEncoder.encode(str, fgDefaultEncoding);
		} catch (UnsupportedEncodingException e) {
			RubyPlugin.log(e);
		}
		return ""; //$NON-NLS-1$
	}	
	
	public static ILoadpathEntry[] decodeJRELibraryLoadpathEntries(String encoded) {
		StringTokenizer tok= new StringTokenizer(encoded, " "); //$NON-NLS-1$
		ArrayList res= new ArrayList();
		while (tok.hasMoreTokens()) {
			try {
				tok.nextToken(); // desc: ignore
				int kind= Integer.parseInt(tok.nextToken());
				IPath path= decodePath(tok.nextToken());
				boolean isExported= Boolean.valueOf(tok.nextToken()).booleanValue();
				switch (kind) {
					case ILoadpathEntry.CPE_SOURCE:
						res.add(RubyCore.newSourceEntry(path));
						break;
					case ILoadpathEntry.CPE_LIBRARY:
						res.add(RubyCore.newLibraryEntry(path, isExported));
						break;
					case ILoadpathEntry.CPE_VARIABLE:
						res.add(RubyCore.newVariableEntry(path, isExported));
						break;
					case ILoadpathEntry.CPE_PROJECT:
						res.add(RubyCore.newProjectEntry(path, isExported));
						break;
					case ILoadpathEntry.CPE_CONTAINER:
						res.add(RubyCore.newContainerEntry(path, isExported));
						break;
				}								
			} catch (NumberFormatException e) {
				String message= PreferencesMessages.NewRubyProjectPreferencePage_error_decode; 
				RubyPlugin.log(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, message, e));
			} catch (NoSuchElementException e) {
				String message= PreferencesMessages.NewRubyProjectPreferencePage_error_decode; 
				RubyPlugin.log(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, message, e));
			}
		}
		return (ILoadpathEntry[]) res.toArray(new ILoadpathEntry[res.size()]);	
	}
	
	
	public static String encodeJRELibrary(String desc, ILoadpathEntry[] cpentries) {
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < cpentries.length; i++) {
			ILoadpathEntry entry= cpentries[i];
			buf.append(encode(desc));
			buf.append(' ');
			buf.append(entry.getEntryKind());
			buf.append(' ');
			buf.append(encodePath(entry.getPath()));
			buf.append(' ');
			buf.append(entry.isExported());
			buf.append(' ');
		}
		return buf.toString();
	}
	
	private static String encodePath(IPath path) {
		if (path == null) {
			return "#"; //$NON-NLS-1$
		} else if (path.isEmpty()) {
			return "&"; //$NON-NLS-1$
		} else {
			return encode(path.toPortableString());
		}
	}
	
	private static IPath decodePath(String str) {
		if ("#".equals(str)) { //$NON-NLS-1$
			return null;
		} else if ("&".equals(str)) { //$NON-NLS-1$
			return Path.EMPTY;
		} else {
			return Path.fromPortableString(decode(str));
		}
	}
	
	
	private ArrayList fCheckBoxes;
	private ArrayList fRadioButtons;
	private ArrayList fTextControls;
	
	private SelectionListener fSelectionListener;
	private ModifyListener fModifyListener;
	
	private Text fSrcFolderNameText;

	private Combo fJRECombo;

	private Button fProjectAsSourceFolder;
	private Button fFoldersAsSourceFolder;

	private Label fSrcFolderNameLabel;

	public NewRubyProjectPreferencePage() {
		super();
		setPreferenceStore(RubyPlugin.getDefault().getPreferenceStore());
		setDescription(PreferencesMessages.NewRubyProjectPreferencePage_description); 
	
		// title used when opened programatically
		setTitle(PreferencesMessages.NewRubyProjectPreferencePage_title); 
		
		fRadioButtons= new ArrayList();
		fCheckBoxes= new ArrayList();
		fTextControls= new ArrayList();
		
		fSelectionListener= new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
		};
		
		fModifyListener= new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlModified(e.widget);
			}
		};
		
	}

	public static void initDefaults(IPreferenceStore store) {
		store.setDefault(SRCBIN_FOLDERS_IN_NEWPROJ, false);
		store.setDefault(SRCBIN_SRCNAME, "src"); //$NON-NLS-1$
		
		store.setDefault(LOADPATH_JRELIBRARY_LIST, getDefaultJRELibraries());
		store.setDefault(LOADPATH_JRELIBRARY_INDEX, 0); 
	}
	
	private static String getDefaultJRELibraries() {
		StringBuffer buf= new StringBuffer();
		ILoadpathEntry cntentry= getJREContainerEntry();
		buf.append(encodeJRELibrary(PreferencesMessages.NewRubyProjectPreferencePage_jre_container_description, new ILoadpathEntry[] { cntentry} )); 
		buf.append(';');
		ILoadpathEntry varentry= getJREVariableEntry();
		buf.append(encodeJRELibrary(PreferencesMessages.NewRubyProjectPreferencePage_jre_variable_description, new ILoadpathEntry[] { varentry })); 
		buf.append(';');
		return buf.toString();
	}
	
	private static ILoadpathEntry getJREContainerEntry() {
		return RubyCore.newContainerEntry(new Path(RubyRuntime.RUBY_CONTAINER)); //$NON-NLS-1$
	}
	
	private static ILoadpathEntry getJREVariableEntry() {
		return RubyCore.newVariableEntry(new Path(RubyRuntime.RUBYLIB_VARIABLE)); //$NON-NLS-1$
	}	

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}		
	
	/**
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IRubyHelpContextIds.NEW_JAVA_PROJECT_PREFERENCE_PAGE);
	}	


	private Button addRadioButton(Composite parent, String label, String key, String value, int indent) { 
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.horizontalIndent= indent;
		
		Button button= new Button(parent, SWT.RADIO);
		button.setText(label);
		button.setData(new String[] { key, value });
		button.setLayoutData(gd);

		button.setSelection(value.equals(getPreferenceStore().getString(key)));
		
		fRadioButtons.add(button);
		return button;
	}
	
	private Text addTextControl(Composite parent, Label labelControl, String key, int indent) {
		GridData gd= new GridData();
		gd.horizontalIndent= indent;
		
		labelControl.setLayoutData(gd);
		
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint= convertWidthInCharsToPixels(30);
		
		Text text= new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setText(getPreferenceStore().getString(key));
		text.setData(key);
		text.setLayoutData(gd);
		
		fTextControls.add(text);
		return text;
	}	
	
	
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.verticalSpacing= convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.numColumns= 2;
		result.setLayout(layout);
		
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan= 2;
		
		Group sourceFolderGroup= new Group(result, SWT.NONE);
		layout= new GridLayout();
		layout.numColumns= 2;
		sourceFolderGroup.setLayout(layout);
		sourceFolderGroup.setLayoutData(gd);
		sourceFolderGroup.setText(PreferencesMessages.NewRubyProjectPreferencePage_sourcefolder_label); 
		
		int indent= 0;
		
		fProjectAsSourceFolder= addRadioButton(sourceFolderGroup, PreferencesMessages.NewRubyProjectPreferencePage_sourcefolder_project, SRCBIN_FOLDERS_IN_NEWPROJ, IPreferenceStore.FALSE, indent); 
		fProjectAsSourceFolder.addSelectionListener(fSelectionListener);

		fFoldersAsSourceFolder= addRadioButton(sourceFolderGroup, PreferencesMessages.NewRubyProjectPreferencePage_sourcefolder_folder, SRCBIN_FOLDERS_IN_NEWPROJ, IPreferenceStore.TRUE, indent); 
		fFoldersAsSourceFolder.addSelectionListener(fSelectionListener);
		
		indent= convertWidthInCharsToPixels(4);

		fSrcFolderNameLabel= new Label(sourceFolderGroup, SWT.NONE);
		fSrcFolderNameLabel.setText(PreferencesMessages.NewRubyProjectPreferencePage_folders_src); 
		fSrcFolderNameText= addTextControl(sourceFolderGroup, fSrcFolderNameLabel, SRCBIN_SRCNAME, indent); 
		fSrcFolderNameText.addModifyListener(fModifyListener);

		String[] jreNames= getJRENames();
		if (jreNames.length > 0) {
			Label jreSelectionLabel= new Label(result, SWT.NONE);
			jreSelectionLabel.setText(PreferencesMessages.NewRubyProjectPreferencePage_jrelibrary_label); 
			jreSelectionLabel.setLayoutData(new GridData());
		
			int index= getPreferenceStore().getInt(LOADPATH_JRELIBRARY_INDEX);
			fJRECombo= new Combo(result, SWT.READ_ONLY);
			fJRECombo.setItems(jreNames);
			fJRECombo.select(index);
			fJRECombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		}
					
		validateFolders();
	
		Dialog.applyDialogFont(result);
		return result;
	}
	
	private void validateFolders() {
		boolean useFolders= fFoldersAsSourceFolder.getSelection();
		
		fSrcFolderNameText.setEnabled(useFolders);
		fSrcFolderNameLabel.setEnabled(useFolders);	
		if (useFolders) {
			String srcName= fSrcFolderNameText.getText();
			if (srcName.length() == 0) {
				updateStatus(new StatusInfo(IStatus.ERROR,  PreferencesMessages.NewRubyProjectPreferencePage_folders_error_namesempty)); 
				return;
			}
			IWorkspace workspace= RubyPlugin.getWorkspace();
			IProject dmy= workspace.getRoot().getProject("project"); //$NON-NLS-1$
			
			IStatus status;
			IPath srcPath= dmy.getFullPath().append(srcName);
			if (srcName.length() != 0) {
				status= workspace.validatePath(srcPath.toString(), IResource.FOLDER);
				if (!status.isOK()) {
					String message= Messages.format(PreferencesMessages.NewRubyProjectPreferencePage_folders_error_invalidsrcname, status.getMessage()); 
					updateStatus(new StatusInfo(IStatus.ERROR, message));
					return;
				}
			}
			ILoadpathEntry entry= RubyCore.newSourceEntry(srcPath);
			status= RubyConventions.validateLoadpath(RubyCore.create(dmy), new ILoadpathEntry[] { entry }, null);
			if (!status.isOK()) {
				String message= PreferencesMessages.NewRubyProjectPreferencePage_folders_error_invalidcp; 
				updateStatus(new StatusInfo(IStatus.ERROR, message));
				return;
			}
		}
		updateStatus(new StatusInfo()); // set to OK
	}
		
	private void updateStatus(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}		
	
	private void controlChanged(Widget widget) {
		if (widget == fFoldersAsSourceFolder || widget == fProjectAsSourceFolder) {
			validateFolders();
		}
	}
	
	private void controlModified(Widget widget) {
		if (widget == fSrcFolderNameText) {
			validateFolders();
		}
	}	
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			button.setSelection(store.getDefaultBoolean(key));
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			String[] info= (String[]) button.getData();
			button.setSelection(info[1].equals(store.getDefaultString(info[0])));
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			text.setText(store.getDefaultString(key));
		}
		if (fJRECombo != null) {
			fJRECombo.select(store.getDefaultInt(LOADPATH_JRELIBRARY_INDEX));
		}
		
		validateFolders();
		super.performDefaults();
	}

	/*
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			store.setValue(key, button.getSelection());
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			if (button.getSelection()) {
				String[] info= (String[]) button.getData();
				store.setValue(info[0], info[1]);
			}
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			store.setValue(key, text.getText());
		}
		
		if (fJRECombo != null) {
			store.setValue(LOADPATH_JRELIBRARY_INDEX, fJRECombo.getSelectionIndex());
		}
		
		RubyPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}
	
	private String[] getJRENames() {
		String prefString= getPreferenceStore().getString(LOADPATH_JRELIBRARY_LIST);
		ArrayList list= new ArrayList();
		StringTokenizer tok= new StringTokenizer(prefString, ";"); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			list.add(decodeJRELibraryDescription(tok.nextToken()));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

}



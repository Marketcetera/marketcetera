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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementImageDescriptor;
import org.rubypeople.rdt.ui.viewsupport.ImageDescriptorRegistry;

public class MembersOrderPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    public static final String PREF_ID= "org.rubypeople.rdt.ui.preferences.MembersOrderPreferencePage"; //$NON-NLS-1$
    
    private static final String ALL_SORTMEMBER_ENTRIES= "T,SF,SM,F,C,M"; //$NON-NLS-1$
    private static final String ALL_VISIBILITY_ENTRIES= "B,V,R"; //$NON-NLS-1$
    private static final String PREF_OUTLINE_SORT_OPTION= PreferenceConstants.APPEARANCE_MEMBER_SORT_ORDER;
    private static final String PREF_VISIBILITY_SORT_OPTION= PreferenceConstants.APPEARANCE_VISIBILITY_SORT_ORDER;
    private static final String PREF_USE_VISIBILITY_SORT_OPTION= PreferenceConstants.APPEARANCE_ENABLE_VISIBILITY_SORT_ORDER;
        
    public static final String CONSTRUCTORS= "C"; //$NON-NLS-1$
    public static final String FIELDS= "F"; //$NON-NLS-1$
    public static final String METHODS= "M"; //$NON-NLS-1$
    public static final String STATIC_METHODS= "SM"; //$NON-NLS-1$
    public static final String STATIC_FIELDS= "SF"; //$NON-NLS-1$
    public static final String TYPES= "T"; //$NON-NLS-1$
    
    public static final String PUBLIC= "B";  //$NON-NLS-1$
    public static final String PRIVATE= "V"; //$NON-NLS-1$
    public static final String PROTECTED= "R"; //$NON-NLS-1$

    private boolean fUseVisibilitySort;
    private ListDialogField fSortOrderList;
    private ListDialogField fVisibilityOrderList;
    private SelectionButtonDialogField fUseVisibilitySortField;

    private static boolean isValidEntries(List entries, String entryString) {
        StringTokenizer tokenizer= new StringTokenizer(entryString, ","); //$NON-NLS-1$
        int i= 0;
        for (; tokenizer.hasMoreTokens(); i++) {
            String token= tokenizer.nextToken();
            if (!entries.contains(token))
                return false;
        }
        return i == entries.size();
    }

    public MembersOrderPreferencePage() {
        //set the preference store
        setPreferenceStore(RubyPlugin.getDefault().getPreferenceStore());
        
        setDescription(PreferencesMessages.MembersOrderPreferencePage_label_description); 

        String memberSortString= getPreferenceStore().getString(PREF_OUTLINE_SORT_OPTION);
        
        String upLabel= PreferencesMessages.MembersOrderPreferencePage_category_button_up; 
        String downLabel= PreferencesMessages.MembersOrderPreferencePage_category_button_down; 

        // category sort
        
        fSortOrderList= new ListDialogField(null,  new String[] { upLabel, downLabel }, new MemberSortLabelProvider());
        fSortOrderList.setDownButtonIndex(1);
        fSortOrderList.setUpButtonIndex(0);
        
        //validate entries stored in store, false get defaults
        List entries= parseList(memberSortString);
        if (!isValidEntries(entries, ALL_SORTMEMBER_ENTRIES)) {
            memberSortString= getPreferenceStore().getDefaultString(PREF_OUTLINE_SORT_OPTION);
            entries= parseList(memberSortString);
        }
        
        fSortOrderList.setElements(entries);
        
        // visibility sort

        fUseVisibilitySort= getPreferenceStore().getBoolean(PREF_USE_VISIBILITY_SORT_OPTION);

        String visibilitySortString= getPreferenceStore().getString(PREF_VISIBILITY_SORT_OPTION); 
        
        upLabel= PreferencesMessages.MembersOrderPreferencePage_visibility_button_up; 
        downLabel= PreferencesMessages.MembersOrderPreferencePage_visibility_button_down; 
        
        fVisibilityOrderList= new ListDialogField(null, new String[] { upLabel, downLabel }, new VisibilitySortLabelProvider());
        fVisibilityOrderList.setDownButtonIndex(1);
        fVisibilityOrderList.setUpButtonIndex(0);
        
        //validate entries stored in store, false get defaults
        entries= parseList(visibilitySortString);
        if (!isValidEntries(entries, ALL_VISIBILITY_ENTRIES)) {
            visibilitySortString= getPreferenceStore().getDefaultString(PREF_VISIBILITY_SORT_OPTION);
            entries= parseList(visibilitySortString);
        }
        fVisibilityOrderList.setElements(entries);
    }
    
    private static List parseList(String string) {
        StringTokenizer tokenizer= new StringTokenizer(string, ","); //$NON-NLS-1$
        List entries= new ArrayList();
        for (int i= 0; tokenizer.hasMoreTokens(); i++) {
            String token= tokenizer.nextToken();
            entries.add(token);
        }
        return entries;
    }
    
    /*
     * @see PreferencePage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IRubyHelpContextIds.SORT_ORDER_PREFERENCE_PAGE);
    }

    /*
     * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        // Create both the dialog lists
        Composite sortComposite= new Composite(parent, SWT.NONE);
        sortComposite.setFont(parent.getFont());

        GridLayout layout= new GridLayout();
        layout.numColumns= 2;
        layout.marginWidth= 0;
        layout.marginHeight= 0;
        sortComposite.setLayout(layout);

        GridData gd= new GridData();
        gd.verticalAlignment= GridData.FILL;
        gd.horizontalAlignment= GridData.FILL_HORIZONTAL;
        sortComposite.setLayoutData(gd);

        createListDialogField(sortComposite, fSortOrderList);
        
        fUseVisibilitySortField= new SelectionButtonDialogField(SWT.CHECK);
        fUseVisibilitySortField.setDialogFieldListener(new IDialogFieldListener() {
            public void dialogFieldChanged(DialogField field) {
                fVisibilityOrderList.setEnabled(fUseVisibilitySortField.isSelected());
            }
        });
        fUseVisibilitySortField.setLabelText(PreferencesMessages.MembersOrderPreferencePage_usevisibilitysort_label); 
        fUseVisibilitySortField.doFillIntoGrid(sortComposite, 2);
        fUseVisibilitySortField.setSelection(fUseVisibilitySort);
        
        createListDialogField(sortComposite, fVisibilityOrderList);
        fVisibilityOrderList.setEnabled(fUseVisibilitySortField.isSelected());
        
        Dialog.applyDialogFont(sortComposite);
        
        return sortComposite;
    }
    

    private void createListDialogField(Composite composite, ListDialogField dialogField) {
        Control list= dialogField.getListControl(composite);
        GridData gd= new GridData();
        gd.horizontalAlignment= GridData.FILL;
        gd.grabExcessHorizontalSpace= true;
        gd.verticalAlignment= GridData.FILL;
        gd.grabExcessVerticalSpace= true;
        gd.widthHint= convertWidthInCharsToPixels(50);

        list.setLayoutData(gd);
        
        Composite buttons= dialogField.getButtonBox(composite);
        gd= new GridData();
        gd.horizontalAlignment= GridData.FILL;
        gd.grabExcessHorizontalSpace= false;
        gd.verticalAlignment= GridData.FILL;
        gd.grabExcessVerticalSpace= true;
        
        buttons.setLayoutData(gd);
    }

    /*
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    /*
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        IPreferenceStore prefs= RubyPlugin.getDefault().getPreferenceStore();
        String str= prefs.getDefaultString(PREF_OUTLINE_SORT_OPTION);
        if (str != null)
            fSortOrderList.setElements(parseList(str));
        else
            fSortOrderList.setElements(parseList(ALL_SORTMEMBER_ENTRIES));
    
        str= prefs.getDefaultString(PREF_VISIBILITY_SORT_OPTION);
        if (str != null)
            fVisibilityOrderList.setElements(parseList(str));
        else
            fVisibilityOrderList.setElements(parseList(ALL_VISIBILITY_ENTRIES));
    
        fUseVisibilitySortField.setSelection(prefs.getDefaultBoolean(PREF_USE_VISIBILITY_SORT_OPTION));
        
        super.performDefaults();
    }

    /*
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    //reorders elements in the Outline based on selection
    public boolean performOk() {

        //save preferences for both dialog lists
        IPreferenceStore store= getPreferenceStore();
        updateList(store, fSortOrderList, PREF_OUTLINE_SORT_OPTION);
        updateList(store, fVisibilityOrderList, PREF_VISIBILITY_SORT_OPTION);
        
        //update the button setting
        store.setValue(PREF_USE_VISIBILITY_SORT_OPTION, fUseVisibilitySortField.isSelected());
        RubyPlugin.getDefault().savePluginPreferences();
        
        return true;
    }
    
    private void updateList(IPreferenceStore store, ListDialogField list, String str) {
        StringBuffer buf= new StringBuffer();
        List curr= list.getElements();
        for (Iterator iter= curr.iterator(); iter.hasNext();) {
            String s= (String) iter.next();
            buf.append(s);
            buf.append(',');
        }
        store.setValue(str, buf.toString());
    }

    private class MemberSortLabelProvider extends LabelProvider {

        public MemberSortLabelProvider() {
        }
        
        /*
        * @see org.eclipse.jface.viewers.ILabelProvider#getImage(Object)
        */
        public Image getImage(Object element) {
            //access to image registry
            ImageDescriptorRegistry registry= RubyPlugin.getImageDescriptorRegistry();
            ImageDescriptor descriptor= null;

            if (element instanceof String) {
                int visibility= IMethod.PUBLIC;
                String s= (String) element;
                if (s.equals(FIELDS)) {
                    //0 will give the default field image   
                    descriptor= RubyElementImageProvider.getConstantImageDescriptor();
                } else if (s.equals(CONSTRUCTORS)) {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(visibility);
                    //add a constructor adornment to the image descriptor
                    descriptor= new RubyElementImageDescriptor(descriptor, RubyElementImageDescriptor.CONSTRUCTOR, RubyElementImageProvider.SMALL_SIZE);
                } else if (s.equals(METHODS)) {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(visibility);
                } else if (s.equals(STATIC_FIELDS)) {
                    descriptor= RubyElementImageProvider.getConstantImageDescriptor();
                    //add a static fields adornment to the image descriptor
                    descriptor= new RubyElementImageDescriptor(descriptor, RubyElementImageDescriptor.STATIC, RubyElementImageProvider.SMALL_SIZE);
                } else if (s.equals(STATIC_METHODS)) {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(visibility);
                    //add a static methods adornment to the image descriptor
                    descriptor= new RubyElementImageDescriptor(descriptor, RubyElementImageDescriptor.STATIC, RubyElementImageProvider.SMALL_SIZE);
                } else if (s.equals(TYPES)) {
                    descriptor= RubyElementImageProvider.getTypeImageDescriptor(false, false, false);
                } else {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(IMethod.PUBLIC);
                }
                return registry.get(descriptor);
            }
            return null;
        }

        /*
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(Object)
         */
        public String getText(Object element) {

            if (element instanceof String) {
                String s= (String) element;
                if (s.equals(FIELDS)) {
                    return PreferencesMessages.MembersOrderPreferencePage_fields_label; 
                } else if (s.equals(METHODS)) {
                    return PreferencesMessages.MembersOrderPreferencePage_methods_label; 
                } else if (s.equals(STATIC_FIELDS)) {
                    return PreferencesMessages.MembersOrderPreferencePage_staticfields_label; 
                } else if (s.equals(STATIC_METHODS)) {
                    return PreferencesMessages.MembersOrderPreferencePage_staticmethods_label; 
                } else if (s.equals(CONSTRUCTORS)) {
                    return PreferencesMessages.MembersOrderPreferencePage_constructors_label; 
                } else if (s.equals(TYPES)) {
                    return PreferencesMessages.MembersOrderPreferencePage_types_label; 
                }
            }
            return ""; //$NON-NLS-1$
        }
    }

    
    private class VisibilitySortLabelProvider extends LabelProvider {
        
        public VisibilitySortLabelProvider() {
        }
        
        /*
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(Object)
         */
        public Image getImage(Object element) {
            //access to image registry
            ImageDescriptorRegistry registry= RubyPlugin.getImageDescriptorRegistry();
            ImageDescriptor descriptor= null;
            
            if (element instanceof String) {
                String s= (String) element;
                if (s.equals(PUBLIC)) {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(IMethod.PUBLIC);
                } else if (s.equals(PRIVATE)) {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(IMethod.PRIVATE);
                } else if (s.equals(PROTECTED)) {
                    descriptor= RubyElementImageProvider.getMethodImageDescriptor(IMethod.PROTECTED);
                } 
                return registry.get(descriptor);
            }
            return null;
        }
        
        /*
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(Object)
         */
        public String getText(Object element) {
            if (element instanceof String) {
                String s= (String) element;
                
                if (s.equals(PUBLIC)) {
                    return PreferencesMessages.MembersOrderPreferencePage_public_label; 
                } else if (s.equals(PRIVATE)) {
                    return PreferencesMessages.MembersOrderPreferencePage_private_label; 
                } else if (s.equals(PROTECTED)) {
                    return PreferencesMessages.MembersOrderPreferencePage_protected_label; 
                }
            }
            return ""; //$NON-NLS-1$
        }
    }

    

}

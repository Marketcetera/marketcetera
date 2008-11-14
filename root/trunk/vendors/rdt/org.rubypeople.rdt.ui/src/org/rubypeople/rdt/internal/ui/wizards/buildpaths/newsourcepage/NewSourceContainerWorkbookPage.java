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
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier.ILoadpathModifierListener;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.preferences.ScrolledPageContent;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.util.ViewerPane;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathBasePage;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElementAttribute;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;

public class NewSourceContainerWorkbookPage extends BuildPathBasePage implements ILoadpathModifierListener {
    
    public static final String OPEN_SETTING= "org.rubypeople.rdt.internal.ui.wizards.buildpaths.NewSourceContainerPage.openSetting";  //$NON-NLS-1$
    
    private ListDialogField fClassPathList;
    private HintTextGroup fHintTextGroup;
    private DialogPackageExplorer fPackageExplorer;
	private final BuildPathsBlock fBuildPathsBlock;
	
	private IRubyProject fRubyProject;


    /**
     * Constructor of the <code>NewSourceContainerWorkbookPage</code> which consists of 
     * a tree representing the project, a toolbar with the available actions, an area 
     * containing hyperlinks that perform the same actions as those in the toolbar but 
     * additionally with some short description.
     * 
     * @param classPathList
     * @param outputLocationField
     * @param context a runnable context, can be <code>null</code>
     */
    public NewSourceContainerWorkbookPage(ListDialogField classPathList, IRunnableContext context, BuildPathsBlock buildPathsBlock) {
        fClassPathList= classPathList;
		fBuildPathsBlock= buildPathsBlock;

		fPackageExplorer= new DialogPackageExplorer();
		fHintTextGroup= new HintTextGroup(fPackageExplorer, context, this);
     }
    
    /**
     * Initialize the controls displaying
     * the content of the java project and saving 
     * the '.classpath' and '.project' file.
     * 
     * Must be called before initializing the 
     * controls using <code>getControl(Composite)</code>.
     * 
     * @param javaProject the current java project
     */
    public void init(IRubyProject javaProject) {
		fRubyProject= javaProject;
        fHintTextGroup.setRubyProject(javaProject);
        
        fPackageExplorer.setInput(javaProject);
    }
    
     
    /**
     * Initializes controls and return composite containing
     * these controls.
     * 
     * Before calling this method, make sure to have 
     * initialized this instance with a java project 
     * using <code>init(IRubyProject)</code>.
     * 
     * @param parent the parent composite
     * @return composite containing controls
     * 
     * @see #init(IRubyProject)
     */
    public Control getControl(Composite parent) {
        final int[] sashWeight= {60};
        final IPreferenceStore preferenceStore= RubyPlugin.getDefault().getPreferenceStore();
        preferenceStore.setDefault(OPEN_SETTING, true);
        
        // ScrolledPageContent is needed for resizing on expand the expandable composite
        ScrolledPageContent scrolledContent = new ScrolledPageContent(parent);
        Composite body= scrolledContent.getBody();
        body.setLayout(new GridLayout());
        
        final SashForm sashForm= new SashForm(body, SWT.VERTICAL | SWT.NONE);
        sashForm.setFont(sashForm.getFont());
        
        ViewerPane pane= new ViewerPane(sashForm, SWT.BORDER | SWT.FLAT);
        pane.setContent(fPackageExplorer.createControl(pane));
		fPackageExplorer.setContentProvider();
        
        final ExpandableComposite excomposite= new ExpandableComposite(sashForm, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
        excomposite.setFont(sashForm.getFont());
        excomposite.setText(NewWizardMessages.NewSourceContainerWorkbookPage_HintTextGroup_title);
        final boolean isExpanded= preferenceStore.getBoolean(OPEN_SETTING);
        excomposite.setExpanded(isExpanded);
        excomposite.addExpansionListener(new ExpansionAdapter() {
                       public void expansionStateChanged(ExpansionEvent e) {
                           ScrolledPageContent parentScrolledComposite= getParentScrolledComposite(excomposite);
                           if (parentScrolledComposite != null) {
                              boolean expanded= excomposite.isExpanded();
                              parentScrolledComposite.reflow(true);
                              adjustSashForm(sashWeight, sashForm, expanded);
                              preferenceStore.setValue(OPEN_SETTING, expanded);
                           }
                       }
                 });
        
        excomposite.setClient(fHintTextGroup.createControl(excomposite));
		
	    final DialogPackageExplorerActionGroup actionGroup= new DialogPackageExplorerActionGroup(fHintTextGroup, this);
		   
		           
        // Create toolbar with actions on the left
        ToolBarManager tbm= actionGroup.createLeftToolBarManager(pane);
        pane.setTopCenter(null);
        pane.setTopLeft(tbm.getControl());
        
        // Create toolbar with help on the right
        tbm= actionGroup.createLeftToolBar(pane);
        pane.setTopRight(tbm.getControl());
        
        fHintTextGroup.setActionGroup(actionGroup);
        fPackageExplorer.setActionGroup(actionGroup);
        actionGroup.addListener(fHintTextGroup);
        
		sashForm.setWeights(new int[] {60, 40});
		adjustSashForm(sashWeight, sashForm, excomposite.isExpanded());
		GridData gd= new GridData(GridData.FILL_BOTH);
		PixelConverter converter= new PixelConverter(parent);
		gd.heightHint= converter.convertHeightInCharsToPixels(20);
		sashForm.setLayoutData(gd);
                
        parent.layout(true);

        return scrolledContent;
    }
    
    /**
     * Adjust the size of the sash form.
     * 
     * @param sashWeight the weight to be read or written
     * @param sashForm the sash form to apply the new weights to
     * @param isExpanded <code>true</code> if the expandable composite is 
     * expanded, <code>false</code> otherwise
     */
    private void adjustSashForm(int[] sashWeight, SashForm sashForm, boolean isExpanded) {
        if (isExpanded) {
            int upperWeight= sashWeight[0];
            sashForm.setWeights(new int[]{upperWeight, 100 - upperWeight});
        }
        else {
            // TODO Dividing by 10 because of https://bugs.eclipse.org/bugs/show_bug.cgi?id=81939
            sashWeight[0]= sashForm.getWeights()[0] / 10;
            sashForm.setWeights(new int[]{95, 5});
        }
        sashForm.layout(true);
    }
    
    /**
     * Get the scrolled page content of the given control by 
     * traversing the parents.
     * 
     * @param control the control to get the scrolled page content for 
     * @return the scrolled page content or <code>null</code> if none found
     */
    private ScrolledPageContent getParentScrolledComposite(Control control) {
       Control parent= control.getParent();
       while (!(parent instanceof ScrolledPageContent)) {
           parent= parent.getParent();
       }
       if (parent instanceof ScrolledPageContent) {
           return (ScrolledPageContent) parent;
       }
       return null;
   }
    
    /**
     * Get the active shell.
     * 
     * @return the active shell
     */
    private Shell getShell() {
        return RubyPlugin.getActiveWorkbenchShell();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage#getSelection()
     */
    public List getSelection() {
        List selectedList= new ArrayList();
        
        IRubyProject project= fHintTextGroup.getRubyProject();
        try {
            List list= fHintTextGroup.getSelection().toList();
            List existingEntries= LoadpathModifier.getExistingEntries(project);
        
            for(int i= 0; i < list.size(); i++) {
                Object obj= list.get(i);
                if (obj instanceof ISourceFolderRoot) {
                    ISourceFolderRoot element= (ISourceFolderRoot)obj;
                    CPListElement cpElement= LoadpathModifier.getLoadpathEntry(existingEntries, element); 
                    selectedList.add(cpElement);
                }
                else if (obj instanceof IRubyProject) {
                    ILoadpathEntry entry= LoadpathModifier.getLoadpathEntryFor(project.getPath(), project, ILoadpathEntry.CPE_SOURCE);
                    if (entry == null)
                        continue;
                    CPListElement cpElement= CPListElement.createFromExisting(entry, project);
                    selectedList.add(cpElement);
                }
            }
        } catch (RubyModelException e) {
            return new ArrayList();
        }
        return selectedList;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage#setSelection(java.util.List)
     */
    public void setSelection(List selection, boolean expand) {
		// page switch
		
        if (selection.size() == 0)
            return;
	    
		List cpEntries= new ArrayList();
		
		for (int i= 0; i < selection.size(); i++) {
			Object obj= selection.get(i);
			if (obj instanceof CPListElement) {
				CPListElement element= (CPListElement) obj;
				if (element.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
					cpEntries.add(element);
				}
			} else if (obj instanceof CPListElementAttribute) {
				CPListElementAttribute attribute= (CPListElementAttribute)obj;
				CPListElement element= attribute.getParent();
				if (element.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
					cpEntries.add(element);
				}
			}
		}
		
        // refresh classpath
        List list= fClassPathList.getElements();
        ILoadpathEntry[] entries= new ILoadpathEntry[list.size()];
        for(int i= 0; i < list.size(); i++) {
            CPListElement entry= (CPListElement) list.get(i);
            entries[i]= entry.getLoadpathEntry(); 
        }
        try {
			fRubyProject.setRawLoadpath(entries, null);
            fPackageExplorer.refresh();
        } catch (RubyModelException e) {
            RubyPlugin.log(e);
        }
        
        fPackageExplorer.setSelection(cpEntries);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage#isEntryKind(int)
     */
    public boolean isEntryKind(int kind) {
        return kind == ILoadpathEntry.CPE_SOURCE;
    }
    
    /**
     * Update <code>fClassPathList</code>.
     */
    public void classpathEntryChanged(List newEntries) {
        fClassPathList.setElements(newEntries);
    }
}

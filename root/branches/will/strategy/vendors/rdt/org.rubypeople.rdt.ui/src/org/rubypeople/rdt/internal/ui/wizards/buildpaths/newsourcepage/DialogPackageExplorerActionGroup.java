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
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.actions.ActionContext;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.buildpath.AddSelectedSourceFolderOperation;
import org.rubypeople.rdt.internal.corext.buildpath.CreateFolderOperation;
import org.rubypeople.rdt.internal.corext.buildpath.EditFiltersOperation;
import org.rubypeople.rdt.internal.corext.buildpath.ExcludeOperation;
import org.rubypeople.rdt.internal.corext.buildpath.ILoadpathInformationProvider;
import org.rubypeople.rdt.internal.corext.buildpath.IPackageExplorerActionListener;
import org.rubypeople.rdt.internal.corext.buildpath.LinkedSourceFolderOperation;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier;
import org.rubypeople.rdt.internal.corext.buildpath.PackageExplorerActionEvent;
import org.rubypeople.rdt.internal.corext.buildpath.RemoveFromLoadpathOperation;
import org.rubypeople.rdt.internal.corext.buildpath.ResetAllOperation;
import org.rubypeople.rdt.internal.corext.buildpath.UnexcludeOperation;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier.ILoadpathModifierListener;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.actions.CompositeActionGroup;
import org.rubypeople.rdt.internal.ui.packageview.LoadPathContainer;
import org.rubypeople.rdt.internal.ui.util.ViewerPane;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElementAttribute;

/**
 * Action group for the package explorer. Creates and manages a set 
 * of <code>LoadpathModifierOperation</code>s and creates a <code>ToolBarManager</code> 
 * on request. Based on this operations, <code>LoadpathModifierAction</code>s are generated. 
 * The available operations are:
 * 
 * @see org.eclipse.jdt.internal.corext.buildpath.AddSelectedSourceFolderOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.RemoveFromLoadpathOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.IncludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.UnincludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.ExcludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.UnexcludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.EditFiltersOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.ResetOperation
 */
public class DialogPackageExplorerActionGroup extends CompositeActionGroup {
    
    public static class DialogExplorerActionContext extends ActionContext {
        private IRubyProject fRubyProject;
        private List fSelectedElements;

        /**
         * Constructor to create an action context for the dialog package explorer.
         * 
         * For reasons of completeness, the selection of the super class 
         * <code>ActionContext</code> is also set, but is not intendet to be used.
         * 
         * @param selection the current selection
         * @param jProject the element's Ruby project
         */
        public DialogExplorerActionContext(ISelection selection, IRubyProject jProject) {
            super(null);
            fRubyProject= jProject;
            fSelectedElements= ((IStructuredSelection)selection).toList();
            IStructuredSelection structuredSelection= new StructuredSelection(new Object[] {fSelectedElements, jProject});
            super.setSelection(structuredSelection);
        }
        
        /**
         * Constructor to create an action context for the dialog package explorer.
         * 
         * For reasons of completeness, the selection of the super class 
         * <code>ActionContext</code> is also set, but is not intendet to be used.
         * 
         * @param selectedElements a list of currently selected elements
         * @param jProject the element's Ruby project
         */
        public DialogExplorerActionContext(List selectedElements, IRubyProject jProject) {
            super(null);
            fRubyProject= jProject;
            fSelectedElements= selectedElements;
            IStructuredSelection structuredSelection= new StructuredSelection(new Object[] {fSelectedElements, jProject});
            super.setSelection(structuredSelection);
        }
        
        public IRubyProject getRubyProject() {
            return fRubyProject;
        }
        
        public List getSelectedElements() {
            return fSelectedElements;
        }
    }
    
    /** Ruby project */
    public static final int RUBY_PROJECT= 0x01;
    /** Package fragment root */
    public static final int SOURCE_FOLDER_ROOT= 0x02;
    /** Package fragment */
    public static final int SOURCE_FOLDER= 0x03;
    /** Compilation unit */
    public static final int RUBY_SCRIPT= 0x04;
    /** File */
    public static final int FILE= 0x05;
    /** Normal folder */
    public static final int FOLDER= 0x06;
    /** Excluded folder */
    public static final int EXCLUDED_FOLDER= 0x07;
    /** Excluded file */
    public static final int EXCLUDED_FILE= 0x08;
    /** Default output folder */
    public static final int DEFAULT_OUTPUT= 0x09;
    /** Included file */
    public static final int INCLUDED_FILE= 0xA;
    /** Included folder */
    public static final int INCLUDED_FOLDER= 0xB;
    /** Output folder (for a source folder) */
    public static final int OUTPUT= 0xC;
    /** An archive element (.zip or .jar) */
    public static final int ARCHIVE= 0xD;
    /** A IPackageFragmentRoot with include/exclude filters set */
    public static final int MODIFIED_FRAGMENT_ROOT= 0xE;
    /** Default package fragment */
    public static final int DEFAULT_FRAGMENT= 0xF;
    /** Undefined type */
    public static final int UNDEFINED= 0x10;
    /** Multi selection */
    public static final int MULTI= 0x11;
    /** No elements selected */
    public static final int NULL_SELECTION= 0x12;
    /** Elements that are contained in an archive (.jar or .zip) */
    public static final int ARCHIVE_RESOURCE= 0x13;
    /** Elements that represent classpath container (= libraries) */
    public static final int CONTAINER= 0x14;
    
    private LoadpathModifierAction[] fActions;
    private int fLastType;
    private List fListeners;
    private static final int fContextSensitiveActions= 5;
    
    /**
     * Constructor which creates the operations and based on this 
     * operations the actions.
     * 
     * @param provider a information provider to pass necessary information 
     * to the operations
     * @param page a listener for the changes on classpath entries, that is 
     * the listener will be notified whenever a classpath entry changed.
     * @see ILoadpathModifierListener
     */
    public DialogPackageExplorerActionGroup(ILoadpathInformationProvider provider, final NewSourceContainerWorkbookPage page) {
        super();
        fLastType= UNDEFINED;
        fListeners= new ArrayList();
        fActions= new LoadpathModifierAction[8];
        LoadpathModifierOperation op;
        op= new AddSelectedSourceFolderOperation(page, provider);
        // TODO User disabled image when available
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_ADD_AS_SOURCE_FOLDER, null,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddSelSFToCP_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddSelSFToCP_tooltip, IAction.AS_PUSH_BUTTON), 0);
        op= new RemoveFromLoadpathOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_REMOVE_AS_SOURCE_FOLDER, RubyPluginImages.DESC_DLCL_REMOVE_AS_SOURCE_FOLDER, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_tooltip, IAction.AS_PUSH_BUTTON), 1);
        op= new ExcludeOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_EXCLUDE_FROM_BUILDPATH, RubyPluginImages.DESC_DLCL_EXCLUDE_FROM_BUILDPATH,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_tooltip, IAction.AS_PUSH_BUTTON), 2);
        op= new UnexcludeOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_INCLUDE_ON_BUILDPATH, RubyPluginImages.DESC_DLCL_INCLUDE_ON_BUILDPATH,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Unexclude_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Unexclude_tooltip, IAction.AS_PUSH_BUTTON), 3);
        op= new EditFiltersOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_CONFIGURE_BUILDPATH_FILTERS, RubyPluginImages.DESC_DLCL_CONFIGURE_BUILDPATH_FILTERS,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_tooltip, IAction.AS_PUSH_BUTTON), 4); 
//        LoadpathModifierDropDownAction dropDown= new LoadpathModifierDropDownAction(action, 
//                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Configure_label, 
//                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Configure_tooltip); 
//        addAction(dropDown, 4);
//        op= new EditOutputFolderOperation(page, provider);
//        action= new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_CONFIGURE_OUTPUT_FOLDER, RubyPluginImages.DESC_DLCL_CONFIGURE_OUTPUT_FOLDER,
//                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_EditOutput_label, 
//                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_EditOutput_tooltip, IAction.AS_PUSH_BUTTON) {
//        	
//        	public void run() {
//        		page.commitDefaultOutputFolder();
//        		super.run();
//        	}
//        }; 
//        dropDown.addAction(action);
        /*addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_OBJS_TEXT_EDIT, RubyPluginImages.DESC_DLCL_TEXT_EDIT, 
                NewWizardMessages.getString("NewSourceContainerWorkbookPage.ToolBar.Edit.label"), //$NON-NLS-1$
                NewWizardMessages.getString("NewSourceContainerWorkbookPage.ToolBar.Edit.tooltip"), IAction.AS_PUSH_BUTTON), //$NON-NLS-1$
                ILoadpathInformationProvider.EDIT);*/
        op= new LinkedSourceFolderOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_ADD_LINKED_SOURCE_TO_BUILDPATH, RubyPluginImages.DESC_DLCL_ADD_LINKED_SOURCE_TO_BUILDPATH, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_tooltip, IAction.AS_PUSH_BUTTON), 5);
        op= new CreateFolderOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_OBJS_SOURCE_FOLDER_ROOT, null, 
        		NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_CreateSrcFolder_label, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_CreateSrcFolder_tooltip
        		, IAction.AS_PUSH_BUTTON), 6);
        op= new ResetAllOperation(page, provider);
        addAction(new LoadpathModifierAction(op, RubyPluginImages.DESC_ELCL_CLEAR, RubyPluginImages.DESC_DLCL_CLEAR,
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_ClearAll_label, 
                NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_ClearAll_tooltip, IAction.AS_PUSH_BUTTON), 7);
    }

    private void addAction(LoadpathModifierAction action, int index) {
        fActions[index]= action;
    }
    
    /**
     * Get an action of the specified type
     * 
     * @param type the type of the desired action, must be a
     * constante of <code>ILoadpathInformationProvider</code>
     * @return the requested action
     * 
     * @see ILoadpathInformationProvider
     */
    public LoadpathModifierAction getAction(int type) {
    	for (int i= 0; i < fActions.length; i++) {
			if (fActions[i].getOperation().getTypeId() == type)
				return fActions[i];
		}
    	throw new ArrayIndexOutOfBoundsException();
    }
    
    public LoadpathModifierAction[] getActions() {
    	List result= new ArrayList();
    	for (int i= 0; i < fActions.length; i++) {
			LoadpathModifierAction action= fActions[i];
			if (action instanceof LoadpathModifierDropDownAction) {
				LoadpathModifierDropDownAction dropDownAction= (LoadpathModifierDropDownAction)action;
				LoadpathModifierAction[] actions= dropDownAction.getActions();
				for (int j= 0; j < actions.length; j++) {
					result.add(actions[j]);
				}
			} else {
				result.add(action);
			}
		}
    	return (LoadpathModifierAction[])result.toArray(new LoadpathModifierAction[result.size()]);
    }
    
    /**
     * Create a toolbar manager for a given 
     * <code>ViewerPane</code>
     * 
     * @param pane the pane to create the <code>
     * ToolBarManager</code> for.
     * @return the created <code>ToolBarManager</code>
     */
    public ToolBarManager createLeftToolBarManager(ViewerPane pane) {
        ToolBarManager tbm= pane.getToolBarManager();
        for (int i= 0; i < fContextSensitiveActions; i++) {
            tbm.add(fActions[i]);
            if (i == 1 || i == 3)
                tbm.add(new Separator());
        }
        tbm.update(true);
        return tbm;
    }
    
    /**
     * Create a toolbar manager for a given 
     * <code>ViewerPane</code>
     * 
     * @param pane the pane to create the help toolbar for
     * @return the created <code>ToolBarManager</code>
     */
    public ToolBarManager createLeftToolBar(ViewerPane pane) {
        ToolBar tb= new ToolBar(pane, SWT.FLAT);
        pane.setTopRight(tb);
        ToolBarManager tbm= new ToolBarManager(tb);
        for (int i= fContextSensitiveActions; i < fActions.length; i++) {
        	tbm.add(fActions[i]);
        }
        tbm.add(new HelpAction());
        tbm.update(true);
        return tbm;
    }
    
    /**
     * Forces the action group to recompute the available actions 
     * and fire an event to all listeners
     * @throws RubyModelException 
     * 
     * @see #setContext(DialogExplorerActionContext)
     * @see #informListeners(String[], LoadpathModifierAction[])
     */
    public void refresh(DialogExplorerActionContext context) throws RubyModelException {
        super.setContext(context);
        if (context == null) // can happen when disposing
            return;
        List selectedElements= context.getSelectedElements();
        IRubyProject project= context.getRubyProject();
        
        int type= MULTI;
        if (selectedElements.size() == 0) {
            type= NULL_SELECTION;
            
            if (type == fLastType)
                return;
        } else if (selectedElements.size() == 1 || identicalTypes(selectedElements, project)) {
            type= getType(selectedElements.get(0), project);
        }
        
        internalSetContext(selectedElements, project, type);
    }
    
    /**
     * Set the context of the action group. Note that this method is deprecated. 
     * <ul><li>Clients should use DialogPackageExplorerActionGroup.setContext(DialogExplorerActionContext) instead</li>
     * <li>If this method is called, it is expected that the provided context is of type 
     * <code>DialogExplorerActionContext</code>. If this is not the case, the caller will 
     * end up with a <code>ClassCastException</code>.
     * 
     * @deprecated use instead DialogPackageExplorerActionGroup.setContext(DialogExplorerActionContext)
     * 
     * @see #setContext(DialogExplorerActionContext)
     */
    public void setContext(ActionContext context) {
        try {
            setContext((DialogExplorerActionContext)context);
        } catch (RubyModelException e) {
            RubyPlugin.log(e);
        }
    }
    
    /**
     * Set the context for the action group. This also includes 
     * updating the actions (that is, enable or disable them). 
     * The decision which actions should be enabled or disabled is based 
     * on the content of the <code>DialogExplorerActionContext</code>
     * 
     * If the type of the selection changes, then listeners will be notified 
     * about the new set of available actions.
     * 
     * Note: notification is only done if the TYPE changes (not the selected object 
     * as such). This means that if elements of the same type are selected (for 
     * example two times a folder), NO notification will take place. There might 
     * be situations where the type of two objects is the same but the set of 
     * available actions is not. However, if clients decide that upon some action 
     * a recomputation of the available actions has to be forced, then 
     * <code>PackageExplorerActionGroup.refresh(DialogExplorerActionContext)</code> can be 
     * called.
     * 
     * @param context the action context
     * 
     * @see IPackageExplorerActionListener
     * @see PackageExplorerActionEvent
     * @see DialogExplorerActionContext
     * @see #addListener(IPackageExplorerActionListener)
     * @see #refresh(DialogExplorerActionContext)
     * 
     * @throws RubyModelException if there is a failure while computing the available 
     * actions.
     */
    public void setContext(DialogExplorerActionContext context) throws RubyModelException {
        super.setContext(context);
        if (context == null) // can happen when disposing
            return;
        List selectedElements= context.getSelectedElements();
        IRubyProject project= context.getRubyProject();
        
        int type= MULTI;
        if (selectedElements.size() == 0) {
            type= NULL_SELECTION;
            
            if (type == fLastType)
                return;
        }
        else if (selectedElements.size() == 1 || identicalTypes(selectedElements, project)) {
            type= getType(selectedElements.get(0), project);
            
            if (selectedElements.size() > 1)
                type= type | MULTI;
        
            if (type == fLastType)
                return;
        }
        
        internalSetContext(selectedElements, project, type);
    }
    
    /**
     * Get a description for the last selection explaining 
     * why no operation is possible.<p>
     * This can be usefull if a context sensitive widget does 
     * not want to display all operations although some of them 
     * are valid.
     * 
     * @return a description for the last selection that explains 
     * why no operation is available.
     */
    public String getNoActionDescription() {
        String[] description= noAction(fLastType);
        return description[0];
    }
    
    /**
     * Internal method to set the context of the action group.
     * 
     * @param selectedElements a list of selected elements, can be empty
     * @param project the Ruby project
     * @param type the type of the selected element(s)
     * @throws RubyModelException
     */
    private void internalSetContext(List selectedElements, IRubyProject project, int type) throws RubyModelException {
        fLastType= type;
        List availableActions= getAvailableActions(selectedElements, project);
        LoadpathModifierAction[] actions= new LoadpathModifierAction[availableActions.size()];
        String[] descriptions= new String[availableActions.size()];
        if (availableActions.size() > 0) {
            for(int i= 0; i < availableActions.size(); i++) {
                LoadpathModifierAction action= (LoadpathModifierAction)availableActions.get(i);
                actions[i]= action;
                descriptions[i]= action.getDescription(type);
            }
        } else
            descriptions= noAction(type);

        informListeners(descriptions, actions);
    }
    
    /**
     * Finds out wheter the list of elements consists only of elements 
     * having the same type (for example all are of type 
     * DialogPackageExplorerActionGroup.COMPILATION_UNIT). This allows 
     * to use a description for the available actions which is more 
     * specific and therefore provides more information.
     * 
     * @param elements a list of elements to be compared to each other
     * @param project the java project
     * @return <code>true</code> if all elements are of the same type, 
     * <code>false</code> otherwise.
     * @throws RubyModelException 
     */
    private boolean identicalTypes(List elements, IRubyProject project) throws RubyModelException {
		if (elements.size() == 0) {
			return false;
		}
		
        Object firstElement= elements.get(0);
        int firstType= getType(firstElement, project);
        for(int i= 1; i < elements.size(); i++) {
            if(firstType != getType(elements.get(i), project))
                return false;
        }
        return true;
    }
    
    /**
     * Inform all listeners about new actions.
     * 
     * @param descriptions an array of descriptions for each 
     * actions, where the description at position 'i' belongs to 
     * the action at position 'i'
     * @param actions an array of available actions
     */
    private void informListeners(String[] descriptions, LoadpathModifierAction[] actions) {
        Iterator iterator= fListeners.iterator();
        PackageExplorerActionEvent event= new PackageExplorerActionEvent(descriptions, actions);
        while(iterator.hasNext()) {
            IPackageExplorerActionListener listener= (IPackageExplorerActionListener)iterator.next();
            listener.handlePackageExplorerActionEvent(event);
        }
    }
    
    /**
     * Returns string array with only one element which contains a short reason to indicate 
     * why there are no actions available.
     * 
     * @return a description to explain why there are no actions available
     */
    private String[] noAction(int type) {
        String reason;
        switch(type) {
            case FILE: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_File; break; 
            case FILE | MULTI: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_File; break; 
            case DEFAULT_FRAGMENT: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_DefaultPackage; break; 
            case DEFAULT_FRAGMENT | MULTI: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_DefaultPackage; break; 
            case NULL_SELECTION: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_NullSelection; break; 
            case MULTI: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_MultiSelection; break; 
            case ARCHIVE_RESOURCE: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_ArchiveResource; break; 
            default: reason= NewWizardMessages.PackageExplorerActionGroup_NoAction_NoReason; 
        }
        return new String[] {reason};
    }  
    
    /**
     * Computes the type based on the current selection. The type
     * can be usefull to set the content of the hint text group
     * properly.
     * 
     * @param obj the object to get the type from
     * @return the type of the current selection or UNDEFINED if no
     * appropriate type could be found. Possible types are:<br>
     * PackageExplorerActionGroup.FOLDER<br>
     * PackageExplorerActionGroup.EXCLUDED_FOLDER;<br>
     * PackageExplorerActionGroup.EXCLUDED_FILE<br>
     * PackageExplorerActionGroup.DEFAULT_OUTPUT<br>
     * PackageExplorerActionGroup.INCLUDED_FILE<br>
     * PackageExplorerActionGroup.INCLUDED_FOLDER<br>
     * PackageExplorerActionGroup.OUTPUT<br>
     * PackageExplorerActionGroup.MODIFIED_FRAGMENT_ROOT<br>
     * PackageExplorerActionGroup.DEFAULT_FRAGMENT<br>
     * PackageExplorerActionGroup.JAVA_PROJECT<br>
     * PackageExplorerActionGroup.PACKAGE_FRAGMENT_ROOT<br>
     * PackageExplorerActionGroup.PACKAGE_FRAGMENT<br>
     * PackageExplorerActionGroup.COMPILATION_UNIT<br>
     * PackageExplorerActionGroup.FILE<br>
     * @throws RubyModelException 
     */
    public static int getType(Object obj, IRubyProject project) throws RubyModelException {
            if (obj instanceof IRubyProject)
                return RUBY_PROJECT;
            if (obj instanceof LoadPathContainer)
                return CONTAINER;
            if (obj instanceof ISourceFolderRoot)
                return LoadpathModifier.filtersSet((ISourceFolderRoot)obj) ? MODIFIED_FRAGMENT_ROOT : SOURCE_FOLDER_ROOT;
            if (obj instanceof ISourceFolder) {
                if (LoadpathModifier.isDefaultFolder((ISourceFolder)obj)) {
                    if (((ISourceFolderRoot)((IRubyElement)obj).getAncestor(IRubyElement.SOURCE_FOLDER_ROOT)).isArchive())
                        return ARCHIVE_RESOURCE;
                    return DEFAULT_FRAGMENT;
                }
                if (LoadpathModifier.isIncluded((IRubyElement)obj, project, null))
                    return INCLUDED_FOLDER;
                if (((ISourceFolderRoot)((IRubyElement)obj).getAncestor(IRubyElement.SOURCE_FOLDER_ROOT)).isArchive())
                    return ARCHIVE_RESOURCE;
                return SOURCE_FOLDER;
            }
            if (obj instanceof IRubyScript) {
                if (((ISourceFolderRoot)((IRubyElement)obj).getAncestor(IRubyElement.SOURCE_FOLDER_ROOT)).isArchive())
                    return ARCHIVE_RESOURCE;
                return LoadpathModifier.isIncluded((IRubyElement)obj, project, null) ? INCLUDED_FILE : RUBY_SCRIPT;
            }
            if (obj instanceof IFolder) {
                return getFolderType((IFolder)obj, project);
            }
            if (obj instanceof IFile)
                return getFileType((IFile)obj, project);
            if (obj instanceof CPListElementAttribute)
                return OUTPUT;
        return UNDEFINED;
    }
    
    /**
     * Get the type of the folder
     * 
     * @param folder folder to get the type from
     * @return the type code for the folder. Possible types are:<br>
     * PackageExplorerActionGroup.FOLDER<br>
     * PackageExplorerActionGroup.EXCLUDED_FOLDER;<br>
     * @throws RubyModelException 
     */
    private static int getFolderType(IFolder folder, IRubyProject project) throws RubyModelException {
        IContainer folderParent= folder.getParent();
		if (folderParent.getFullPath().equals(project.getPath()))
            return FOLDER;
        if (LoadpathModifier.getFolder(folderParent) != null)
            return EXCLUDED_FOLDER;
        ISourceFolderRoot fragmentRoot= LoadpathModifier.getFolderRoot(folder, project, null);
		if (fragmentRoot == null)
            return FOLDER;
        if (fragmentRoot.equals(RubyCore.create(folderParent)))
            return EXCLUDED_FOLDER;
        return FOLDER;              
    }
    
    /**
     * Get the type of the file
     * 
     * @param file file to get the type from
     * @return the type code for the file. Possible types are:<br>
     * PackageExplorerActionGroup.EXCLUDED_FILE<br>
     * PackageExplorerActionGroup.FILE
     * @throws RubyModelException 
     */
    private static int getFileType(IFile file, IRubyProject project) throws RubyModelException {
//        if (LoadpathModifier.isArchive(file, project))
//            return ARCHIVE;
        if (!RubyCore.isRubyLikeFileName(file.getName()))
            return FILE;
        IContainer fileParent= file.getParent();
		if (fileParent.getFullPath().equals(project.getPath())) {
            if (project.isOnLoadpath(project)) 
                return EXCLUDED_FILE;
            return FILE;
        }
        ISourceFolderRoot fragmentRoot= LoadpathModifier.getFolderRoot(file, project, null);
		if (fragmentRoot == null)
            return FILE;
        if (fragmentRoot.isArchive())
            return ARCHIVE_RESOURCE;
        if (fragmentRoot.equals(RubyCore.create(fileParent)))
            return EXCLUDED_FILE;
        if (LoadpathModifier.getFolder(fileParent) == null) {
            if (LoadpathModifier.parentExcluded(fileParent, project))
                return FILE;
            return EXCLUDED_FILE;
        }
        return EXCLUDED_FILE;
    }
    
    /**
     * Based on the given list of elements, get the list of available 
     * actions that can be applied on this elements
     * 
     * @param selectedElements the list of elements to get the actions for
     * @param project the Ruby project
     * @return a list of <code>LoadpathModifierAction</code>s
     * @throws RubyModelException
     */
    private List getAvailableActions(List selectedElements, IRubyProject project) throws RubyModelException {
		if (project == null || !project.exists()) {
			return new ArrayList();
		}
		
        List actions= new ArrayList();
        int[] types= new int[selectedElements.size()];
        for(int i= 0; i < types.length; i++) {
            types[i]= getType(selectedElements.get(i), project);
        }
        for(int i= 0; i < fActions.length; i++) {
            if(fActions[i] instanceof LoadpathModifierDropDownAction) {
                if(changeEnableState(fActions[i], selectedElements, types)) {
                    LoadpathModifierAction[] dropDownActions= ((LoadpathModifierDropDownAction)fActions[i]).getActions();
                    for(int j= 0; j < dropDownActions.length; j++) {
                        if(changeEnableState(dropDownActions[j], selectedElements, types))
                            actions.add(dropDownActions[j]);
                    }
                }
            }
            else if(changeEnableState(fActions[i], selectedElements, types)) {
                actions.add(fActions[i]);
            }
        }
        return actions;
    }
    
    /**
     * Changes the enabled state of an action if necessary.
     * 
     * @param action the action to change it's state for
     * @param selectedElements a list of selected elements
     * @param types an array of types corresponding to the types of 
     * the selected elements 
     * @return <code>true</code> if the action is valid (= enabled), <code>false</code> otherwise
     * @throws RubyModelException
     */
    private boolean changeEnableState(LoadpathModifierAction action, List selectedElements, int[] types) throws RubyModelException {
        if(action.isValid(selectedElements, types)) {
            if (!action.isEnabled())
                action.setEnabled(true);
            return true;
        } else {
            if (action.isEnabled())
                action.setEnabled(false);
            return false;
        }
    }
    
    /**
     * Fill the context menu with the available actions
     * 
     * @param menu the menu to be filled up with actions
     */
    public void fillContextMenu(IMenuManager menu) {        
        for (int i= 0; i < fContextSensitiveActions; i++) {
            IAction action= getAction(i);
            if (action instanceof LoadpathModifierDropDownAction) {
                if (action.isEnabled()) {
                    IAction[] actions= ((LoadpathModifierDropDownAction)action).getActions();
                    for(int j= 0; j < actions.length; j++) {
                        if(actions[j].isEnabled())
                            menu.add(actions[j]);
                    }
                }
            }
            else if (action.isEnabled())
                menu.add(action);
        }
        super.fillContextMenu(menu);
    }
    
    /**
     * Add listeners for the <code>PackageExplorerActionEvent</code>.
     * 
     * @param listener the listener to be added
     * 
     * @see PackageExplorerActionEvent
     * @see IPackageExplorerActionListener
     */
    public void addListener(IPackageExplorerActionListener listener) {
        fListeners.add(listener);
    }
    
    /**
     * Remove the listener from the list of registered listeners.
     * 
     * @param listener the listener to be removed
     */
    public void removeListener(IPackageExplorerActionListener listener) {
        fListeners.remove(listener);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.actions.CompositeActionGroup#dispose()
     */
    public void dispose() {
        fListeners.clear();
        super.dispose();
    }
}

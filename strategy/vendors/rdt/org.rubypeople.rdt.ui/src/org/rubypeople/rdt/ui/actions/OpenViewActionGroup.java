/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IContextMenuConstants;

/**
 * Action group that adds actions to open a new JDT view part or an external 
 * viewer to a context menu and the global menu bar.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class OpenViewActionGroup extends ActionGroup {

    private boolean fEditorIsOwner;
	private boolean fIsTypeHiararchyViewerOwner;
    private boolean fIsCallHiararchyViewerOwner;
    
	private ISelectionProvider fSelectionProvider;

//	private OpenSuperImplementationAction fOpenSuperImplementation;
//	private OpenExternalRubydocAction fOpenExternalRubydoc;
	private OpenTypeHierarchyAction fOpenTypeHierarchy;
    private OpenCallHierarchyAction fOpenCallHierarchy;
	private PropertyDialogAction fOpenPropertiesDialog;

	/**
	 * Creates a new <code>OpenActionGroup</code>. The group requires
	 * that the selection provided by the page's selection provider is 
	 * of type {@link IStructuredSelection}.
	 * 
	 * @param page the page that owns this action group
	 */
	public OpenViewActionGroup(Page page) {
		createSiteActions(page.getSite(), null);
	}
	
	/**
	 * Creates a new <code>OpenActionGroup</code>. The group requires
	 * that the selection provided by the given selection provider is 
	 * of type {@link IStructuredSelection}.
	 * 
	 * @param page the page that owns this action group
	 * @param selectionProvider the selection provider used instead of the
	 *  page selection provider.
	 * 
	 * @since 3.2
	 */
	public OpenViewActionGroup(Page page, ISelectionProvider selectionProvider) {
		createSiteActions(page.getSite(), selectionProvider);
	}
	
	/**
	 * Creates a new <code>OpenActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is 
	 * of type {@link IStructuredSelection}.
	 * 
	 * @param part the view part that owns this action group
	 */
	public OpenViewActionGroup(IViewPart part) {
		this(part, null);
	}
	
	/**
	 * Creates a new <code>OpenActionGroup</code>. The group requires
	 * that the selection provided by the given selection provider is of type
	 * {@link IStructuredSelection}.
	 * 
	 * @param part the view part that owns this action group
	 * @param selectionProvider the selection provider used instead of the
	 *  page selection provider.
	 *  
	 * @since 3.2
	 */
	public OpenViewActionGroup(IViewPart part, ISelectionProvider selectionProvider) {
		createSiteActions(part.getSite(), selectionProvider);
		// we do a name check here to avoid class loading. 
		String partName= part.getClass().getName();
		fIsTypeHiararchyViewerOwner= "org.rubypeople.rdt.internal.ui.typehierarchy.TypeHierarchyViewPart".equals(partName); //$NON-NLS-1$
		fIsCallHiararchyViewerOwner= "org.rubypeople.rdt.internal.ui.callhierarchy.CallHierarchyViewPart".equals(partName); //$NON-NLS-1$
	}
	
	/**
	 * Creates a new <code>OpenActionGroup</code>. The group requires
	 * that the selection provided by the given selection provider is of type 
	 * {@link IStructuredSelection}.
	 * 
	 * @param site the site that will own the action group.
	 * @param selectionProvider the selection provider used instead of the
	 *  page selection provider.
	 *  
	 * @since 3.2
	 */
	public OpenViewActionGroup(IWorkbenchSite site, ISelectionProvider selectionProvider) {
		createSiteActions(site, selectionProvider);
	}
	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param part the editor part
	 */
	public OpenViewActionGroup(RubyEditor part) {
		fEditorIsOwner= true;

//		fOpenSuperImplementation= new OpenSuperImplementationAction(part);
//		fOpenSuperImplementation.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_SUPER_IMPLEMENTATION);
//		part.setAction("OpenSuperImplementation", fOpenSuperImplementation); //$NON-NLS-1$
//
//		fOpenExternalRubydoc= new OpenExternalRubydocAction(part);
//		fOpenExternalRubydoc.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_EXTERNAL_JAVADOC);
//		part.setAction("OpenExternalRubydoc", fOpenExternalRubydoc); //$NON-NLS-1$

		fOpenTypeHierarchy= new OpenTypeHierarchyAction(part);
		fOpenTypeHierarchy.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_TYPE_HIERARCHY);
		part.setAction("OpenTypeHierarchy", fOpenTypeHierarchy); //$NON-NLS-1$

        fOpenCallHierarchy= new OpenCallHierarchyAction(part);
        fOpenCallHierarchy.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_CALL_HIERARCHY);
        part.setAction("OpenCallHierarchy", fOpenCallHierarchy); //$NON-NLS-1$

		initialize(part.getEditorSite().getSelectionProvider());
	}
	
	private void createSiteActions(IWorkbenchSite site, ISelectionProvider specialProvider) {
//		fOpenSuperImplementation= new OpenSuperImplementationAction(site);
//		fOpenSuperImplementation.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_SUPER_IMPLEMENTATION);
//		fOpenSuperImplementation.setSpecialSelectionProvider(specialProvider);
//		
//		fOpenExternalRubydoc= new OpenExternalRubydocAction(site);
//		fOpenExternalRubydoc.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_EXTERNAL_JAVADOC);
//		fOpenExternalRubydoc.setSpecialSelectionProvider(specialProvider);

		fOpenTypeHierarchy= new OpenTypeHierarchyAction(site);
		fOpenTypeHierarchy.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_TYPE_HIERARCHY);
		fOpenTypeHierarchy.setSpecialSelectionProvider(specialProvider);

		fOpenCallHierarchy= new OpenCallHierarchyAction(site);
        fOpenCallHierarchy.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_CALL_HIERARCHY);
        fOpenCallHierarchy.setSpecialSelectionProvider(specialProvider);

        ISelectionProvider provider= specialProvider != null ? specialProvider : site.getSelectionProvider();
        
        if(getShowProperties()) {
	        fOpenPropertiesDialog= new PropertyDialogAction(site, provider);
	        fOpenPropertiesDialog.setActionDefinitionId(IWorkbenchActionDefinitionIds.PROPERTIES);
        }
		
        initialize(provider);
	}

	private void initialize(ISelectionProvider provider) {
		fSelectionProvider= provider;
		ISelection selection= provider.getSelection();
//		fOpenSuperImplementation.update(selection);
//		fOpenExternalRubydoc.update(selection);
		fOpenTypeHierarchy.update(selection);
        fOpenCallHierarchy.update(selection);
		if (!fEditorIsOwner) {
			if(getShowProperties()) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ss= (IStructuredSelection)selection;
					fOpenPropertiesDialog.selectionChanged(ss);
				} else {
					fOpenPropertiesDialog.selectionChanged(selection);
				}
			}
//			provider.addSelectionChangedListener(fOpenSuperImplementation);
//			provider.addSelectionChangedListener(fOpenExternalRubydoc);
			provider.addSelectionChangedListener(fOpenTypeHierarchy);
            provider.addSelectionChangedListener(fOpenCallHierarchy);
			// no need to register the open properties dialog action since it registers itself
		}
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBar) {
		super.fillActionBars(actionBar);
		setGlobalActionHandlers(actionBar);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		if (!fIsTypeHiararchyViewerOwner)
			appendToGroup(menu, fOpenTypeHierarchy);
        if (!fIsCallHiararchyViewerOwner)
            appendToGroup(menu, fOpenCallHierarchy);
		IStructuredSelection selection= getStructuredSelection();
		if (getShowProperties() && fOpenPropertiesDialog != null && fOpenPropertiesDialog.isEnabled() && selection != null &&fOpenPropertiesDialog.isApplicableForSelection(selection))
			menu.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, fOpenPropertiesDialog);
	}

	/*
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
//		fSelectionProvider.removeSelectionChangedListener(fOpenSuperImplementation);
//		fSelectionProvider.removeSelectionChangedListener(fOpenExternalRubydoc);
		fSelectionProvider.removeSelectionChangedListener(fOpenTypeHierarchy);
		fSelectionProvider.removeSelectionChangedListener(fOpenCallHierarchy);
		super.dispose();
	}
	
	private void setGlobalActionHandlers(IActionBars actionBars) {
//		actionBars.setGlobalActionHandler(RdtActionConstants.OPEN_SUPER_IMPLEMENTATION, fOpenSuperImplementation);
//		actionBars.setGlobalActionHandler(RdtActionConstants.OPEN_EXTERNAL_JAVA_DOC, fOpenExternalRubydoc);
		actionBars.setGlobalActionHandler(RdtActionConstants.OPEN_TYPE_HIERARCHY, fOpenTypeHierarchy);
        actionBars.setGlobalActionHandler(RdtActionConstants.OPEN_CALL_HIERARCHY, fOpenCallHierarchy);
        
        if (!fEditorIsOwner && getShowProperties())
        	actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), fOpenPropertiesDialog);		
	}
	
	private void appendToGroup(IMenuManager menu, IAction action) {
		if (action.isEnabled())
			menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);
	}
	
	private IStructuredSelection getStructuredSelection() {
		ISelection selection= getContext().getSelection();
		if (selection instanceof IStructuredSelection)
			return (IStructuredSelection)selection;
		return null;
	}
	
	/**
	 * Note: This method is for internal use only.
	 * As specified in the class documentation, this class should not be subclassed by clients.
	 */
	protected boolean getShowProperties() {
		return true;
	}
}

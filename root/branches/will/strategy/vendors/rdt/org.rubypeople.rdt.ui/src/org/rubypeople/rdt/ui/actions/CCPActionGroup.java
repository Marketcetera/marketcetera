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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.actions.MoveResourceAction;
import org.eclipse.ui.actions.RenameResourceAction;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.ui.views.navigator.ResourceNavigatorRenameAction;
import org.rubypeople.rdt.ui.IPackagesViewPart;

/**
 * Action group that adds the copy, cut, paste actions to a view part's context
 * menu and installs handlers for the corresponding global menu actions.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class CCPActionGroup extends ActionGroup {

	private IWorkbenchSite fSite;
	private Clipboard fClipboard;

 	private SelectionListenerAction[] fActions;

 	private SelectionListenerAction fDeleteAction;
	private SelectionListenerAction fCopyAction;
	private RenameResourceAction fRenameAction;
//	private SelectionDispatchAction fCopyQualifiedNameAction;
	private PasteAction fPasteAction;
	private MoveResourceAction fMoveAction;
//	private SelectionListenerAction fCutAction;
	
	private TreeViewer fTreeViewer;
	
	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires that
	 * the selection provided by the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param part the view part that owns this action group
	 */
	public CCPActionGroup(IViewPart part) {
		if (part instanceof IPackagesViewPart) {
			IPackagesViewPart pack = (IPackagesViewPart) part;
			fTreeViewer = pack.getTreeViewer();
		}
		init(part.getSite());
	}
	
	/**
	 * Creates a new <code>CCPActionGroup</code>.  The group requires that
	 * the selection provided by the page's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param page the page that owns this action group
	 */
	public CCPActionGroup(Page page) {
		this(page.getSite());
	}

	private CCPActionGroup(IWorkbenchSite site) {		
		init(site);
	}

	private void init(IWorkbenchSite site) {
		fSite= site;
		fClipboard= new Clipboard(site.getShell().getDisplay());
		
		fPasteAction= new PasteAction(fSite.getShell(), fClipboard);
		fPasteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
		
		fCopyAction= new CopyAction(fSite.getShell(), fClipboard, fPasteAction);
		fCopyAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);
		
//		fCopyQualifiedNameAction= new CopyQualifiedNameAction(fSite, fClipboard, fPasteAction);
//		fCopyQualifiedNameAction.setActionDefinitionId(CopyQualifiedNameAction.JAVA_EDITOR_ACTION_DEFINITIONS_ID);
		
//		fCutAction= new CutAction(fSite.getShell(), fClipboard, fPasteAction);
//		fCutAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);
		
		fMoveAction = new MoveResourceAction(fSite.getShell());
		
		if (fTreeViewer != null) {
			fRenameAction = new ResourceNavigatorRenameAction(fSite.getShell(), fTreeViewer);
		} else {
			fRenameAction = new RenameResourceAction(fSite.getShell());
		}
		fRenameAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.RENAME);
		
		fDeleteAction= new DeleteResourceAction(fSite.getShell());
		fDeleteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		
		fActions= new SelectionListenerAction[] { /*fCutAction,*/ fCopyAction, /*fCopyQualifiedNameAction,*/ fPasteAction, fDeleteAction, fRenameAction, fMoveAction };
		registerActionsAsSelectionChangeListeners();
	}

	private void registerActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		ISelection selection= provider.getSelection();
		for (int i= 0; i < fActions.length; i++) {
			SelectionListenerAction action= fActions[i];
			provider.addSelectionChangedListener(action);
		}
	}
	
	private void deregisterActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		for (int i= 0; i < fActions.length; i++) {
			provider.removeSelectionChangedListener(fActions[i]);
		}
	}
	
	
	/**
	 * Returns the delete action managed by this action group. 
	 * 
	 * @return the delete action. Returns <code>null</code> if the group
	 * 	doesn't provide any delete action
	 */
	public IAction getDeleteAction() {
		return fDeleteAction;
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), fDeleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);
//		actionBars.setGlobalActionHandler(CopyQualifiedNameAction.ACTION_HANDLER_ID, fCopyQualifiedNameAction);
//		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), fCutAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fPasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), fRenameAction);
		actionBars.setGlobalActionHandler(ActionFactory.MOVE.getId(), fMoveAction);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		for (int i= 0; i < fActions.length; i++) {
			SelectionListenerAction action= fActions[i];
//			if (action == fCutAction && !fCutAction.isEnabled())
//				continue;
			menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);
		}		
	}		
	
	/*
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
		super.dispose();
		if (fClipboard != null){
			fClipboard.dispose();
			fClipboard= null;
		}
		deregisterActionsAsSelectionChangeListeners();
	}

}

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
package org.rubypeople.rdt.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;
import org.rubypeople.rdt.internal.ui.search.SearchUtil;

/**
 * Action group that adds the search for read references actions to a
 * context menu and the global menu bar.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class ReadReferencesSearchGroup extends ActionGroup  {

	private static final String MENU_TEXT= SearchMessages.group_readReferences; 

	private IWorkbenchSite fSite;
	private RubyEditor fEditor;
	private IActionBars fActionBars;
	
	private String fGroupId;

	private FindReadReferencesAction fFindReadReferencesAction;
	private FindReadReferencesInProjectAction fFindReadReferencesInProjectAction;
//	private FindReadReferencesInHierarchyAction fFindReadReferencesInHierarchyAction;
	private FindReadReferencesInWorkingSetAction fFindReadReferencesInWorkingSetAction;
	
	/**
	 * Creates a new <code>ReadReferencesSearchGroup</code>. The group requires
	 * that the selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public ReadReferencesSearchGroup(IWorkbenchSite site) {
		fSite= site;
		fGroupId= IContextMenuConstants.GROUP_SEARCH;

		fFindReadReferencesAction= new FindReadReferencesAction(site);
		fFindReadReferencesAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_WORKSPACE);

		fFindReadReferencesInProjectAction= new FindReadReferencesInProjectAction(site);
		fFindReadReferencesInProjectAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_PROJECT);

//		fFindReadReferencesInHierarchyAction= new FindReadReferencesInHierarchyAction(site);
//		fFindReadReferencesInHierarchyAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_HIERARCHY);

		fFindReadReferencesInWorkingSetAction= new FindReadReferencesInWorkingSetAction(site);
		fFindReadReferencesInWorkingSetAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_WORKING_SET);

		// register the actions as selection listeners
		ISelectionProvider provider= fSite.getSelectionProvider();
		ISelection selection= provider.getSelection();
		registerAction(fFindReadReferencesAction, provider, selection);
		registerAction(fFindReadReferencesInProjectAction, provider, selection);
//		registerAction(fFindReadReferencesInHierarchyAction, provider, selection);
		registerAction(fFindReadReferencesInWorkingSetAction, provider, selection);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public ReadReferencesSearchGroup(RubyEditor editor) {
		fEditor= editor;
		fSite= fEditor.getSite();
		fGroupId= ITextEditorActionConstants.GROUP_FIND;

		fFindReadReferencesAction= new FindReadReferencesAction(fEditor);
		fFindReadReferencesAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_WORKSPACE);
		fEditor.setAction("SearchReadAccessInWorkspace", fFindReadReferencesAction); //$NON-NLS-1$

		fFindReadReferencesInProjectAction= new FindReadReferencesInProjectAction(fEditor);
		fFindReadReferencesInProjectAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_PROJECT);
		fEditor.setAction("SearchReadAccessInProject", fFindReadReferencesInProjectAction); //$NON-NLS-1$

//		fFindReadReferencesInHierarchyAction= new FindReadReferencesInHierarchyAction(fEditor);
//		fFindReadReferencesInHierarchyAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_HIERARCHY);
//		fEditor.setAction("SearchReadAccessInHierarchy", fFindReadReferencesInHierarchyAction); //$NON-NLS-1$

		fFindReadReferencesInWorkingSetAction= new FindReadReferencesInWorkingSetAction(fEditor);
		fFindReadReferencesInWorkingSetAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_READ_ACCESS_IN_WORKING_SET);
		fEditor.setAction("SearchReadAccessInWorkingSet", fFindReadReferencesInWorkingSetAction); //$NON-NLS-1$
	}

	private void registerAction(SelectionDispatchAction action, ISelectionProvider provider, ISelection selection) {
		action.update(selection);
		provider.addSelectionChangedListener(action);
	}

	private void addAction(IAction action, IMenuManager manager) {
		if (action.isEnabled()) {
			manager.add(action);
		}
	}
	
	private void addWorkingSetAction(IWorkingSet[] workingSets, IMenuManager manager) {
		FindAction action;
		if (fEditor != null)
			action= new WorkingSetFindAction(fEditor, new FindReadReferencesInWorkingSetAction(fEditor, workingSets), SearchUtil.toString(workingSets));
		else
			action= new WorkingSetFindAction(fSite, new FindReadReferencesInWorkingSetAction(fSite, workingSets), SearchUtil.toString(workingSets));
		action.update(getContext().getSelection());
		addAction(action, manager);
	}
	
	
	/* (non-Javadoc)
	 * Method declared on ActionGroup.
	 */
	public void fillContextMenu(IMenuManager manager) {
		MenuManager javaSearchMM= new MenuManager(MENU_TEXT, IContextMenuConstants.GROUP_SEARCH);
		addAction(fFindReadReferencesAction, javaSearchMM);
		addAction(fFindReadReferencesInProjectAction, javaSearchMM);
//		addAction(fFindReadReferencesInHierarchyAction, javaSearchMM);
		
		javaSearchMM.add(new Separator());
		
		Iterator iter= SearchUtil.getLRUWorkingSets().sortedIterator();
		while (iter.hasNext()) {
			addWorkingSetAction((IWorkingSet[]) iter.next(), javaSearchMM);
		}
		addAction(fFindReadReferencesInWorkingSetAction, javaSearchMM);

		if (!javaSearchMM.isEmpty())
			manager.appendToGroup(fGroupId, javaSearchMM);
	}
	
	/* 
	 * Method declared on ActionGroup.
	 */
	public void fillActionBars(IActionBars actionBars) {
		Assert.isNotNull(actionBars);
		super.fillActionBars(actionBars);
		fActionBars= actionBars;
		updateGlobalActionHandlers();
	}

	/* 
	 * Method declared on ActionGroup.
	 */
	public void dispose() {
		ISelectionProvider provider= fSite.getSelectionProvider();
		if (provider != null) {
			disposeAction(fFindReadReferencesAction, provider);
			disposeAction(fFindReadReferencesInProjectAction, provider);
//			disposeAction(fFindReadReferencesInHierarchyAction, provider);
			disposeAction(fFindReadReferencesInWorkingSetAction, provider);
		}
		fFindReadReferencesAction= null;
		fFindReadReferencesInProjectAction= null;
//		fFindReadReferencesInHierarchyAction= null;
		fFindReadReferencesInWorkingSetAction= null;
		updateGlobalActionHandlers();
		super.dispose();
	}

	private void updateGlobalActionHandlers() {
		if (fActionBars != null) {
			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_READ_ACCESS_IN_WORKSPACE, fFindReadReferencesAction);
			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_READ_ACCESS_IN_PROJECT, fFindReadReferencesInProjectAction);
//			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_READ_ACCESS_IN_HIERARCHY, fFindReadReferencesInHierarchyAction);
			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_READ_ACCESS_IN_WORKING_SET, fFindReadReferencesInWorkingSetAction);
		}
	}

	private void disposeAction(ISelectionChangedListener action, ISelectionProvider provider) {
		if (action != null)
			provider.removeSelectionChangedListener(action);
	}
}

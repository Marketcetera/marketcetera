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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.actions.ActionMessages;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;

/**
 * Action group that adds the occurrences in file actions
 * to a context menu and the global menu bar.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 1.0
 */
public class OccurrencesSearchGroup extends ActionGroup  {

	private IWorkbenchSite fSite;
	private RubyEditor fEditor;
	private IActionBars fActionBars;
	
	private String fGroupId;

	private FindOccurrencesInFileAction fOccurrencesInFileAction;
//	private FindExceptionOccurrencesAction fExceptionOccurrencesAction;
//	private FindImplementOccurrencesAction fFindImplementorOccurrencesAction;

	/**
	 * Creates a new <code>ImplementorsSearchGroup</code>. The group 
	 * requires that the selection provided by the site's selection provider 
	 * is of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public OccurrencesSearchGroup(IWorkbenchSite site) {
		fSite= site;
		fGroupId= IContextMenuConstants.GROUP_SEARCH;
		
		fOccurrencesInFileAction= new FindOccurrencesInFileAction(site);
		fOccurrencesInFileAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_OCCURRENCES_IN_FILE);
		// Need to reset the label
		fOccurrencesInFileAction.setText(SearchMessages.Search_FindOccurrencesInFile_shortLabel); 

//		fExceptionOccurrencesAction= new FindExceptionOccurrencesAction(site);
//		fExceptionOccurrencesAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_EXCEPTION_OCCURRENCES_IN_FILE);
//
//		fFindImplementorOccurrencesAction= new FindImplementOccurrencesAction(site);
//		fFindImplementorOccurrencesAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_IMPLEMENT_OCCURRENCES_IN_FILE);

		// register the actions as selection listeners
		ISelectionProvider provider= fSite.getSelectionProvider();
		ISelection selection= provider.getSelection();
		registerAction(fOccurrencesInFileAction, provider, selection);
//		registerAction(fExceptionOccurrencesAction, provider, selection);
//		registerAction(fFindImplementorOccurrencesAction, provider, selection);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * 
	 * @param editor the Ruby editor
	 */
	public OccurrencesSearchGroup(RubyEditor editor) {
		fEditor= editor;
		fSite= fEditor.getSite();
		fGroupId= ITextEditorActionConstants.GROUP_FIND;

		fOccurrencesInFileAction= new FindOccurrencesInFileAction(fEditor);
		fOccurrencesInFileAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_OCCURRENCES_IN_FILE);
		// Need to reset the label
		fOccurrencesInFileAction.setText(SearchMessages.Search_FindOccurrencesInFile_shortLabel); 
		fEditor.setAction("SearchOccurrencesInFile", fOccurrencesInFileAction); //$NON-NLS-1$

//		fExceptionOccurrencesAction= new FindExceptionOccurrencesAction(fEditor);
//		fExceptionOccurrencesAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_EXCEPTION_OCCURRENCES_IN_FILE);
//		fEditor.setAction("SearchExceptionOccurrences", fExceptionOccurrencesAction); //$NON-NLS-1$
//
//		fFindImplementorOccurrencesAction= new FindImplementOccurrencesAction(fEditor);
//		fFindImplementorOccurrencesAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SEARCH_IMPLEMENT_OCCURRENCES_IN_FILE);
//		fEditor.setAction("SearchImplementOccurrences", fFindImplementorOccurrencesAction); //$NON-NLS-1$
	}

	private void registerAction(SelectionDispatchAction action, ISelectionProvider provider, ISelection selection){
		action.update(selection);
		provider.addSelectionChangedListener(action);
	}

	private IAction[] getActions() {
//		IAction[] actions= new IAction[3];
		IAction[] actions= new IAction[1];
		actions[0]= fOccurrencesInFileAction;
//		actions[1]= fExceptionOccurrencesAction;
//		actions[2]= fFindImplementorOccurrencesAction;
		return actions;
	}

	/* 
	 * Method declared on ActionGroup.
	 */
	public void fillContextMenu(IMenuManager manager) {
		String menuText= SearchMessages.group_occurrences;
		String shortcut= getShortcutString();
		if (shortcut != null) {
			String[] args= new String[] { menuText, shortcut};
			menuText= Messages.format(ActionMessages.QuickMenuAction_menuTextWithShortcut, args); 
		}

		MenuManager javaSearchMM= new MenuManager(menuText, IContextMenuConstants.GROUP_SEARCH);
		IAction[] actions= getActions();
		for (int i= 0; i < actions.length; i++) {
			IAction action= actions[i];
			if (action.isEnabled())
				javaSearchMM.add(action);
		}
		
		if (!javaSearchMM.isEmpty())
			manager.appendToGroup(fGroupId, javaSearchMM);
	}
	
	private String getShortcutString() {
		IBindingService bindingService= (IBindingService)PlatformUI.getWorkbench().getAdapter(IBindingService.class);
		if (bindingService == null)
			return null;
		return bindingService.getBestActiveBindingFormattedFor(IRubyEditorActionDefinitionIds.SEARCH_OCCURRENCES_IN_FILE_QUICK_MENU);
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
//			disposeAction(fFindImplementorOccurrencesAction, provider);
//			disposeAction(fExceptionOccurrencesAction, provider);
			disposeAction(fOccurrencesInFileAction, provider);
		}
		super.dispose();
//		fFindImplementorOccurrencesAction= null;
//		fExceptionOccurrencesAction= null;
		fOccurrencesInFileAction= null;
		updateGlobalActionHandlers();
	}

	private void updateGlobalActionHandlers() {
		if (fActionBars != null) {
			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_OCCURRENCES_IN_FILE, fOccurrencesInFileAction);
//			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_EXCEPTION_OCCURRENCES, fExceptionOccurrencesAction);
//			fActionBars.setGlobalActionHandler(RdtActionConstants.FIND_IMPLEMENT_OCCURRENCES, fFindImplementorOccurrencesAction);
		}
	}

	private void disposeAction(ISelectionChangedListener action, ISelectionProvider provider) {
		if (action != null)
			provider.removeSelectionChangedListener(action);
	}
}

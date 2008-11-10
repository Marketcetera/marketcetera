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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.progress.IProgressService;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.ActionUtil;
import org.rubypeople.rdt.internal.ui.actions.OpenActionUtil;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.RubySearchQuery;
import org.rubypeople.rdt.internal.ui.search.RubySearchScopeFactory;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;
import org.rubypeople.rdt.internal.ui.search.SearchUtil;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;
import org.rubypeople.rdt.ui.search.ElementQuerySpecification;
import org.rubypeople.rdt.ui.search.QuerySpecification;

/**
 * Abstract class for Ruby search actions.
 * <p>
 * Note: This class is for internal use only. Clients should not use this class.
 * </p>
 * 
 * @since 2.0
 */
public abstract class FindAction extends SelectionDispatchAction {

	// A dummy which can't be selected in the UI
	private static final IRubyElement RETURN_WITHOUT_BEEP= RubyCore.create(RubyPlugin.getWorkspace().getRoot());
		
	private Class[] fValidTypes;
	private RubyEditor fEditor;	


	FindAction(IWorkbenchSite site) {
		super(site);
		fValidTypes= getValidTypes();
		init();
	}

	FindAction(RubyEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}
	
	/**
	 * Called once by the constructors to initialize label, tooltip, image and help support of the action.
	 * To be overridden by implementors of this action.
	 */
	abstract void init();

	/**
	 * Called once by the constructors to get the list of the valid input types of the action.
	 * To be overridden by implementors of this action.
	 * @return the valid input types of the action
	 */
	abstract Class[] getValidTypes();
	
	private boolean canOperateOn(IStructuredSelection sel) {
		return sel != null && !sel.isEmpty() && canOperateOn(getRubyElement(sel, true));
	}
		
	boolean canOperateOn(IRubyElement element) {
		if (element == null || fValidTypes == null || fValidTypes.length == 0 || !ActionUtil.isOnBuildPath(element))
			return false;

		for (int i= 0; i < fValidTypes.length; i++) {
			if (fValidTypes[i].isInstance(element)) {
				if (element.getElementType() == IRubyElement.SOURCE_FOLDER)
					return hasChildren((ISourceFolder)element);
				else
					return true;
			}
		}
		return false;
	}
	
	private boolean hasChildren(ISourceFolder packageFragment) {
		try {
			return packageFragment.hasChildren();
		} catch (RubyModelException ex) {
			return false;
		}
	}

	private IRubyElement getTypeIfPossible(IRubyElement o, boolean silent) {
		switch (o.getElementType()) {
			case IRubyElement.SCRIPT:
				if (silent)
					return o;
				else
					return findType((IRubyScript)o, silent);
			default:
				return o;				
		}
	}

	IRubyElement getRubyElement(IStructuredSelection selection, boolean silent) {
		if (selection.size() == 1) {
			Object firstElement= selection.getFirstElement();
			IRubyElement elem= null;
			if (firstElement instanceof IRubyElement) 
				elem= (IRubyElement) firstElement;
			else if (firstElement instanceof IAdaptable) 
				elem= (IRubyElement) ((IAdaptable) firstElement).getAdapter(IRubyElement.class);
			if (elem != null) {
				return getTypeIfPossible(elem, silent);
			}
			
		}
		return null;
	}

	private void showOperationUnavailableDialog() {
		MessageDialog.openInformation(getShell(), SearchMessages.RubyElementAction_operationUnavailable_title, getOperationUnavailableMessage()); 
	}	

	String getOperationUnavailableMessage() {
		return SearchMessages.RubyElementAction_operationUnavailable_generic; 
	}

	private IRubyElement findType(IRubyScript cu, boolean silent) {
		IType[] types= null;
		try {					
			types= cu.getAllTypes();
		} catch (RubyModelException ex) {
			if (RubyModelUtil.isExceptionToBeLogged(ex))
				ExceptionHandler.log(ex, SearchMessages.RubyElementAction_error_open_message); 
			if (silent)
				return RETURN_WITHOUT_BEEP;
			else
				return null;
		}
		if (types.length == 1 || (silent && types.length > 0))
			return types[0];
		if (silent)
			return RETURN_WITHOUT_BEEP;
		if (types.length == 0)
			return null;
		String title= SearchMessages.RubyElementAction_typeSelectionDialog_title; 
		String message = SearchMessages.RubyElementAction_typeSelectionDialog_message; 
		int flags= (RubyElementLabelProvider.SHOW_DEFAULT);						

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new RubyElementLabelProvider(flags));
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setElements(types);
		
		if (dialog.open() == Window.OK)
			return (IType)dialog.getFirstResult();
		else
			return RETURN_WITHOUT_BEEP;
	}
	
	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void run(IStructuredSelection selection) {
		IRubyElement element= getRubyElement(selection, false);
		if (element == null || !element.exists()) {
			showOperationUnavailableDialog();
			return;
		} 
		else if (element == RETURN_WITHOUT_BEEP)
			return;
		
		run(element);
	}

	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void run(ITextSelection selection) {
		if (!ActionUtil.isProcessable(getShell(), fEditor))
			return;
		try {
			String title= SearchMessages.SearchElementSelectionDialog_title; 
			String message= SearchMessages.SearchElementSelectionDialog_message; 
			
			IRubyElement[] elements= SelectionConverter.codeResolveForked(fEditor, true);
			if (elements.length > 0 && canOperateOn(elements[0])) {
				IRubyElement element= elements[0];
				if (elements.length > 1)
					element= OpenActionUtil.selectRubyElement(elements, getShell(), title, message);
				if (element != null)
					run(element);
			}
			else
				showOperationUnavailableDialog();
		} catch (InvocationTargetException ex) {
			String title= SearchMessages.Search_Error_search_title; 
			String message= SearchMessages.Search_Error_codeResolve; 
			ExceptionHandler.handle(ex, getShell(), title, message);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(canOperateOn(selection));
	}

	/* 
	 * Method declared on SelectionChangedAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/**
	 * Executes this action for the given ruby element.
	 * @param element The ruby element to be found.
	 */
	public void run(IRubyElement element) {
		
		if (!ActionUtil.isProcessable(getShell(), element))
			return;
		
		// will return true except for debugging purposes.
		try {
			performNewSearch(element);
		} catch (RubyModelException ex) {
			ExceptionHandler.handle(ex, getShell(), SearchMessages.Search_Error_search_notsuccessful_title, SearchMessages.Search_Error_search_notsuccessful_message); 
		}
	}

	private void performNewSearch(IRubyElement element) throws RubyModelException {
		RubySearchQuery query= new RubySearchQuery(createQuery(element));
		if (query.canRunInBackground()) {
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
			 */
			SearchUtil.runQueryInBackground(query);
		} else {
			IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the VM verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case it would be ISearchQuery).
			 */
			IStatus status= SearchUtil.runQueryInForeground(progressService, query);
			if (status.matches(IStatus.ERROR | IStatus.INFO | IStatus.WARNING)) {
				ErrorDialog.openError(getShell(), SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message, status); 
			}
		}
	}
	
	QuerySpecification createQuery(IRubyElement element) throws RubyModelException {
		RubySearchScopeFactory factory= RubySearchScopeFactory.getInstance();
		IRubySearchScope scope= factory.createWorkspaceScope(true);
		String description= factory.getWorkspaceScopeDescription(true);
		return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

	abstract int getLimitTo();

	IType getType(IRubyElement element) {
		if (element == null)
			return null;
		
		IType type= null;
		if (element.getElementType() == IRubyElement.TYPE)
			type= (IType)element;
		else if (element instanceof IMember)
			type= ((IMember)element).getDeclaringType();
		return type;
	}
	
	RubyEditor getEditor() {
		return fEditor;
	}
		
}

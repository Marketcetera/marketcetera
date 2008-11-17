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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.IRubyStatusConstants;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.ActionMessages;
import org.rubypeople.rdt.internal.ui.actions.ActionUtil;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.util.OpenTypeHierarchyUtil;

/**
 * This action opens a type hierarchy on the selected type.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>IType</code>.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class OpenTypeHierarchyAction extends SelectionDispatchAction {
	
	private RubyEditor fEditor;
	
	/**
	 * Creates a new <code>OpenTypeHierarchyAction</code>. The action requires
	 * that the selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public OpenTypeHierarchyAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.OpenTypeHierarchyAction_label); 
		setToolTipText(ActionMessages.OpenTypeHierarchyAction_tooltip); 
		setDescription(ActionMessages.OpenTypeHierarchyAction_description); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.OPEN_TYPE_HIERARCHY_ACTION);
	}
	
	/**
	 * Creates a new <code>OpenTypeHierarchyAction</code>. The action requires
	 * that the selection provided by the given selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 * @param provider a special selection provider which is used instead 
	 *  of the site's selection provider or <code>null</code> to use the site's
	 *  selection provider
	 * 
	 * @since 3.2
	 * @deprecated Use {@link #setSpecialSelectionProvider(ISelectionProvider)} instead. This API will be
	 * removed after 3.2 M5.
     */
    public OpenTypeHierarchyAction(IWorkbenchSite site, ISelectionProvider provider) {
        this(site);
        setSpecialSelectionProvider(provider);
    }

	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public OpenTypeHierarchyAction(RubyEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(isEnabled(selection));
	}
	
	private boolean isEnabled(IStructuredSelection selection) {
		if (selection.size() != 1)
			return false;
		Object input= selection.getFirstElement();
		
		if (!(input instanceof IRubyElement))
			return false;
		switch (((IRubyElement)input).getElementType()) {
			case IRubyElement.METHOD:
			case IRubyElement.FIELD:
			case IRubyElement.TYPE:
				return true;
			case IRubyElement.SOURCE_FOLDER_ROOT:
			case IRubyElement.RUBY_PROJECT:
			case IRubyElement.SOURCE_FOLDER:
			case IRubyElement.IMPORT_DECLARATION:	
			case IRubyElement.SCRIPT:
				return true;
			case IRubyElement.LOCAL_VARIABLE:
			default:
				return false;
		}
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(ITextSelection selection) {
		IRubyElement input= SelectionConverter.getInput(fEditor);
		if (!ActionUtil.isProcessable(getShell(), input))
			return;		
		
		try {
			IRubyElement[] elements= SelectionConverter.codeResolveOrInputForked(fEditor);
			if (elements == null)
				return;
			List candidates= new ArrayList(elements.length);
			for (int i= 0; i < elements.length; i++) {
				IRubyElement[] resolvedElements= OpenTypeHierarchyUtil.getCandidates(elements[i]);
				if (resolvedElements != null)	
					candidates.addAll(Arrays.asList(resolvedElements));
			}
			run((IRubyElement[])candidates.toArray(new IRubyElement[candidates.size()]));
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), getDialogTitle(), ActionMessages.SelectionConverter_codeResolve_failed);
		} catch (InterruptedException e) {
			// cancelled
		}
	}
	
	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		if (selection.size() != 1)
			return;
		Object input= selection.getFirstElement();

		if (!(input instanceof IRubyElement)) {
			IStatus status= createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_ruby_element); 
			ErrorDialog.openError(getShell(), getDialogTitle(), ActionMessages.OpenTypeHierarchyAction_messages_title, status); 
			return;
		}
		IRubyElement element= (IRubyElement) input;
		if (!ActionUtil.isProcessable(getShell(), element))
			return;

		List result= new ArrayList(1);
		IStatus status= compileCandidates(result, element);
		if (status.isOK()) {
			run((IRubyElement[]) result.toArray(new IRubyElement[result.size()]));
		} else {
			ErrorDialog.openError(getShell(), getDialogTitle(), ActionMessages.OpenTypeHierarchyAction_messages_title, status); 
		}
	}

	/*
	 * No Rubydoc since the method isn't meant to be public but is
	 * since the beginning
	 */
	public void run(IRubyElement[] elements) {
		if (elements.length == 0) {
			getShell().getDisplay().beep();
			return;
		}
		OpenTypeHierarchyUtil.open(elements, getSite().getWorkbenchWindow());
	}
	
	private static String getDialogTitle() {
		return ActionMessages.OpenTypeHierarchyAction_dialog_title; 
	}
	
	private static IStatus compileCandidates(List result, IRubyElement elem) {
		IStatus ok= new Status(IStatus.OK, RubyPlugin.getPluginId(), 0, "", null); //$NON-NLS-1$		
		try {
			switch (elem.getElementType()) {
				case IRubyElement.METHOD:
				case IRubyElement.FIELD:
				case IRubyElement.TYPE:
				case IRubyElement.SOURCE_FOLDER_ROOT:
				case IRubyElement.RUBY_PROJECT:
					result.add(elem);
					return ok;
				case IRubyElement.SOURCE_FOLDER:
					if (((ISourceFolder)elem).containsRubyResources()) {
						result.add(elem);
						return ok;
					}
					return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_ruby_resources); 
				case IRubyElement.IMPORT_DECLARATION:	
					IImportDeclaration decl= (IImportDeclaration) elem;
					elem= elem.getRubyProject().findType(elem.getElementName());
					
					if (elem != null) {
						result.add(elem);
						return ok;
					}
					return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_unknown_import_decl);			
				case IRubyElement.SCRIPT:
					IRubyScript cu= (IRubyScript)elem;
					IType[] types= cu.getTypes();
					if (types.length > 0) {
						result.addAll(Arrays.asList(types));
						return ok;
					}
					return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_types); 
			}
		} catch (RubyModelException e) {
			return e.getStatus();
		}
		return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_valid_ruby_element); 
	}
	
	private static IStatus createStatus(String message) {
		return new Status(IStatus.INFO, RubyPlugin.getPluginId(), IRubyStatusConstants.INTERNAL_ERROR, message, null);
	}			
}

/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 *          (report 36180: Callers/Callees view)
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
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.IRubyStatusConstants;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.ActionMessages;
import org.rubypeople.rdt.internal.ui.actions.ActionUtil;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.callhierarchy.CallHierarchyMessages;
import org.rubypeople.rdt.internal.ui.callhierarchy.CallHierarchyUI;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;

/**
 * This action opens a call hierarchy on the selected method.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>IMethod</code>.
 */
public class OpenCallHierarchyAction extends SelectionDispatchAction {
    
    private RubyEditor fEditor;
    
    /**
     * Creates a new <code>OpenCallHierarchyAction</code>. The action requires
     * that the selection provided by the site's selection provider is of type <code>
     * org.eclipse.jface.viewers.IStructuredSelection</code>.
     * 
     * @param site the site providing context information for this action
     */
    public OpenCallHierarchyAction(IWorkbenchSite site) {
        super(site);
        setText(CallHierarchyMessages.OpenCallHierarchyAction_label); 
        setToolTipText(CallHierarchyMessages.OpenCallHierarchyAction_tooltip); 
        setDescription(CallHierarchyMessages.OpenCallHierarchyAction_description); 
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CALL_HIERARCHY_OPEN_ACTION);

    }
    
    /**
     * Creates a new <code>OpenCallHierarchyAction</code>. The action requires
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
    public OpenCallHierarchyAction(IWorkbenchSite site, ISelectionProvider provider) {
        this(site);
        setSpecialSelectionProvider(provider);
    }
    
    /**
     * Note: This constructor is for internal use only. Clients should not call this constructor.
     */
    public OpenCallHierarchyAction(RubyEditor editor) {
        this(editor.getEditorSite());
        fEditor= editor;
        setEnabled(SelectionConverter.canOperateOn(fEditor));
    }
    
    /* (non-Javadoc)
     * Method declared on SelectionDispatchAction.
     */
	public void selectionChanged(ITextSelection selection) {
        // Do nothing
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
                return true;
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
			    IRubyElement[] resolvedElements= CallHierarchyUI.getCandidates(elements[i]);
			    if (resolvedElements != null)   
			        candidates.addAll(Arrays.asList(resolvedElements));
			}
			if (candidates.isEmpty()) {
			    IRubyElement enclosingMethod= getEnclosingMethod(input, selection);
			    if (enclosingMethod != null) {
			        candidates.add(enclosingMethod);
			    }
			}
			run((IRubyElement[])candidates.toArray(new IRubyElement[candidates.size()]));
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), getErrorDialogTitle(), ActionMessages.SelectionConverter_codeResolve_failed);
		} catch (InterruptedException e) {
			// cancelled
		}
    }
    
    private IRubyElement getEnclosingMethod(IRubyElement input, ITextSelection selection) {
        IRubyElement enclosingElement= null;
        try {
            switch (input.getElementType()) {
                case IRubyElement.SCRIPT :
                	IRubyScript cu= (IRubyScript) input.getAncestor(IRubyElement.SCRIPT);
                    if (cu != null) {
                        enclosingElement= cu.getElementAt(selection.getOffset());
                    }
                    break;
            }
            if (enclosingElement != null && enclosingElement.getElementType() == IRubyElement.METHOD) {
                return enclosingElement;
            }
        } catch (RubyModelException e) {
            RubyPlugin.log(e);
        }

        return null;
    }

    /* (non-Javadoc)
     * Method declared on SelectionDispatchAction.
     */
	public void run(IStructuredSelection selection) {
        if (selection.size() != 1)
            return;
        Object input= selection.getFirstElement();

        if (!(input instanceof IRubyElement)) {
            IStatus status= createStatus(CallHierarchyMessages.OpenCallHierarchyAction_messages_no_java_element); 
            openErrorDialog(status);
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
            openErrorDialog(status);
        }
    }
    
    private int openErrorDialog(IStatus status) {
        String message= CallHierarchyMessages.OpenCallHierarchyAction_messages_title; 
        String dialogTitle= getErrorDialogTitle();
        return ErrorDialog.openError(getShell(), dialogTitle, message, status);
	}

    private static String getErrorDialogTitle() {
        return CallHierarchyMessages.OpenCallHierarchyAction_dialog_title; 
    }
    
	public void run(IRubyElement[] elements) {
        if (elements.length == 0) {
            getShell().getDisplay().beep();
            return;
        }
        CallHierarchyUI.open(elements, getSite().getWorkbenchWindow());
    }
    
    private static IStatus compileCandidates(List result, IRubyElement elem) {
        IStatus ok= new Status(IStatus.OK, RubyPlugin.getPluginId(), 0, "", null); //$NON-NLS-1$        
        switch (elem.getElementType()) {
            case IRubyElement.METHOD:
                result.add(elem);
                return ok;
        }
        return createStatus(CallHierarchyMessages.OpenCallHierarchyAction_messages_no_valid_java_element); 
    }
    
    private static IStatus createStatus(String message) {
        return new Status(IStatus.INFO, RubyPlugin.getPluginId(), IRubyStatusConstants.INTERNAL_ERROR, message, null);
    }           
}

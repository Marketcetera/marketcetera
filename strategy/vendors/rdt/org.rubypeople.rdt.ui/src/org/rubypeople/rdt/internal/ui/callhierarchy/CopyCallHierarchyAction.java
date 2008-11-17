/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation
 *          (report 36180: Callers/Callees view)
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.callhierarchy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.util.SelectionUtil;

class CopyCallHierarchyAction extends Action {
    private static final char INDENTATION= '\t';  
    
    private CallHierarchyViewPart fView;
    private CallHierarchyViewer fViewer;
	
	private final Clipboard fClipboard;

	public CopyCallHierarchyAction(CallHierarchyViewPart view, Clipboard clipboard, CallHierarchyViewer viewer) {
		super(CallHierarchyMessages.CopyCallHierarchyAction_label);  
		Assert.isNotNull(clipboard);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CALL_HIERARCHY_COPY_ACTION);
		fView= view;
		fClipboard= clipboard;
        fViewer= viewer;
	}

    public boolean canActionBeAdded() {
        Object element = SelectionUtil.getSingleElement(getSelection());
        return element != null;
    }
    
    private ISelection getSelection() {
        ISelectionProvider provider = fView.getSite().getSelectionProvider();

        if (provider != null) {
            return provider.getSelection();
        }

        return null;
    }
    
	/*
	 * @see IAction#run()
	 */
	public void run() {
        StringBuffer buf= new StringBuffer();
        addCalls(fViewer.getTree().getSelection()[0], 0, buf);

		TextTransfer plainTextTransfer = TextTransfer.getInstance();
		try{
			fClipboard.setContents(
				new String[]{ convertLineTerminators(buf.toString()) }, 
				new Transfer[]{ plainTextTransfer });
		}  catch (SWTError e){
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) 
				throw e;
			if (MessageDialog.openQuestion(fView.getViewSite().getShell(), CallHierarchyMessages.CopyCallHierarchyAction_problem, CallHierarchyMessages.CopyCallHierarchyAction_clipboard_busy))  
				run();
		}
	}
	
	/**
     * Adds the specified TreeItem's text to the StringBuffer
     * 
     * @param item
     * @param buf
     */
    private void addCalls(TreeItem item, int indent, StringBuffer buf) {
        for (int i= 0; i < indent; i++) {
            buf.append(INDENTATION);
        }

        buf.append(item.getText());
        buf.append('\n');
        
        if (item.getExpanded()) {
            TreeItem[] items= item.getItems();
            for (int i= 0; i < items.length; i++) {
                addCalls(items[i], indent + 1, buf);
            }
        }        
    }

    private String convertLineTerminators(String in) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter printWriter= new PrintWriter(stringWriter);
		StringReader stringReader= new StringReader(in);
		BufferedReader bufferedReader= new BufferedReader(stringReader);		
		String line;
		try {
			while ((line= bufferedReader.readLine()) != null) {
				printWriter.println(line);
			}
		} catch (IOException e) {
			return in; // return the call hierarchy unfiltered
		}
		return stringWriter.toString();
	}
}

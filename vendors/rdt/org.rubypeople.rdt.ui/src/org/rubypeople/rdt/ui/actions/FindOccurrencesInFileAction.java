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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.ActionUtil;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.OccurrencesFinder;
import org.rubypeople.rdt.internal.ui.search.FindOccurrencesEngine;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;

/**
 * Action to find all occurrences of a compilation unit member (e.g.
 * fields, methods, types, and local variables) in a file. 
 * <p>
 * Action is applicable to selections containing elements of type
 * <tt>IMember</tt>.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 1.0
 */
public class FindOccurrencesInFileAction extends SelectionDispatchAction {
	
	private RubyEditor fEditor;
	private IActionBars fActionBars;
	
	/**
	 * Creates a new <code>FindOccurrencesInFileAction</code>. The action requires 
	 * that the selection provided by the view part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param part the part providing context information for this action
	 */
	public FindOccurrencesInFileAction(IViewPart part) {
		this(part.getSite());
	}
	
	/**
	 * Creates a new <code>FindOccurrencesInFileAction</code>. The action requires 
	 * that the selection provided by the page's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param page the page providing context information for this action
	 */
	public FindOccurrencesInFileAction(Page page) {
		this(page.getSite());
	}
 	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public FindOccurrencesInFileAction(RubyEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(getEditorInput(editor) != null);
	}
	
	/**
	 * Creates a new <code>FindOccurrencesInFileAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 * @since 3.1
	 */
	public FindOccurrencesInFileAction(IWorkbenchSite site) {
		super(site);
		
		if (site instanceof IViewSite)
			fActionBars= ((IViewSite)site).getActionBars();
		else if (site instanceof IEditorSite)
			fActionBars= ((IEditorSite)site).getActionBars();
		else if (site instanceof IPageSite)
			fActionBars= ((IPageSite)site).getActionBars();
		
		setText(SearchMessages.Search_FindOccurrencesInFile_label); 
		setToolTipText(SearchMessages.Search_FindOccurrencesInFile_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.FIND_OCCURRENCES_IN_FILE_ACTION);
	}
	
	//---- Structured Selection -------------------------------------------------------------
	
	/* (non-RubyDoc)
	 * Method declared in SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(getMember(selection) != null);
	}
	
	/* (non-RubyDoc)
	 * Method declared in SelectionDispatchAction.
	 */
	private IMember getMember(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;
		Object o= selection.getFirstElement();
		if (o instanceof IMember) {
			IMember member= (IMember)o;
			try {
				if (member.getNameRange() == null)
					return null;
			} catch (RubyModelException ex) {
				return null;
			}
			return member;
		}
		return null;
	}
	
	public void run(IStructuredSelection selection) {
		IMember member= getMember(selection);
		if (!ActionUtil.isProcessable(getShell(), member))
			return;
		FindOccurrencesEngine engine= FindOccurrencesEngine.create(member, new OccurrencesFinder());
		try {
			ISourceRange range= member.getNameRange();
			String result= engine.run(range.getOffset(), range.getLength());
			if (result != null)
				showMessage(getShell(), fActionBars, result);
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}
	}
	
	private static void showMessage(Shell shell, IActionBars actionBars, String msg) {
		if (actionBars != null) {
			IStatusLineManager statusLine= actionBars.getStatusLineManager();
			if (statusLine != null)
				statusLine.setMessage(msg);
		}
		shell.getDisplay().beep();
	}
	
	//---- Text Selection ----------------------------------------------------------------------
	
	/* (non-RubyDoc)
	 * Method declared in SelectionDispatchAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/* (non-RubyDoc)
	 * Method declared in SelectionDispatchAction.
	 */
	public final void run(ITextSelection ts) {
		IRubyElement input= getEditorInput(fEditor);
		if (!ActionUtil.isProcessable(getShell(), input))
			return;
		FindOccurrencesEngine engine= FindOccurrencesEngine.create(input, new OccurrencesFinder());
		try {
			String result= engine.run(ts.getOffset(), ts.getLength());
			if (result != null)
				showMessage(getShell(), fEditor, result);
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}
	}

	private static IRubyElement getEditorInput(RubyEditor editor) {
		IEditorInput input= editor.getEditorInput();
		if (input instanceof IRubyScriptEditorInput)
			return ((IRubyScriptEditorInput)input).getRubyScript();
		return  RubyPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(input);
	} 
		
	private static void showMessage(Shell shell, RubyEditor editor, String msg) {
		IEditorStatusLine statusLine= (IEditorStatusLine) editor.getAdapter(IEditorStatusLine.class);
		if (statusLine != null) 
			statusLine.setMessage(true, msg, null); 
		shell.getDisplay().beep();
	}
}

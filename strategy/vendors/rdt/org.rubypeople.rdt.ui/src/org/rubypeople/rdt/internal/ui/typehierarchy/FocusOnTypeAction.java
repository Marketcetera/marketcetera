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
package org.rubypeople.rdt.internal.ui.typehierarchy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.dialogs.TypeSelectionDialog2;

/**
 * Refocuses the type hierarchy on a type selection from a all types dialog.
 */
public class FocusOnTypeAction extends Action {
			
	private TypeHierarchyViewPart fViewPart;
	
	public FocusOnTypeAction(TypeHierarchyViewPart part) {
		super(TypeHierarchyMessages.FocusOnTypeAction_label); 
		setDescription(TypeHierarchyMessages.FocusOnTypeAction_description); 
		setToolTipText(TypeHierarchyMessages.FocusOnTypeAction_tooltip); 
		
		fViewPart= part;
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,	IRubyHelpContextIds.FOCUS_ON_TYPE_ACTION);
	}

	/*
	 * @see Action#run
	 */
	public void run() {
		Shell parent= fViewPart.getSite().getShell();
		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(parent, false, 
			PlatformUI.getWorkbench().getProgressService(), 
			SearchEngine.createWorkspaceScope(), IRubySearchConstants.TYPE);
	
		dialog.setTitle(TypeHierarchyMessages.FocusOnTypeAction_dialog_title); 
		dialog.setMessage(TypeHierarchyMessages.FocusOnTypeAction_dialog_message); 
		if (dialog.open() != IDialogConstants.OK_ID) {
			return;
		}
		
		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IType type= (IType)types[0];
			fViewPart.setInputElement(type);
		}
	}	
}

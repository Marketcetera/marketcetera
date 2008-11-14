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

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;

/**
 * Finds declarations of the selected element in the workspace.
 * The action is applicable to selections representing a Ruby element.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 1.0
 */
public class FindDeclarationsAction extends FindAction {
	
	/**
	 * Creates a new <code>FindDeclarationsAction</code>. The action requires
	 * that the selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindDeclarationsAction(IWorkbenchSite site) {
		super(site);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public FindDeclarationsAction(RubyEditor editor) {
		super(editor);
	}
	
	void init() {
		setText(SearchMessages.Search_FindDeclarationAction_label); 
		setToolTipText(SearchMessages.Search_FindDeclarationAction_tooltip); 
		setImageDescriptor(RubyPluginImages.DESC_OBJS_SEARCH_DECL);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.FIND_DECLARATIONS_IN_WORKSPACE_ACTION);
	}
	
	Class[] getValidTypes() {
		return new Class[] { IField.class, IMethod.class, IType.class, IRubyScript.class, IImportDeclaration.class, ISourceFolder.class };
	}
	
	int getLimitTo() {
		return IRubySearchConstants.DECLARATIONS | IRubySearchConstants.IGNORE_DECLARING_TYPE ;
	}
	
}

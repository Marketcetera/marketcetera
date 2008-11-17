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

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;

/**
 * Finds field read accesses of the selected element in working sets.
 * The action is applicable to selections representing a Ruby field.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class FindReadReferencesInWorkingSetAction extends FindReferencesInWorkingSetAction {

	/**
	 * Creates a new <code>FindReadReferencesInWorkingSetAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>. The user will be 
	 * prompted to select the working sets.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindReadReferencesInWorkingSetAction(IWorkbenchSite site) {
		super(site);
	}

	/**
	 * Creates a new <code>FindReadReferencesInWorkingSetAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site			the site providing context information for this action
	 * @param workingSets	the working sets to be used in the search
	 */
	public FindReadReferencesInWorkingSetAction(IWorkbenchSite site, IWorkingSet[] workingSets) {
		super(site, workingSets);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public FindReadReferencesInWorkingSetAction(RubyEditor editor) {
		super(editor);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 * @param workingSets the working sets to be used in the search
	 */
	public FindReadReferencesInWorkingSetAction(RubyEditor editor, IWorkingSet[] workingSets) {
		super(editor, workingSets);
	}
	
	Class[] getValidTypes() {
		return new Class[] { IField.class };
	}
	
	void init() {
		setText(SearchMessages.Search_FindReadReferencesInWorkingSetAction_label); 
		setToolTipText(SearchMessages.Search_FindReadReferencesInWorkingSetAction_tooltip); 
		setImageDescriptor(RubyPluginImages.DESC_OBJS_SEARCH_REF);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.FIND_READ_REFERENCES_IN_WORKING_SET_ACTION);
	}

	int getLimitTo() {
		return IRubySearchConstants.READ_ACCESSES;
	}

	String getOperationUnavailableMessage() {
		return SearchMessages.RubyElementAction_operationUnavailable_field; 
	}
}

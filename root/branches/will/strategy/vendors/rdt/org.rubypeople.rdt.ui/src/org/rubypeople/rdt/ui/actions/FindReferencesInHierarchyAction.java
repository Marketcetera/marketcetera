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
import org.rubypeople.rdt.core.ILocalVariable;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.RubySearchScopeFactory;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;
import org.rubypeople.rdt.ui.search.ElementQuerySpecification;
import org.rubypeople.rdt.ui.search.QuerySpecification;

/**
 * Finds references of the selected element in its hierarchy.
 * The action is applicable to selections representing a Ruby element.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class FindReferencesInHierarchyAction extends FindReferencesAction {

	/**
	 * Creates a new <code>FindReferencesInHierarchyAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindReferencesInHierarchyAction(IWorkbenchSite site) {
		super(site);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public FindReferencesInHierarchyAction(RubyEditor editor) {
		super(editor);
	}
	
	Class[] getValidTypes() {
		return new Class[] { IRubyScript.class, IType.class, IMethod.class, IField.class, ILocalVariable.class };
	}
	
	void init() {
		setText(SearchMessages.Search_FindHierarchyReferencesAction_label); 
		setToolTipText(SearchMessages.Search_FindHierarchyReferencesAction_tooltip); 
		setImageDescriptor(RubyPluginImages.DESC_OBJS_SEARCH_REF);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.FIND_REFERENCES_IN_HIERARCHY_ACTION);
	}

	QuerySpecification createQuery(IRubyElement element) throws RubyModelException {
		IType type= getType(element);
		if (type == null) {
			return super.createQuery(element);
		}
		RubySearchScopeFactory factory= RubySearchScopeFactory.getInstance();
		IRubySearchScope scope= SearchEngine.createHierarchyScope(type);
		String description= factory.getHierarchyScopeDescription(type);
		return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

}

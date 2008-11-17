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
 *   Michael Fraenkel (fraenkel@us.ibm.com) - patch
 *          (report 60714: Call Hierarchy: display search scope in view title)
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.callhierarchy;

import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.search.RubySearchScopeFactory;


class SearchScopeProjectAction extends SearchScopeAction {
	private final SearchScopeActionGroup fGroup;
	
	public SearchScopeProjectAction(SearchScopeActionGroup group) {
		super(group, CallHierarchyMessages.SearchScopeActionGroup_project_text); 
		this.fGroup = group;
		setToolTipText(CallHierarchyMessages.SearchScopeActionGroup_project_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CALL_HIERARCHY_SEARCH_SCOPE_ACTION);
	}
	
	public IRubySearchScope getSearchScope() {
		IMethod method = this.fGroup.getView().getMethod();
		if (method == null) {
			return null;
		}
		
		RubySearchScopeFactory factory= RubySearchScopeFactory.getInstance();
		return factory.createRubyProjectSearchScope(method.getRubyProject(), true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.callhierarchy.SearchScopeActionGroup.SearchScopeAction#getSearchScopeType()
	 */
	public int getSearchScopeType() {
		return SearchScopeActionGroup.SEARCH_SCOPE_TYPE_PROJECT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.callhierarchy.SearchScopeAction#getFullDescription()
	 */
	public String getFullDescription() {
		IMethod method = this.fGroup.getView().getMethod();
		if (method != null) {
			RubySearchScopeFactory factory= RubySearchScopeFactory.getInstance();
			return factory.getProjectScopeDescription(method.getRubyProject(), true);
		}
		return ""; //$NON-NLS-1$
	}
}

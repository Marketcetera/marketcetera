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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.search.RubySearchScopeFactory;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;

class SelectWorkingSetAction extends Action {
	private final SearchScopeActionGroup fGroup;
	
	public SelectWorkingSetAction(SearchScopeActionGroup group) {
		super(CallHierarchyMessages.SearchScopeActionGroup_workingset_select_text); 
		this.fGroup = group;
		setToolTipText(CallHierarchyMessages.SearchScopeActionGroup_workingset_select_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CALL_HIERARCHY_SEARCH_SCOPE_ACTION);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		try {
			IWorkingSet[] workingSets;
			workingSets = RubySearchScopeFactory.getInstance().queryWorkingSets();
			if (workingSets != null) {
				this.fGroup.setActiveWorkingSets(workingSets);
				SearchUtil.updateLRUWorkingSets(workingSets);
			} else {
				this.fGroup.setActiveWorkingSets(null);
			}
		} catch (RubyModelException e) {
			ExceptionHandler.handle(e, RubyPlugin.getActiveWorkbenchShell(), 
					CallHierarchyMessages.SelectWorkingSetAction_error_title, 
					CallHierarchyMessages.SelectWorkingSetAction_error_message); 
		}
	}
}

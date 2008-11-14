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
package org.rubypeople.rdt.internal.ui.workingsets;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;

/**
 * Clears the selected working set in the action group's view.
 * 
 * @since 2.0
 */
public class ClearWorkingSetAction extends Action {
	
	private WorkingSetFilterActionGroup fActionGroup;

	public ClearWorkingSetAction(WorkingSetFilterActionGroup actionGroup) {
		super(WorkingSetMessages.ClearWorkingSetAction_text); 
		Assert.isNotNull(actionGroup);
		setToolTipText(WorkingSetMessages.ClearWorkingSetAction_toolTip); 
		setEnabled(actionGroup.getWorkingSet() != null);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CLEAR_WORKING_SET_ACTION);
		fActionGroup= actionGroup;
	}

	/*
	 * Overrides method from Action
	 */
	public void run() {
		fActionGroup.setWorkingSet(null, true);
	}
}

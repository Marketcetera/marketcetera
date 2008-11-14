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

import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

/**
 * Wraps a <code>RubyElementSearchActions</code> to find its results
 * in the specified working set.
 * <p>
 * The action is applicable to selections and Search view entries
 * representing a Ruby element.
 * 
 * <p>
 * Note: This class is for internal use only. Clients should not use this class.
 * </p>
 * 
 * @since 2.0
 */
public class WorkingSetFindAction extends FindAction {

	private FindAction fAction;

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 */
	public WorkingSetFindAction(IWorkbenchSite site, FindAction action, String workingSetName) {
		super(site);
		init(action, workingSetName);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 */
	public WorkingSetFindAction(RubyEditor editor, FindAction action, String workingSetName) {
		super(editor);
		init(action, workingSetName);
	}

	Class[] getValidTypes() {
		return null; // ignore, we override canOperateOn
	}
	
	void init() {
		// ignore: do our own init in 'init(FindAction, String)'
	}
	
	private void init(FindAction action, String workingSetName) {
		Assert.isNotNull(action);
		fAction= action;
		setText(workingSetName);
		setImageDescriptor(action.getImageDescriptor());
		setToolTipText(action.getToolTipText());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.WORKING_SET_FIND_ACTION);
	}
	
	public void run(IRubyElement element) {
		fAction.run(element);
	}

	boolean canOperateOn(IRubyElement element) {
		return fAction.canOperateOn(element);
	}

	int getLimitTo() {
		return -1;
	}

	String getOperationUnavailableMessage() {
		return fAction.getOperationUnavailableMessage();
	}

}

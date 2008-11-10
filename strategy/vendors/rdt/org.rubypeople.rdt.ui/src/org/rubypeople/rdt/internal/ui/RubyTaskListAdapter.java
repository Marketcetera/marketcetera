/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;

import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IRubyElement;

import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;

public class RubyTaskListAdapter implements ITaskListResourceAdapter {
	/*
	 * @see ITaskListResourceAdapter#getAffectedResource(IAdaptable)
	 */
	public IResource getAffectedResource(IAdaptable element) {
		IRubyElement ruby = (IRubyElement) element;
		IResource resource= ruby.getResource();
		if (resource != null)
			return resource; 
		
		IRubyScript script= (IRubyScript) ruby.getAncestor(IRubyElement.SCRIPT);
		if (script != null) {
			return RubyModelUtil.toOriginal(script).getResource();
		}
		return null;
	 }
}


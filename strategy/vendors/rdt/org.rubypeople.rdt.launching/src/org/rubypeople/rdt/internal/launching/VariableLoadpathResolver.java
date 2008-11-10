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

package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntryResolver;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;


public class VariableLoadpathResolver implements IRuntimeLoadpathEntryResolver {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(org.eclipse.jdt.launching.IRuntimeLoadpathEntry, org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		return resolveRuntimeLoadpathEntry(entry);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(org.eclipse.jdt.launching.IRuntimeLoadpathEntry, org.eclipse.jdt.core.IJavaProject)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, IRubyProject project) throws CoreException {
		return resolveRuntimeLoadpathEntry(entry);
	}

	private IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry) throws CoreException{
		String variableString = ((VariableLoadpathEntry)entry).getVariableString();
		String strpath = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(variableString);
		IPath path = new Path(strpath).makeAbsolute();
		IRuntimeLoadpathEntry archiveEntry = RubyRuntime.newArchiveRuntimeLoadpathEntry(path);
		return new IRuntimeLoadpathEntry[] { archiveEntry };	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver#resolveVMInstall(org.eclipse.jdt.core.ILoadpathEntry)
	 */
	public IVMInstall resolveVMInstall(ILoadpathEntry entry) throws CoreException {
		return null;
	}
}

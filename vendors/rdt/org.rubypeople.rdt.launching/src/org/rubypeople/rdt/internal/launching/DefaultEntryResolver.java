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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry2;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntryResolver;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

/**
 * Default resolver for a contributed classpath entry
 */
public class DefaultEntryResolver implements IRuntimeLoadpathEntryResolver {
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(org.eclipse.jdt.launching.IRuntimeLoadpathEntry, org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		IRuntimeLoadpathEntry2 entry2 = (IRuntimeLoadpathEntry2)entry;
		IRuntimeLoadpathEntry[] entries = entry2.getRuntimeLoadpathEntries(configuration);
		List resolved = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			IRuntimeLoadpathEntry[] temp = RubyRuntime.resolveRuntimeLoadpathEntry(entries[i], configuration);
			for (int j = 0; j < temp.length; j++) {
				resolved.add(temp[j]);
			}
		}
		return (IRuntimeLoadpathEntry[]) resolved.toArray(new IRuntimeLoadpathEntry[resolved.size()]);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(org.eclipse.jdt.launching.IRuntimeLoadpathEntry, org.eclipse.jdt.core.IRubyProject)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, IRubyProject project) throws CoreException {
		IRuntimeLoadpathEntry2 entry2 = (IRuntimeLoadpathEntry2)entry;
		IRuntimeLoadpathEntry[] entries = entry2.getRuntimeLoadpathEntries(null);
		List resolved = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			IRuntimeLoadpathEntry[] temp = RubyRuntime.resolveRuntimeLoadpathEntry(entries[i], project);
			for (int j = 0; j < temp.length; j++) {
				resolved.add(temp[j]);
			}
		}
		return (IRuntimeLoadpathEntry[]) resolved.toArray(new IRuntimeLoadpathEntry[resolved.size()]);
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver#resolveVMInstall(org.eclipse.jdt.core.ILoadpathEntry)
	 */
	public IVMInstall resolveVMInstall(ILoadpathEntry entry) throws CoreException {
		return null;
	}
}

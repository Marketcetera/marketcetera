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
package org.rubypeople.rdt.launching;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.IRubyProject;

/**
 * Default implementation for loadpath provider.
 * <p>
 * This class may be subclassed.
 * </p>
 * @since 0.9.0
 */
public class StandardLoadpathProvider implements IRuntimeLoadpathProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathProvider#computeUnresolvedLoadpath(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] computeUnresolvedLoadpath(ILaunchConfiguration configuration) throws CoreException {
		boolean useDefault = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_DEFAULT_LOADPATH, true);
		if (useDefault) {
			IRubyProject proj = RubyRuntime.getRubyProject(configuration);
			IRuntimeLoadpathEntry jreEntry = RubyRuntime.computeRubyVMEntry(configuration);
			if (proj == null) {
				//no project - use default libraries
				if (jreEntry == null) {
					return new IRuntimeLoadpathEntry[0];
				}
				return new IRuntimeLoadpathEntry[]{jreEntry};				
			}
			IRuntimeLoadpathEntry[] entries = RubyRuntime.computeUnresolvedRuntimeLoadpath(proj);
			// replace project JRE with config's JRE
			IRuntimeLoadpathEntry projEntry = RubyRuntime.computeRubyVMEntry(proj);
			if (jreEntry != null && projEntry != null) {
				if (!jreEntry.equals(projEntry)) {
					for (int i = 0; i < entries.length; i++) {
						IRuntimeLoadpathEntry entry = entries[i];
						if (entry.equals(projEntry)) {
							entries[i] = jreEntry;
							return entries;
						}
					}
				}
			}
			return entries;
		}
		// recover persisted classpath
		return recoverRuntimePath(configuration, IRubyLaunchConfigurationConstants.ATTR_LOADPATH);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathProvider#resolveLoadpath(org.eclipse.jdt.launching.IRuntimeLoadpathEntry[], org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] resolveLoadpath(IRuntimeLoadpathEntry[] entries, ILaunchConfiguration configuration) throws CoreException {
		List all = new ArrayList(entries.length);
		for (int i = 0; i < entries.length; i++) {
			IRuntimeLoadpathEntry[] resolved =RubyRuntime.resolveRuntimeLoadpathEntry(entries[i], configuration);
			for (int j = 0; j < resolved.length; j++) {
				all.add(resolved[j]);
			}
		}
		return (IRuntimeLoadpathEntry[])all.toArray(new IRuntimeLoadpathEntry[all.size()]);
	}
	
	/**
	 * Returns a collection of runtime classpath entries that are defined in the
	 * specified attribute of the given launch configuration. When present,
	 * the attribute must contain a list of runtime classpath entry mementos.
	 * 
	 * @param configuration launch configuration
	 * @param attribute attribute name containing the list of entries
	 * @return collection of runtime classpath entries that are defined in the
	 *  specified attribute of the given launch configuration
	 * @exception CoreException if unable to retrieve the list
	 */
	protected IRuntimeLoadpathEntry[] recoverRuntimePath(ILaunchConfiguration configuration, String attribute) throws CoreException {
		List entries = configuration.getAttribute(attribute, Collections.EMPTY_LIST);
		IRuntimeLoadpathEntry[] rtes = new IRuntimeLoadpathEntry[entries.size()];
		Iterator iter = entries.iterator();
		int i = 0;
		while (iter.hasNext()) {
			rtes[i] = RubyRuntime.newRuntimeLoadpathEntry((String)iter.next());
			i++;
		}
		return rtes;		
	}	

}

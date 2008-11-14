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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.w3c.dom.Element;

/**
 * Enhancements to <code>IRuntimeLoadpathEntry</code> to support
 * extensible runtime classpath entries. Contributed runtime classpath
 * entries have a type of <code>OTHER</code>, and are contributed to
 * the <code>runtimeLoadpathEntries</code> extension point.
 * <p>
 * Clients are not intended to implement this interface, as new types
 * of runtime classpath entries are only intended to be contributed
 * by the Java debugger.
 * </p>
 * @since 0.9.0
 * @see org.rubypeople.rdt.launching.IRuntimeLoadpathEntry
 */
public interface IRuntimeLoadpathEntry2 extends IRuntimeLoadpathEntry {
	
	/**
	 * Initializes this runtime classpath entry from the given memento.
	 * 
	 * @param memento memento created by a classpath entry of the same type
	 * @throws CoreException if unable to initialize from the given memento
	 */
	public void initializeFrom(Element memento) throws CoreException;
	
	/**
	 * Returns the unique identifier of the extension that contributed
	 * this classpath entry type, or <code>null</code> if this classpath
	 * entry type was not contributed.
	 * 
	 * @return the unique identifier of the extension that contributed
	 *  this classpath entry type, or <code>null</code> if this classpath
	 *  entry type was not contributed
	 */
	public String getTypeId();
	
	/**
	 * Returns whether this classpath entry is composed of other entries.
	 * 
	 * @return whether this classpath entry is composed of other entries
	 */
	public boolean isComposite();
	
	/**
	 * Returns the classpath entries this entry is composed of, or an
	 * empty collection if this entry is not a composite entry.
	 * 
	 * @param configuration the context (launch configuration) in which
	 *  this runtime classpath entry is being queried for contained
	 * 	entries, possibly <code>null</code> 
	 * @return the classpath entries this entry is composed of, or an
	 * empty collection if this entry is not a composite entry
	 * @throws CoreException if unable to retrieve contained entries
	 */
	public IRuntimeLoadpathEntry[] getRuntimeLoadpathEntries(ILaunchConfiguration configuration) throws CoreException;
	
	/**
	 * Returns a human readable name for this classpath entry.
	 * 
	 * @return a human readable name for this classpath entry
	 */
	public String getName();
}

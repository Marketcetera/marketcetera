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


import org.eclipse.core.runtime.IPath;

/**
 * Determines if container entries are duplicates/redundant on a runtime
 * classpath. If an <code>IClasspathContianer</code> implements this interface,
 * the <code>isDuplicate</code> method is used to determine if containers are
 * duplicates/redundant. Otherwise, containers with the same identifier are
 * considered duplicates. 
 * 
 * @since 2.0.1
 * @deprecated support has been added to <code>ClasspathContainerInitializer</code>
 *  to handle comparison of classpath containers. Use
 *  <code>ClasspathContainerInitializer.getComparisonID(IPath,IJavaProject)</code>.
 *  When a classpath container implements this interface, this interface is
 *  used to determine equality before using the support defined in
 *  <code>ClasspathContainerInitializer</code>. 
 */
public interface IRuntimeContainerComparator {
	
	/**
	 * Returns whether this container is a duplicate of the container
	 * identified by the given path.
	 * 
	 * @param containerPath the container to compare against
	 * @return whether this container is a duplicate of the container
	 * identified by the given path
	 */
	public boolean isDuplicate(IPath containerPath);

}

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


import org.rubypeople.rdt.core.ILoadpathEntry;

/**
 * Optional enhancements to {@link IRuntimeLoadpathEntryResolver}.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 0.9.0
 */
public interface IRuntimeLoadpathEntryResolver2 extends IRuntimeLoadpathEntryResolver {
	
	/**
	 * Returns whether the given classpath entry references a VM install.
	 * 
	 * @param entry classpath entry
	 * @return whether the given classpath entry references a VM install
	 */
	public boolean isVMInstallReference(ILoadpathEntry entry);
}

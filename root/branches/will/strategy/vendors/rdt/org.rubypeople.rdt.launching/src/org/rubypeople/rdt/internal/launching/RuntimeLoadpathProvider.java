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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.IRuntimeLoadpathProvider;

/**
 * Proxy to a runtime classpath provider extension.
 */
public class RuntimeLoadpathProvider implements IRuntimeLoadpathProvider {

	private IConfigurationElement fConfigurationElement;
	
	private IRuntimeLoadpathProvider fDelegate;
	
	/**
	 * Constructs a new resolver on the given configuration element
	 */
	public RuntimeLoadpathProvider(IConfigurationElement element) {
		fConfigurationElement = element;
	}
		
	/**
	 * Returns the resolver delegate (and creates if required) 
	 */
	protected IRuntimeLoadpathProvider getProvider() throws CoreException {
		if (fDelegate == null) {
			fDelegate = (IRuntimeLoadpathProvider)fConfigurationElement.createExecutableExtension("class"); //$NON-NLS-1$
		}
		return fDelegate;
	}
	
	public String getIdentifier() {
		return fConfigurationElement.getAttribute("id"); //$NON-NLS-1$
	}
	/**
	 * @see IRuntimeLoadpathProvider#computeUnresolvedLoadpath(ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] computeUnresolvedLoadpath(ILaunchConfiguration configuration) throws CoreException {
		return getProvider().computeUnresolvedLoadpath(configuration);
	}

	/**
	 * @see IRuntimeLoadpathProvider#resolveLoadpath(IRuntimeLoadpathEntry[], ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] resolveLoadpath(IRuntimeLoadpathEntry[] entries, ILaunchConfiguration configuration) throws CoreException {
		return getProvider().resolveLoadpath(entries, configuration);
	}

}

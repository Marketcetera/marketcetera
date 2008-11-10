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
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntryResolver;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntryResolver2;
import org.rubypeople.rdt.launching.IVMInstall;

/**
 * Proxy to a runtime classpath entry resolver extension.
 */
public class RuntimeLoadpathEntryResolver implements IRuntimeLoadpathEntryResolver2 {

	private IConfigurationElement fConfigurationElement;
	
	private IRuntimeLoadpathEntryResolver fDelegate;
	
	/**
	 * Constructs a new resolver on the given configuration element
	 */
	public RuntimeLoadpathEntryResolver(IConfigurationElement element) {
		fConfigurationElement = element;
	}
	
	/**
	 * @see IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry, ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		return getResolver().resolveRuntimeLoadpathEntry(entry, configuration);
	}
	
	/**
	 * Returns the resolver delegate (and creates if required) 
	 */
	protected IRuntimeLoadpathEntryResolver getResolver() throws CoreException {
		if (fDelegate == null) {
			fDelegate = (IRuntimeLoadpathEntryResolver)fConfigurationElement.createExecutableExtension("class"); //$NON-NLS-1$
		}
		return fDelegate;
	}
	
	/**
	 * Returns the variable name this resolver is registered for, or <code>null</code>
	 */
	public String getVariableName() {
		return fConfigurationElement.getAttribute("variable"); //$NON-NLS-1$
	}
	
	/**
	 * Returns the container id this resolver is registered for, or <code>null</code>
	 */
	public String getContainerId() {
		return fConfigurationElement.getAttribute("container"); //$NON-NLS-1$
	}	
	
	/**
	 * Returns the runtime classpath entry id this resolver is registered
	 * for,or <code>null</code> if none.
	 */
	public String getRuntimeLoadpathEntryId() {
		return fConfigurationElement.getAttribute("runtimeLoadpathEntryId"); //$NON-NLS-1$
	}

	/**
	 * @see IRuntimeLoadpathEntryResolver#resolveVMInstall(ILoadpathEntry)
	 */
	public IVMInstall resolveVMInstall(ILoadpathEntry entry) throws CoreException {
		return getResolver().resolveVMInstall(entry);
	}

	/**
	 * @see IRuntimeLoadpathEntryResolver#resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry, IRubyProject)
	 */
	public IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, IRubyProject project) throws CoreException {
		return getResolver().resolveRuntimeLoadpathEntry(entry, project);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntryResolver2#isVMInstallReference(org.eclipse.jdt.core.ILoadpathEntry)
	 */
	public boolean isVMInstallReference(ILoadpathEntry entry) {
		try {
			IRuntimeLoadpathEntryResolver resolver = getResolver();
			if (resolver instanceof IRuntimeLoadpathEntryResolver2) {
				IRuntimeLoadpathEntryResolver2 resolver2 = (IRuntimeLoadpathEntryResolver2) resolver;
				return resolver2.isVMInstallReference(entry);
			} else {
				return resolver.resolveVMInstall(entry) != null;
			}
		} catch (CoreException e) {
			return false;
		}
	}

}

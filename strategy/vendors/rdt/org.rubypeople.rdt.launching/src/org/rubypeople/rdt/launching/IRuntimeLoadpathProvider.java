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

/**
 * A classpath provider computes an unresolved classpath for a launch
 * configuration, and resolves classpath entries for a launch configuration.
 * A classpath provider is defined as an extension of type 
 * <code>org.eclipse.jdt.launching.classpathProvider</code>.
 * <p>
 * A provider is registered with an identifier that can be
 * referenced by a launch configuration. A classpath provider is consulted
 * to compute a classpath or source lookup path when a launch configuration
 * references a provider in one or both of the following attributes:
 * <ul>
 * <li><code>ATTR_CLASSPATH_PROVIDER</code></li>
 * <li><code>ATTR_SOURCE_PATH_PROVIDER</code></li>
 * </ul>
 * </p>
 * A provider extension is defined in <code>plugin.xml</code>.
 * Following is an example definition of a runtime classpath provider
 * extension.
 * <pre>
 * &lt;extension point="org.eclipse.jdt.launching.classpathProviders"&gt;
 *   &lt;classpathProvider 
 *      id="com.example.ExampleLoadpathProvider"
 *      class="com.example.ExampleLoadpathProviderImpl"
 *   &lt;/classpathProvider&gt;
 * &lt;/extension&gt;
 * </pre>
 * The attributes are specified as follows:
 * <ul>
 * <li><code>id</code> specifies a unique identifier for this extension. This 
 * 	identifier may be used to reference a provider on one of the launch
 *  configuration attributes mentioned above.</li>
 * <li><code>class</code> specifies the fully qualified name of the Java class
 *   that implements <code>IRuntimeLoadpathProvider</code>.</li>
 * </ul>
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @since 0.9.0
 */
public interface IRuntimeLoadpathProvider {
	
	/**
	 * Computes and returns an unresolved classpath for the given launch configuration.
	 * Variable and container entries are not resolved.
	 * 
	 * @param configuration launch configuration
	 * @return unresolved path
	 * @exception CoreException if unable to compute a path
	 */
	public IRuntimeLoadpathEntry[] computeUnresolvedLoadpath(ILaunchConfiguration configuration) throws CoreException;
	
	/**
	 * Returns the resolved path corresponding to the given path, in the context of the
	 * given launch configuration. Variable and container entries are resolved. The returned
	 * (resolved) path need not have the same number of entries as the given (unresolved)
	 * path.
	 * 
	 * @param entries entries to resolve
	 * @param configuration launch configuration context to resolve in
	 * @return resolved path
	 * @exception CoreException if unable to resolve a path
	 */
	public IRuntimeLoadpathEntry[] resolveLoadpath(IRuntimeLoadpathEntry[] entries, ILaunchConfiguration configuration) throws CoreException;

}

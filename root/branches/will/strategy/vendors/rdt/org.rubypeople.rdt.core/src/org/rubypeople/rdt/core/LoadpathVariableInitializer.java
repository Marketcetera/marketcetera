/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.core;

/**
 * Abstract base implementation of all loadpath variable initializers.
 * Loadpath variable initializers are used in conjunction with the
 * "org.rubypeople.rdt.core.loadpathVariableInitializer" extension point.
 * <p>
 * Clients should subclass this class to implement a specific loadpath
 * variable initializer. The subclass must have a public 0-argument
 * constructor and a concrete implementation of <code>initialize</code>.
 * 
 * @see ILoadpathEntry
 * @since 0.9.1
 */
public abstract class LoadpathVariableInitializer {

    /**
     * Creates a new loadpath variable initializer.
     */
    public LoadpathVariableInitializer() {
    	// a loadpath variable initializer must have a public 0-argument constructor
    }

    /**
     * Binds a value to the workspace loadpath variable with the given name,
     * or fails silently if this cannot be done. 
     * <p>
     * A variable initializer is automatically activated whenever a variable value
     * is needed and none has been recorded so far. The implementation of
     * the initializer can set the corresponding variable using 
     * <code>RubyCore#setLoadpathVariable</code>.
     * 
     * @param variable the name of the workspace loadpath variable
     *    that requires a binding
     * 
     * @see RubyCore#getLoadpathVariable(String)
     * @see RubyCore#setLoadpathVariable(String, org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
     * @see RubyCore#setLoadpathVariables(String[], org.eclipse.core.runtime.IPath[], org.eclipse.core.runtime.IProgressMonitor)
     */
    public abstract void initialize(String variable);
}

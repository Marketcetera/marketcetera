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
package org.rubypeople.rdt.ui.dialogs;

/**
 * An interfaces to give access to the type presented in type
 * selection dialogs like the open type dialog.
 * <p>
 * Please note that <code>ITypeInfoRequestor</code> objects <strong>don't
 * </strong> have value semantic. The state of the object might change over 
 * time especially since objects are reused for different call backs. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface ITypeInfoRequestor {
	
	public boolean isModule();
	
	/**
	 * Returns the type name.
	 * 
	 * @return the info's type name.
	 */
	public String getTypeName();
	
	/**
	 * Returns the package name.
	 * 
	 * @return the info's package name.
	 */ 
	public String getPackageName();

	/**
	 * Returns a dot separated string of the enclosing types or an 
	 * empty string if the type is a top level type.
	 * 
	 * @return a dot separated string of the enclosing types
	 */
	public String getEnclosingName();
}
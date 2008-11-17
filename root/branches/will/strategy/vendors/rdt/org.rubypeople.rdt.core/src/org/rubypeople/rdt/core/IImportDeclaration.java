/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added J2SE 1.5 support
 *******************************************************************************/
package org.rubypeople.rdt.core;

/**
 * Represents an import declaration in Ruby compilation unit.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IImportDeclaration extends IRubyElement, ISourceReference {
/**
 * Returns the name that has been imported. 
 * For an on-demand import, this includes the trailing <code>".*"</code>.
 * For example, for the statement <code>"import java.util.*"</code>,
 * this returns <code>"java.util.*"</code>.
 * For the statement <code>"import java.util.Hashtable"</code>,
 * this returns <code>"java.util.Hashtable"</code>.
 * 
 * @return the name that has been imported
 */
String getElementName();

}

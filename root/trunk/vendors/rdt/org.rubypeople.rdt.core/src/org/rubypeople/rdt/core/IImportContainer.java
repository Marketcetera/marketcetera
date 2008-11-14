/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.core;

/**
 * Represents an import container is a child of a Ruby compilation unit that contains
 * all (and only) the import declarations. If a compilation unit has no import
 * declarations, no import container will be present.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IImportContainer extends IRubyElement, IParent, ISourceReference {
/**
 * Returns the first import declaration in this import container with the given name.
 * This is a handle-only method. The import declaration may or may not exist.
 * 
 * @param name the given name
 * 
 * @return the first import declaration in this import container with the given name
 */
IImportDeclaration getImport(String name);
}

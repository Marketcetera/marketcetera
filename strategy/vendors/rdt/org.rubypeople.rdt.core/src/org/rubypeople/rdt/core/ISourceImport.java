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

public interface ISourceImport {

/**
 * Answer the source end position of the import declaration.
 */

int getDeclarationSourceEnd();
/**
 * Answer the source start position of the import declaration.
 */

int getDeclarationSourceStart();

/**
 * Answer the name of the import.
 * A name is a simple name or a qualified, dot separated name.
 * For example, Hashtable or java.util.Hashtable.
 */
String getName();	
}

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
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.ISourceImport;

/**
 * Element info for IImportDeclaration elements.
 * 
 * @see org.eclipse.jdt.core.IImportDeclaration
 */
public class ImportDeclarationElementInfo extends MemberElementInfo implements ISourceImport {

	String name;

	public String getName() {
		return this.name;
	}
}

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

/**
 * Element info for IField elements.
 */

public class RubyFieldElementInfo extends MemberElementInfo {

	protected String fieldName;

	/**
	 * The type name of this field.
	 */
	protected String typeName;

	public String getName() {
		return this.fieldName;
	}

	/**
	 * Returns the type name of the field.
	 */
	public String getTypeName() {
		return this.typeName;
	}

	/**
	 * Sets the type name of the field.
	 */
	protected void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}

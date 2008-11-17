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
 *Element info for IMember elements. 
 */
 public abstract class MemberElementInfo extends SourceRefElementInfo {

	/**
	 * The start position of this member's name in the its
	 * openable's buffer.
	 */
	protected int nameStart= -1;

	/**
	 * The last position of this member's name in the its
	 * openable's buffer.
	 */
	protected int nameEnd= -1;

	/**
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceType#getNameSourceEnd()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceMethod#getNameSourceEnd()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceField#getNameSourceEnd()
	 */
	public int getNameSourceEnd() {
		return this.nameEnd;
	}
	/**
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceType#getNameSourceStart()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceMethod#getNameSourceStart()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceField#getNameSourceStart()
	 */
	public int getNameSourceStart() {
		return this.nameStart;
	}

	/**
	 * Sets the last position of this member's name, relative
	 * to its openable's source buffer.
	 */
	protected void setNameSourceEnd(int end) {
		this.nameEnd= end;
	}
	/**
	 * Sets the start position of this member's name, relative
	 * to its openable's source buffer.
	 */
	protected void setNameSourceStart(int start) {
		this.nameStart= start;
	}
}

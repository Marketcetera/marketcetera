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

import org.rubypeople.rdt.core.ISourceRange;

/**
 * Element info for ISourceReference elements.
 */
/* package */class SourceRefElementInfo extends RubyElementInfo {

	protected int fSourceRangeStart, fSourceRangeEnd;

	/**
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceType#getDeclarationSourceEnd()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceMethod#getDeclarationSourceEnd()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceField#getDeclarationSourceEnd()
	 */
	public int getDeclarationSourceEnd() {
		return fSourceRangeEnd;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceType#getDeclarationSourceStart()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceMethod#getDeclarationSourceStart()
	 * @see org.eclipse.jdt.internal.compiler.env.ISourceField#getDeclarationSourceStart()
	 */
	public int getDeclarationSourceStart() {
		return fSourceRangeStart;
	}

	protected ISourceRange getSourceRange() {
		return new SourceRange(fSourceRangeStart, fSourceRangeEnd - fSourceRangeStart + 1);
	}

	protected void setSourceRangeEnd(int end) {
		fSourceRangeEnd = end;
	}

	protected void setSourceRangeStart(int start) {
		fSourceRangeStart = start;
	}
}

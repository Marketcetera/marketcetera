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

import java.util.HashMap;

import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;

public class RubyScriptElementInfo extends OpenableElementInfo {

	/**
	 * The length of this compilation unit's source code <code>String</code>
	 */
	protected int sourceLength;

	/**
	 * Timestamp of original resource at the time this element was opened or
	 * last updated.
	 */
	protected long timestamp;

	/*
	 * The positions of annotations for each element in this compilation unit. A
	 * map from IRubyElement to long[]
	 */
	public HashMap annotationPositions;
	
	protected SyntaxException syntaxException = null ;

	public void addAnnotationPositions(IRubyElement handle, long[] positions) {
		if (positions == null) return;
		if (this.annotationPositions == null) this.annotationPositions = new HashMap();
		this.annotationPositions.put(handle, positions);
	}

	/**
	 * Returns the length of the source string.
	 */
	public int getSourceLength() {
		return this.sourceLength;
	}

	protected ISourceRange getSourceRange() {
		return new SourceRange(0, this.sourceLength);
	}

	protected boolean isOpen() {
		return true;
	}

	/**
	 * Sets the length of the source string.
	 */
	public void setSourceLength(int newSourceLength) {
		this.sourceLength = newSourceLength;
	}

	public SyntaxException getSyntaxException() {
		return syntaxException;
	}

	public void setSyntaxException(SyntaxException syntaxException) {
		this.syntaxException = syntaxException;
	}
	

}

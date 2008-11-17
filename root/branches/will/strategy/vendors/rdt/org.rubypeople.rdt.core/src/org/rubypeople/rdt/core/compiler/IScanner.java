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

package org.rubypeople.rdt.core.compiler;

 
 /**
  * Definition of a Java scanner, as returned by the <code>ToolFactory</code>.
  * The scanner is responsible for tokenizing a given source, providing information about
  * the nature of the token read, its positions and source equivalent.
  * <p>
  * When the scanner has finished tokenizing, it answers an EOF token (<code>
  * ITerminalSymbols#TokenNameEOF</code>.
  * </p><p>
  * When encountering lexical errors, an <code>InvalidInputException</code> is thrown.
 * </p><p>
 * This interface is not intended to be implemented by clients.
 * </p>
  * 
  * @see org.eclipse.jdt.core.ToolFactory
  * @see ITerminalSymbols
  * @since 2.0
  */
public interface IScanner {

	int TokenNameEOF = -1;

	/**
	 * Answers the starting position of the current token inside the original source.
	 * This position is zero-based and inclusive. It corresponds to the position of the first character 
	 * which is part of this token. If this character was a unicode escape sequence, it points at the first 
	 * character of this sequence.
	 * 
	 * @return the starting position of the current token inside the original source
	 */
	int getCurrentTokenStartPosition();

	/**
	 * Answers the ending position of the current token inside the original source.
	 * This position is zero-based and inclusive. It corresponds to the position of the last character
	 * which is part of this token. If this character was a unicode escape sequence, it points at the last 
	 * character of this sequence.
	 * 
	 * @return the ending position of the current token inside the original source
	 */
	int getCurrentTokenEndPosition();

	/**
	 * Read the next token in the source, and answers its ID as specified by <code>ITerminalSymbols</code>.
	 * Note that the actual token ID values are subject to change if new keywords were added to the language
	 * (for instance, 'assert' is a keyword in 1.4).
	 * 
	 * @throws InvalidInputException in case a lexical error was detected while reading the current token
	 * @return the next token
	 */
	int getNextToken() throws InvalidInputException;

	/**
	 * Set the scanner source to process. By default, the scanner will consider starting at the beginning of the
	 * source until it reaches its end.
	 * 
	 * @param source the given source
	 */
	void setSource(char[] source);
}

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
package org.rubypeople.rdt.ui.text.ruby;

import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyScript;

/**
 * Context information for quick fix and quick assist processors.
 * <p>
 * Note: this interface is not intended to be implemented.
 * </p>
 *
 * @since 1.0.0
 */
public interface IInvocationContext {

	/**
	 * @return Returns the current ruby script.
	 */
	IRubyScript getRubyScript();

	/**
	 * @return Returns the offset of the current selection
	 */
	int getSelectionOffset();

	/**
	 * @return Returns the length of the current selection
	 */
	int getSelectionLength();

	/**
	 * Returns an AST of the compilation unit, possibly only a partial AST focused on the selection
	 * offset (see {@link org.eclipse.jdt.core.dom.ASTParser#setFocalPosition(int)}).
	 * The returned AST is shared and therefore protected and cannot be modified.
	 * The client must check the AST API level and do nothing if they are given an AST
	 * they can't handle. (see {@link org.eclipse.jdt.core.dom.AST#apiLevel()}).
	 * @return Returns the root of the AST corresponding to the current compilation unit.
	 */
	RootNode getASTRoot();

	/**
	 * Convenience method to evaluate the AST node covering the current selection.
	 * @return Returns the node that covers the location of the problem
	 */
	Node getCoveringNode();

	/**
	 * Convenience method to evaluate the AST node that is covered by the current selection.
	 * @return Returns the node that is covered by the location of the problem
	 */
	Node getCoveredNode();

}

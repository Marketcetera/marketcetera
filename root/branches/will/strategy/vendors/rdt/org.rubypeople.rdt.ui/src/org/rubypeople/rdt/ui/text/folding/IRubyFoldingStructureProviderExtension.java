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
package org.rubypeople.rdt.ui.text.folding;

import org.rubypeople.rdt.core.IRubyElement;


/**
 * Extends {@link IRubyFoldingStructureProvider} with the following
 * functions:
 * <ul>
 * <li>collapsing of comments and members</li>
 * <li>expanding and collapsing of certain ruby elements</li>
 * </ul>
 * 
 * @since 0.9.0
 */
public interface IRubyFoldingStructureProviderExtension {
	/**
	 * Collapses all members except for top level types.
	 */
	void collapseMembers();
	/**
	 * Collapses all comments.
	 */
	void collapseComments();
	/**
	 * Collapses the given elements.
	 * 
	 * @param elements the ruby elements to collapse (the array and its elements will not be
	 *        modified)
	 */
	void collapseElements(IRubyElement[] elements);
	/**
	 * Expands the given elements.
	 * 
	 * @param elements the ruby elements to expand (the array and its elements will not be modified)
	 */
	void expandElements(IRubyElement[] elements);
}

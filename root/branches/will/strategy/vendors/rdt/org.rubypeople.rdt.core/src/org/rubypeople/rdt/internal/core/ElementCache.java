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

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.buffer.LRUCache;
import org.rubypeople.rdt.internal.core.buffer.OverflowingLRUCache;

/**
 * An LRU cache of <code>RubyElements</code>.
 */
public class ElementCache extends OverflowingLRUCache {

	IRubyElement spaceLimitParent = null;
	
	/**
	 * Constructs a new element cache of the given size.
	 */
	public ElementCache(int size) {
		super(size);
	}

	/**
	 * Constructs a new element cache of the given size.
	 */
	public ElementCache(int size, int overflow) {
		super(size, overflow);
	}
	
	/*
	 * Ensures that there is enough room for adding the given number of children.
	 * If the space limit must be increased, record the parent that needed this space limit.
	 */
	protected void ensureSpaceLimit(int childrenSize, IRubyElement parent) {
		// ensure the children can be put without closing other elements
		int spaceNeeded = 1 + (int)((1 + fLoadFactor) * (childrenSize + fOverflow));
		if (fSpaceLimit < spaceNeeded) {
			// parent is being opened with more children than the space limit
			shrink(); // remove overflow
			setSpaceLimit(spaceNeeded); 
			this.spaceLimitParent = parent;
		}
	}
	
	/*
	 * If the given parent was the one that increased the space limit, reset
	 * the space limit to the given default value.
	 */
	protected void resetSpaceLimit(int defaultLimit, IRubyElement parent) {
		if (parent.equals(this.spaceLimitParent)) {
			setSpaceLimit(defaultLimit);
			this.spaceLimitParent = null;
		}
	}

	/**
	 * Returns true if the element is successfully closed and removed from the
	 * cache, otherwise false.
	 * 
	 * <p>
	 * NOTE: this triggers an external removal of this element by closing the
	 * element.
	 */
	protected boolean close(LRUCacheEntry entry) {
		Openable element = (Openable) entry._fKey;
		try {
			if (!element.canBeRemovedFromCache()) {
				return false;
			}
			element.close();
			return true;
		} catch (RubyModelException npe) {
			return false;
		}
	}

	/**
	 * Returns a new instance of the reciever.
	 */
	protected LRUCache newInstance(int size, int overflow) {
		return new ElementCache(size, overflow);
	}
}

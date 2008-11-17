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
package org.rubypeople.rdt.core.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * Collects the results returned by a <code>SearchEngine</code>.
 */
public class CollectingSearchRequestor extends SearchRequestor {
	private ArrayList<SearchMatch> fFound;

	public CollectingSearchRequestor() {
		fFound = new ArrayList<SearchMatch>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		fFound.add(match);
	}

	/**
	 * @return a List of {@link SearchMatch}es (not sorted)
	 */
	public List<SearchMatch> getResults() {
		return fFound;
	}
}

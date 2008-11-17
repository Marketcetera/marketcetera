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
package org.rubypeople.rdt.internal.ui.search;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class RubySearchContentProvider implements IStructuredContentProvider {
	protected final Object[] EMPTY_ARR= new Object[0];
	protected RubySearchResult fResult;
	private RubySearchResultPage fPage;

	RubySearchContentProvider(RubySearchResultPage page) {
		fPage= page;
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		initialize((RubySearchResult) newInput);
	}
	
	protected void initialize(RubySearchResult result) {
		fResult= result;
	}
	
	public abstract void elementsChanged(Object[] updatedElements);
	public abstract void clear();

	public void filtersChanged(MatchFilter[] filters) {
	}
	
	
	public void dispose() {
		// nothing to do
	}

	RubySearchResultPage getPage() {
		return fPage;
	}

}

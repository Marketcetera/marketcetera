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

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.search.FieldReferenceMatch;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchRequestor;

public class NewSearchResultCollector extends SearchRequestor {
	private RubySearchResult fSearch;
	private boolean fIgnorePotentials;

	public NewSearchResultCollector(RubySearchResult search, boolean ignorePotentials) {
		super();
		fSearch= search;
		fIgnorePotentials= ignorePotentials;
	}
	
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		IRubyElement enclosingElement= (IRubyElement) match.getElement();
		if (enclosingElement != null) {
			if (fIgnorePotentials && (match.getAccuracy() == SearchMatch.A_INACCURATE))
				return;
			boolean isWriteAccess= false;
			boolean isReadAccess= false;
			if (match instanceof FieldReferenceMatch) {
				FieldReferenceMatch fieldRef= ((FieldReferenceMatch)match);
				isWriteAccess= fieldRef.isWriteAccess();
				isReadAccess= fieldRef.isReadAccess();
			}
			fSearch.addMatch(new RubyElementMatch(enclosingElement, match.getRule(), match.getOffset(), match.getLength(), match.getAccuracy(), isReadAccess, isWriteAccess, match.isInsideDocComment()));
		}
	}

	public void beginReporting() {
	}

	public void endReporting() {
	}

	public void enterParticipant(SearchParticipant participant) {
	}

	public void exitParticipant(SearchParticipant participant) {
	}


}

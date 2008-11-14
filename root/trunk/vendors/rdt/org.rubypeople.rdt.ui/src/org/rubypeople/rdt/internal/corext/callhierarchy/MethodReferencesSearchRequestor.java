/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 *          (report 36180: Callers/Callees view)
 *******************************************************************************/
package org.rubypeople.rdt.internal.corext.callhierarchy;

import java.util.Map;

import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchRequestor;

class MethodReferencesSearchRequestor extends SearchRequestor {
    private CallSearchResultCollector fSearchResults;
    private boolean fRequireExactMatch = false;
//  FIXME I turned off the check for exact match, because our SearchEngine doesn't yet properly classify accuracy!
    
    MethodReferencesSearchRequestor() {
        fSearchResults = new CallSearchResultCollector();
    }

    public Map getCallers() {
        return fSearchResults.getCallers();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
     */
    public void acceptSearchMatch(SearchMatch match) {
        if (fRequireExactMatch && (match.getAccuracy() != SearchMatch.A_ACCURATE)) { 
            return;
        }
        
        if (match.isInsideDocComment()) {
            return;
        }

        if (match.getElement() != null && match.getElement() instanceof IMember) {
            IMember member= (IMember) match.getElement();
            switch (member.getElementType()) {
                case IRubyElement.METHOD:
                case IRubyElement.TYPE:
                case IRubyElement.FIELD:
                    fSearchResults.addMember(member, member, match.getOffset(), match.getOffset()+match.getLength());
                    break;
            }
        }
    }
}

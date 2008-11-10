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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.corext.util.SearchUtils;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

class CallerMethodWrapper extends MethodWrapper {
    public CallerMethodWrapper(MethodWrapper parent, MethodCall methodCall) {
        super(parent, methodCall);
    }

    protected IRubySearchScope getSearchScope() {
        return CallHierarchy.getDefault().getSearchScope();
    }

    protected String getTaskName() {
        return CallHierarchyMessages.CallerMethodWrapper_taskname; 
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#createMethodWrapper(org.eclipse.jdt.internal.corext.callhierarchy.MethodCall)
	 */
	protected MethodWrapper createMethodWrapper(MethodCall methodCall) {
        return new CallerMethodWrapper(this, methodCall);
    }

	/**
	 * @return The result of the search for children
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper#findChildren(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected Map findChildren(IProgressMonitor progressMonitor) {
		try {
			MethodReferencesSearchRequestor searchRequestor= new MethodReferencesSearchRequestor();
			SearchEngine searchEngine= new SearchEngine();

			IProgressMonitor monitor= new SubProgressMonitor(progressMonitor, 95, SubProgressMonitor.SUPPRESS_SUBTASK_LABEL);
			IRubySearchScope defaultSearchScope= getSearchScope();
			boolean isWorkspaceScope= SearchEngine.createWorkspaceScope().equals(defaultSearchScope);

			for (Iterator iter= getMembers().iterator(); iter.hasNext();) {
				checkCanceled(progressMonitor);

				IMember member= (IMember) iter.next();
				SearchPattern pattern= SearchPattern.createPattern(member, IRubySearchConstants.REFERENCES, SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE);
				IRubySearchScope searchScope= isWorkspaceScope ? getAccurateSearchScope(defaultSearchScope, member) : defaultSearchScope;
				searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, searchScope, searchRequestor,
						monitor);
			}
			return searchRequestor.getCallers();
			
		} catch (CoreException e) {
			RubyPlugin.log(e);
			return new HashMap(0);
		}
	}

	private IRubySearchScope getAccurateSearchScope(IRubySearchScope defaultSearchScope, IMember member) throws RubyModelException {
		if (!(member.isType(IRubyElement.METHOD) && (((IMethod)member).isPrivate())))
			return defaultSearchScope;
		
		if (member.getRubyScript() != null) {
			return SearchEngine.createRubySearchScope(new IRubyElement[] { member.getRubyScript() });
		} else {
			return defaultSearchScope;
		}
	}

    /**
     * Returns a collection of IMember instances representing what to search for 
     */
    private Collection getMembers() {
        Collection result = new ArrayList();

        result.add(getMember());

        return result;
    }
}

package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.search.CollectingSearchRequestor;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;

public class RubyElementRequestor {

	private IRubyScript script;

	public RubyElementRequestor(IRubyScript script) {
		this.script = script;
	}

	public IType[] findType(String fullyQualifiedName) {
		List<IType> types = new ArrayList<IType>();
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, fullyQualifiedName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		SearchParticipant[] participants = new SearchParticipant[] { BasicSearchEngine.getDefaultSearchParticipant() };
		IRubySearchScope scope = BasicSearchEngine.createRubySearchScope( new IRubyElement[] { script.getRubyProject() } );
		CollectingSearchRequestor requestor = new CollectingSearchRequestor();
		try {
			new BasicSearchEngine().search(pattern, participants, scope, requestor, null);
		} catch (CoreException e) {
			RubyCore.log(e);
		} // TODO check the result locations and prefer those that are imported by this script.
		List<SearchMatch> matches = requestor.getResults();
		for (SearchMatch match : matches) {
			IType type = (IType) match.getElement();
			if (type == null) continue;
			if (!type.getFullyQualifiedName().equals(fullyQualifiedName)) continue;
			types.add(type);
		}
		if (types.isEmpty()) {
			// retry without filtering out exact fully qualified matches?!
			for (SearchMatch match : matches) {
				IType type = (IType) match.getElement();
				if (type == null) continue;
				types.add(type);
			}
		}
		
		return types.toArray(new IType[types.size()]);		
	}
}

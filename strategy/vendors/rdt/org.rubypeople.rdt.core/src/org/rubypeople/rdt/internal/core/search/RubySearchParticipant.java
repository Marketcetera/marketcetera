package org.rubypeople.rdt.internal.core.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.core.search.SearchRequestor;
import org.rubypeople.rdt.internal.core.search.indexing.SourceIndexer;
import org.rubypeople.rdt.internal.core.search.matching.MatchLocator;
import org.rubypeople.rdt.internal.core.util.Util;

public class RubySearchParticipant extends SearchParticipant {

	private IndexSelector indexSelector;

	@Override
	public SearchDocument getDocument(String documentPath) {
		if (Util.isERBLikeFileName(new Path(documentPath).lastSegment())) {
			return new ERBSearchDocument(documentPath, this);
		}
		return new RubySearchDocument(documentPath, this);
	}

	@Override
	public void indexDocument(SearchDocument document, IPath indexLocation) {
		document.removeAllIndexEntries(); // in case the document was already indexed
		// FIXME We can cheat and use indexLocation as the source folder root path, and determine the "src folder" names from the diff between it and documentPath!
		String documentPath = document.getPath();
		if (org.rubypeople.rdt.internal.core.util.Util.isRubyOrERBLikeFileName(documentPath)) {
			new SourceIndexer(document).indexDocument();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#selectIndexes(org.eclipse.jdt.core.search.SearchQuery, org.eclipse.jdt.core.search.SearchContext)
	 */
	public IPath[] selectIndexes(
		SearchPattern pattern,
		IRubySearchScope scope) {
		
		if (this.indexSelector == null) {
			this.indexSelector = new IndexSelector(scope, pattern);
		}
		return this.indexSelector.getIndexLocations();
	}
	
	/* (non-Javadoc)
	 * @see SearchParticipant#locateMatches(SearchDocument[], SearchPattern, IJavaSearchScope, SearchRequestor, IProgressMonitor)
	 */
	public void locateMatches(SearchDocument[] indexMatches, SearchPattern pattern,
			IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		
		MatchLocator matchLocator = 
			new MatchLocator(
				pattern, 
				requestor, 
				scope,
				monitor == null ? null : new SubProgressMonitor(monitor, 95)
		);

		/* eliminating false matches and locating them */
		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
		matchLocator.locateMatches(indexMatches);
//		
//
//		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
//		
//		matchLocator.locatePackageDeclarations(this);
	}

}

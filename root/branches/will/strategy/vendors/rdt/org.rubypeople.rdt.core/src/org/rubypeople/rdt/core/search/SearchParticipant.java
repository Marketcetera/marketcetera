package org.rubypeople.rdt.core.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class SearchParticipant {
	
	/**
	 * Indexes the given document in the given index. A search participant
	 * asked to index a document should parse it and call 
	 * {@link SearchDocument#addIndexEntry(char[], char[])} as many times as
	 * needed to add index entries to the index. If delegating to another
	 * participant, it should use the original index location (and not the
	 * delegatee's one). In the particular case of delegating to the default
	 * search participant (see {@link SearchEngine#getDefaultSearchParticipant()}),
	 * the provided document's path must be a path ending with one of the 
	 * {@link org.eclipse.jdt.core.JavaCore#getJavaLikeExtensions() Java-like extensions} 
	 * or with '.class'.
	 * <p>
	 * The given index location must represent a path in the file system to a file that
	 * either already exists or is going to be created. If it exists, it must be an index file,
	 * otherwise its data might be overwritten.
	 * </p><p>
	 * Clients are not expected to call this method.
	 * </p>
	 * 
	 * @param document the document to index
	 * @param indexLocation the location in the file system to the index
	 */
	public abstract void indexDocument(SearchDocument document, IPath indexLocation);
	
	/**
	 * Returns a search document for the given path.
	 * The given document path is a string that uniquely identifies the document.
	 * Most of the time it is a workspace-relative path, but it can also be a file system path, or a path inside a zip file.
	 * <p>
	 * Implementors of this method can either create an instance of their own subclass of 
	 * {@link SearchDocument} or return an existing instance of such a subclass.
	 * </p>
	 * 
	 * @param documentPath the path of the document.
	 * @return a search document
	 */
	public abstract SearchDocument getDocument(String documentPath);

	/**
	 * Returns the collection of index locations to consider when performing the
	 * given search query in the given scope. The search engine calls this
	 * method before locating matches.
	 * <p>
	 * An index location represents a path in the file system to a file that holds index information. 
	 * </p><p>
	 * Clients are not expected to call this method.
	 * </p>
	 * 
	 * @param query the search pattern to consider
	 * @param scope the given search scope
	 * @return the collection of index paths to consider
	 */
	public abstract IPath[] selectIndexes(SearchPattern pattern, IRubySearchScope scope);

	/**
	 * Locates the matches in the given documents using the given search pattern
	 * and search scope, and reports them to the givenn search requestor. This
	 * method is called by the search engine once it has search documents
	 * matching the given pattern in the given search scope.
	 * <p>
	 * Note that a participant (e.g. a JSP participant) can pre-process the contents of the given documents, 
	 * create its own documents whose contents are Java compilation units and delegate the match location 
	 * to the default participant (see {@link SearchEngine#getDefaultSearchParticipant()}). Passing its own
	 * {@link SearchRequestor} this particpant can then map the match positions back to the original
	 * contents, create its own matches and report them to the original requestor.
	 * </p><p>
	 * Implementors of this method should check the progress monitor
	 * for cancelation when it is safe and appropriate to do so.  The cancelation
	 * request should be propagated to the caller by throwing 
	 * <code>OperationCanceledException</code>.
	 * </p>
	 * 
	 * @param documents the documents to locate matches in
	 * @param pattern the search pattern to use when locating matches
	 * @param scope the scope to limit the search to
	 * @param requestor the requestor to report matches to
	 * @param monitor the progress monitor to report progress to,
	 * or <code>null</code> if no progress should be reported
	 * @throws CoreException if the requestor had problem accepting one of the matches
	 */
	public abstract void locateMatches(SearchDocument[] documents, SearchPattern pattern, IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException;

	
	/**
	 * Notification that this participant's help is needed in a search.
	 * <p>
	 * This method should be re-implemented in subclasses that need to do something
	 * when the participant is needed in a search.
	 * </p>
	 */
	public void beginSearching() {
		// do nothing
	}
	
	/**
	 * Notification that this participant's help is no longer needed.
	 * <p>
	 * This method should be re-implemented in subclasses that need to do something
	 * when the participant is no longer needed in a search.
	 * </p>
	 */
	public void doneSearching() {
		// do nothing
	}

	/**
	 * Returns a displayable name of this search participant.
	 * <p>
	 * This method should be re-implemented in subclasses that need to 
	 * display a meaningfull name.
	 * </p>
	 * 
	 * @return the displayable name of this search participant
	 */
	public String getDescription() {
		return "Search participant"; //$NON-NLS-1$
	}

}

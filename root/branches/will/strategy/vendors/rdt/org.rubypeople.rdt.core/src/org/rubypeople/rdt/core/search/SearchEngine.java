package org.rubypeople.rdt.core.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;

public class SearchEngine {

	private BasicSearchEngine basicEngine;

	/**
	 * Creates a new search engine.
	 */
	public SearchEngine() {
		this.basicEngine = new BasicSearchEngine();
	}
	
	/**
	 * Creates a new search engine with the given working copy owner.
	 * The working copies owned by this owner will take precedence over 
	 * the primary compilation units in the subsequent search operations.
	 * 
	 * @param workingCopyOwner the owner of the working copies that take precedence over their original compilation units
	 * @since 1.0
	 */
	public SearchEngine(WorkingCopyOwner workingCopyOwner) {
		this.basicEngine = new BasicSearchEngine(workingCopyOwner);
	}

	public static IRubySearchScope createWorkspaceScope() {
		return BasicSearchEngine.createWorkspaceScope();
	}
	
	/**
	 * Searches for all top-level types and member types in the given scope.
	 * The search can be selecting specific types (given a package or a type name
	 * prefix and match modes). 
	 * 
	 * @param namespace the qualification/namespace for the types
	 * @param typeName the dot-separated qualified name of the searched type (the qualification include
	 *					the enclosing types if the searched type is a member type), or a prefix
	 *					for this type, or a wild-carded string for this type.
	 * @param matchRule one of
	 * <ul>
	 *		<li>{@link SearchPattern#R_EXACT_MATCH} if the package name and type name are the full names
	 *			of the searched types.</li>
	 *		<li>{@link SearchPattern#R_PREFIX_MATCH} if the package name and type name are prefixes of the names
	 *			of the searched types.</li>
	 *		<li>{@link SearchPattern#R_PATTERN_MATCH} if the package name and type name contain wild-cards.</li>
	 *		<li>{@link SearchPattern#R_CAMELCASE_MATCH} if type name are camel case of the names of the searched types.</li>
	 * </ul>
	 * combined with {@link SearchPattern#R_CASE_SENSITIVE},
	 *   e.g. {@link SearchPattern#R_EXACT_MATCH} | {@link SearchPattern#R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *   or {@link SearchPattern#R_PREFIX_MATCH} if a prefix non case sensitive match is requested.
	 * @param searchFor determines the nature of the searched elements
	 *	<ul>
	 * 	    <li>{@link IRubySearchConstants#CLASS}: only look for classes</li>
	 *		<li>{@link IRubySearchConstants#MODULE}: only look for modules</li>
	 * 	    <li>{@link IRubySearchConstants#TYPE}: look for all types</li>
	 *	</ul>
	 * @param scope the scope to search in
	 * @param nameRequestor the requestor that collects the results of the search
	 * @param waitingPolicy one of
	 * <ul>
	 *		<li>{@link IRubySearchConstants#FORCE_IMMEDIATE_SEARCH} if the search should start immediately</li>
	 *		<li>{@link IRubySearchConstants#CANCEL_IF_NOT_READY_TO_SEARCH} if the search should be cancelled if the
	 *			underlying indexer has not finished indexing the workspace</li>
	 *		<li>{@link IRubySearchConstants#WAIT_UNTIL_READY_TO_SEARCH} if the search should wait for the
	 *			underlying indexer to finish indexing the workspace</li>
	 * </ul>
	 * @param progressMonitor the progress monitor to report progress to, or <code>null</code> if no progress
	 *							monitor is provided
	 * @exception RubyModelException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 * @since 1.0
	 */
	public void searchAllTypeNames(
		final char[] namespace, 
		final char[] typeName,
		final int matchRule, 
		int searchFor, 
		IRubySearchScope scope, 
		final TypeNameRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws RubyModelException {
		
		this.basicEngine.searchAllTypeNames(namespace, typeName, matchRule, searchFor, scope, nameRequestor, waitingPolicy, progressMonitor);
	}

	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements) {
		return BasicSearchEngine.createRubySearchScope(elements);
	}

	/**
	 * Returns a Ruby search scope limited to the given Ruby elements.
	 * The Ruby elements resulting from a search with this scope will
	 * be children of the given elements.
	 * 
	 * If an element is an IRubyProject, then it includes:
	 * - its source folders if IRubySearchScope.SOURCES is specified, 
	 * - its application libraries (internal and external jars, class folders that are on the raw classpath, 
	 *   or the ones that are coming from a classpath path variable,
	 *   or the ones that are coming from a classpath container with the K_APPLICATION kind)
	 *   if IRubySearchScope.APPLICATION_LIBRARIES is specified
	 * - its system libraries (internal and external jars, class folders that are coming from an 
	 *   ILoadpathContainer with the K_SYSTEM kind) 
	 *   if IRubySearchScope.APPLICATION_LIBRARIES is specified
	 * - its referenced projects (with their source folders and jars, recursively) 
	 *   if IRubySearchScope.REFERENCED_PROJECTS is specified.
	 * If an element is an ISourceFolderRoot, then only the package fragments of 
	 * this package fragment root will be included.
	 * If an element is an ISourceFolder, then only the compilation unit and class 
	 * files of this package fragment will be included. Subpackages will NOT be 
	 * included.
	 *
	 * @param elements the Ruby elements the scope is limited to
	 * @param includeMask the bit-wise OR of all include types of interest
	 * @return a new Ruby search scope
	 * @see IRubySearchScope#SOURCES
	 * @see IRubySearchScope#APPLICATION_LIBRARIES
	 * @see IRubySearchScope#SYSTEM_LIBRARIES
	 * @see IRubySearchScope#REFERENCED_PROJECTS
	 * @since 1.0
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements, int includeMask) {
		return BasicSearchEngine.createRubySearchScope(elements, includeMask);
	}

	public static SearchParticipant getDefaultSearchParticipant() {
		return BasicSearchEngine.getDefaultSearchParticipant();
	}

	/**
	 * Searches for matches of a given search pattern. Search patterns can be created using helper
	 * methods (from a String pattern or a Ruby element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @param pattern the pattern to search
	 * @param participants the particpants in the search
	 * @param scope the search scope
	 * @param requestor the requestor to report the matches to
	 * @param monitor the progress monitor used to report progress
	 * @exception CoreException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 *@since 1.0
	 */
	public void search(SearchPattern pattern, SearchParticipant[] participants, IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		this.basicEngine.search(pattern, participants, scope, requestor, monitor);
	}

	/**
	 * Returns a Ruby search scope limited to the hierarchy of the given type.
	 * The Ruby elements resulting from a search with this scope will
	 * be types in this hierarchy, or members of the types in this hierarchy.
	 *
	 * @param type the focus of the hierarchy scope
	 * @return a new hierarchy scope
	 * @exception RubyModelException if the hierarchy could not be computed on the given type
	 */
	public static IRubySearchScope createHierarchyScope(IType type) throws RubyModelException {
		return BasicSearchEngine.createHierarchyScope(type);
	}

}

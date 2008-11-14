package org.rubypeople.rdt.internal.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.core.search.SearchRequestor;
import org.rubypeople.rdt.core.search.TypeNameRequestor;
import org.rubypeople.rdt.internal.core.DefaultWorkingCopyOwner;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;
import org.rubypeople.rdt.internal.core.search.matching.MatchLocator;
import org.rubypeople.rdt.internal.core.search.matching.RubySearchPattern;
import org.rubypeople.rdt.internal.core.search.matching.TypeDeclarationPattern;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class BasicSearchEngine {
	
	// Type decl kinds
	public static final int CLASS_DECL = 1;
	public static final int MODULE_DECL = 2;
	
	public static final boolean VERBOSE = false;
	
	/*
	 * A list of working copies that take precedence over their original 
	 * compilation units.
	 */
	private IRubyScript[] workingCopies;
	
	/*
	 * A working copy owner whose working copies will take precedent over 
	 * their original compilation units.
	 */
	private WorkingCopyOwner workingCopyOwner;

	
	/*
	 * Creates a new search basic engine.
	 */
	public BasicSearchEngine() {
		// will use working copies of PRIMARY owner
	}
	
	/**
	 * @see SearchEngine#SearchEngine(WorkingCopyOwner) for detailed comment.
	 */
	public BasicSearchEngine(WorkingCopyOwner workingCopyOwner) {
		this.workingCopyOwner = workingCopyOwner;
	}

	/**
	 * Searches for matches of a given search pattern. Search patterns can be created using helper
	 * methods (from a String pattern or a Ruby element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @see SearchEngine#search(SearchPattern, SearchParticipant[], IRubySearchScope, SearchRequestor, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void search(SearchPattern pattern, SearchParticipant[] participants, IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.search(SearchPattern, SearchParticipant[], IRubySearchScope, SearchRequestor, IProgressMonitor)"); //$NON-NLS-1$
		}
		findMatches(pattern, participants, scope, requestor, monitor);
	}
	
	/**
	 * @see SearchEngine#createRubySearchScope(IRubyElement[]) for detailed comment.
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements) {
		return createRubySearchScope(elements, true);
	}

	/**
	 * @see SearchEngine#createRubySearchScope(IRubyElement[], boolean) for detailed comment.
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements, boolean includeReferencedProjects) {
		int includeMask = IRubySearchScope.SOURCES | IRubySearchScope.APPLICATION_LIBRARIES | IRubySearchScope.SYSTEM_LIBRARIES;
		if (includeReferencedProjects) {
			includeMask |= IRubySearchScope.REFERENCED_PROJECTS;
		}
		return createRubySearchScope(elements, includeMask);
	}

	/**
	 * @see SearchEngine#createRubySearchScope(IRubyElement[], int) for detailed comment.
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements, int includeMask) {
		RubySearchScope scope = new RubySearchScope();
		HashSet visitedProjects = new HashSet(2);
		for (int i = 0, length = elements.length; i < length; i++) {
			IRubyElement element = elements[i];
			if (element != null) {
				try {
					if (element instanceof RubyProject) {
						scope.add((RubyProject)element, includeMask, visitedProjects);
					} else {
						scope.add(element);
					}
				} catch (RubyModelException e) {
					// ignore
				}
			}
		}
		return scope;
	}
	
	/**
	 * Searches for matches to a given query. Search queries can be created using helper
	 * methods (from a String pattern or a Ruby element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @param scope the search result has to be limited to the given scope
	 * @param requestor a callback object to which each match is reported
	 */
	void findMatches(SearchPattern pattern, SearchParticipant[] participants, IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
		try {
			/* initialize progress monitor */
			if (monitor != null)
				monitor.beginTask(Messages.engine_searching, 100); 
			if (VERBOSE) {
				Util.verbose("Searching for pattern: " + pattern.toString()); //$NON-NLS-1$
				Util.verbose(scope.toString());
			}
			if (participants == null) {
				if (VERBOSE) Util.verbose("No participants => do nothing!"); //$NON-NLS-1$
				return;
			}
	
			IndexManager indexManager = RubyModelManager.getRubyModelManager().getIndexManager();
			requestor.beginReporting();
			for (int i = 0, l = participants.length; i < l; i++) {
				if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
	
				SearchParticipant participant = participants[i];
				SubProgressMonitor subMonitor= monitor==null ? null : new SubProgressMonitor(monitor, 1000);
				if (subMonitor != null) subMonitor.beginTask("", 1000); //$NON-NLS-1$
				try {
					if (subMonitor != null) subMonitor.subTask(Messages.bind(Messages.engine_searching_indexing, new String[] {participant.getDescription()})); 
					participant.beginSearching();
					requestor.enterParticipant(participant);
					PathCollector pathCollector = new PathCollector();
					indexManager.performConcurrentJob(
						new PatternSearchJob(pattern, participant, scope, pathCollector),
						IRubySearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
						subMonitor);
					if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
	
					// locate index matches if any (note that all search matches could have been issued during index querying)
					if (subMonitor != null) subMonitor.subTask(Messages.bind(Messages.engine_searching_matching, new String[] {participant.getDescription()})); 
					String[] indexMatchPaths = pathCollector.getPaths();
					if (indexMatchPaths != null) {
						pathCollector = null; // release
						int indexMatchLength = indexMatchPaths.length;
						SearchDocument[] indexMatches = new SearchDocument[indexMatchLength];
						for (int j = 0; j < indexMatchLength; j++) {
							indexMatches[j] = participant.getDocument(indexMatchPaths[j]);
						}
						SearchDocument[] matches = MatchLocator.addWorkingCopies(pattern, indexMatches, getWorkingCopies(), participant);
						participant.locateMatches(matches, pattern, scope, requestor, subMonitor);
					}
				} finally {		
					requestor.exitParticipant(participant);
					participant.doneSearching();
				}
			}
		} finally {
			requestor.endReporting();
			if (monitor != null)
				monitor.done();
		}
	}
	
	/*
	 * Returns the list of working copies used by this search engine.
	 * Returns null if none.
	 */
	private IRubyScript[] getWorkingCopies() {
		IRubyScript[] copies;
		if (this.workingCopies != null) {
			if (this.workingCopyOwner == null) {
				copies = RubyModelManager.getRubyModelManager().getWorkingCopies(DefaultWorkingCopyOwner.PRIMARY, false/*don't add primary WCs a second time*/);
				if (copies == null) {
					copies = this.workingCopies;
				} else {
					HashMap pathToCUs = new HashMap();
					for (int i = 0, length = copies.length; i < length; i++) {
						IRubyScript unit = copies[i];
						pathToCUs.put(unit.getPath(), unit);
					}
					for (int i = 0, length = this.workingCopies.length; i < length; i++) {
						IRubyScript unit = this.workingCopies[i];
						pathToCUs.put(unit.getPath(), unit);
					}
					int length = pathToCUs.size();
					copies = new IRubyScript[length];
					pathToCUs.values().toArray(copies);
				}
			} else {
				copies = this.workingCopies;
			}
		} else if (this.workingCopyOwner != null) {
			copies = RubyModelManager.getRubyModelManager().getWorkingCopies(this.workingCopyOwner, true/*add primary WCs*/);
		} else {
			copies = RubyModelManager.getRubyModelManager().getWorkingCopies(DefaultWorkingCopyOwner.PRIMARY, false/*don't add primary WCs a second time*/);
		}
		if (copies == null) return null;
		
		// filter out primary working copies that are saved
		IRubyScript[] result = null;
		int length = copies.length;
		int index = 0;
		for (int i = 0; i < length; i++) {
			RubyScript copy = (RubyScript)copies[i];
			try {
				if (!copy.isPrimary()
						|| copy.hasUnsavedChanges()
						|| copy.hasResourceChanged()) {
					if (result == null) {
						result = new IRubyScript[length];
					}
					result[index++] = copy;
				}
			}  catch (RubyModelException e) {
				// copy doesn't exist: ignore
			}
		}
		if (index != length && result != null) {
			System.arraycopy(result, 0, result = new IRubyScript[index], 0, index);
		}
		return result;
	}
	
	
	public static SearchParticipant getDefaultSearchParticipant() {
		return new RubySearchParticipant();
	}

	public static IRubySearchScope createWorkspaceScope() {
		return RubyModelManager.getRubyModelManager().getWorkspaceScope();
	}

	public static Collection<IType> findType(String simpleTypeName) {
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, "*" + simpleTypeName + "*", IRubySearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);
		SearchParticipant[] participants = new SearchParticipant[] {getDefaultSearchParticipant()};
		IRubySearchScope scope = createWorkspaceScope();
		TypeRequestor requestor = new TypeRequestor();
		try {
			new BasicSearchEngine().search(pattern, participants, scope, requestor, null);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
		List<IType> types = new ArrayList<IType>();
		List<IType> matches = requestor.getTypes();
		for (IType type : matches) {
			if (Util.getSimpleName(type.getElementName()).equals(simpleTypeName))
					types.add(type);
		}
		return types;
	}
	
	private static class TypeRequestor extends SearchRequestor {
		private List<IType> types = new ArrayList<IType>();
		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			Object element = match.getElement();
			types.add((IType) element);
		}
		public List<IType> getTypes() {
			return types;
		}
	}
	
	
	/**
	 * Searches for all top-level types and member types in the given scope.
	 * The search can be selecting specific types (given a package or a type name
	 * prefix and match modes). 
	 * 
	 * @see SearchEngine#searchAllTypeNames(char[], char[], int, int, IJavaSearchScope, TypeNameRequestor, int, IProgressMonitor)
	 * 	for detailed comment
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

		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.searchAllTypeNames(char[], char[], int, int, IRubySearchScope, IRestrictedAccessTypeRequestor, int, IProgressMonitor)"); //$NON-NLS-1$
			Util.verbose("	- namespace: "+(namespace==null?"null":new String(namespace))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- type name: "+(typeName==null?"null":new String(typeName))); //$NON-NLS-1$ //$NON-NLS-2$
			Util.verbose("	- match rule: "+getMatchRuleString(matchRule)); //$NON-NLS-1$
			Util.verbose("	- search for: "+searchFor); //$NON-NLS-1$
			Util.verbose("	- scope: "+scope); //$NON-NLS-1$
		}

		// Return on invalid combination of namespace and type names
		if (namespace == null || namespace.length == 0) {
			if (typeName != null && typeName.length == 0) {
				if (VERBOSE) {
					Util.verbose("	=> return no result due to invalid empty values for package and type names!"); //$NON-NLS-1$
				}
				return;
			}
		}

		IndexManager indexManager = RubyModelManager.getRubyModelManager().getIndexManager();
		final char typeSuffix;
		switch(searchFor){
			case IRubySearchConstants.CLASS :
				typeSuffix = IIndexConstants.CLASS_SUFFIX;
				break;
			case IRubySearchConstants.MODULE :
				typeSuffix = IIndexConstants.MODULE_SUFFIX;
				break;
			default : 
				typeSuffix = IIndexConstants.TYPE_SUFFIX;
				break;
		}
		
		final TypeDeclarationPattern pattern = new TypeDeclarationPattern(
			null, // ignore "packages"
			getEnclosingTypeNames(namespace),
			typeName,
			typeSuffix,
			matchRule);

		// Get working copy path(s). Store in a single string in case of only one to optimize comparison in requestor
		final HashSet workingCopyPaths = new HashSet();
		String workingCopyPath = null;
		IRubyScript[] copies = getWorkingCopies();
		final int copiesLength = copies == null ? 0 : copies.length;
		if (copies != null) {
			if (copiesLength == 1) {
				workingCopyPath = copies[0].getPath().toString();
			} else {
				for (int i = 0; i < copiesLength; i++) {
					IRubyScript workingCopy = copies[i];
					workingCopyPaths.add(workingCopy.getPath().toString());
				}
			}
		}
		final String singleWkcpPath = workingCopyPath;

		// Index requestor
		IndexQueryRequestor searchRequestor = new IndexQueryRequestor(){
			public boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant) {
				// Filter unexpected types
				TypeDeclarationPattern record = (TypeDeclarationPattern)indexRecord;
				if (record.enclosingTypeNames == IIndexConstants.ONE_ZERO_CHAR) {
					return true; // filter out local and anonymous classes
				}
				switch (copiesLength) {
					case 0:
						break;
					case 1:
						if (singleWkcpPath.equals(documentPath)) {
							return true; // fliter out *the* working copy
						}
						break;
					default:
						if (workingCopyPaths.contains(documentPath)) {
							return true; // filter out working copies
						}
						break;
				}

				// Accept document path
				if (match(record.typeSuffix, record.modifiers)) {
					nameRequestor.acceptType(record.typeSuffix == IIndexConstants.MODULE_SUFFIX, record.pkg, record.simpleName, record.enclosingTypeNames, documentPath);
				}
				return true;
			}
		};
	
		try {
			if (progressMonitor != null) {
				progressMonitor.beginTask(Messages.engine_searching, 100); 
			}
			// add type names from indexes
			indexManager.performConcurrentJob(
				new PatternSearchJob(
					pattern, 
					getDefaultSearchParticipant(), // Ruby search only
					scope, 
					searchRequestor),
				waitingPolicy,
				progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 100));	
				
			// add type names from working copies
			if (copies != null) {
				for (int i = 0; i < copiesLength; i++) {
					IRubyScript workingCopy = copies[i];
					if (!scope.encloses(workingCopy)) continue;
					final String path = workingCopy.getPath().toString();
					if (workingCopy.isConsistent()) {
						// TODO Clean this up and figure out what we use instead of package names...
//						IPackageDeclaration[] packageDeclarations = workingCopy.getPackageDeclarations();
//						char[] packageDeclaration = packageDeclarations.length == 0 ? CharOperation.NO_CHAR : packageDeclarations[0].getElementName().toCharArray();
						char[] packageDeclaration = CharOperation.NO_CHAR;
						IType[] allTypes = workingCopy.getAllTypes();
						for (int j = 0, allTypesLength = allTypes.length; j < allTypesLength; j++) {
							IType type = allTypes[j];
							IRubyElement parent = type.getParent();
							char[][] enclosingTypeNames;
							if (parent instanceof IType) {
								char[] parentQualifiedName = ((IType)parent).getTypeQualifiedName("::").toCharArray();
								enclosingTypeNames = CharOperation.splitOn("::", parentQualifiedName);
							} else {
								enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
							}
							char[] simpleName = type.getElementName().toCharArray();
							int kind;
							if (type.isClass()) {
								kind = CLASS_DECL;
							} else /*if (type.isModule())*/ {
								kind = MODULE_DECL;
							}
							if (match(typeSuffix, namespace, typeName, matchRule, kind, squish(enclosingTypeNames), simpleName)) {
								nameRequestor.acceptType(type.isModule(), packageDeclaration, simpleName, enclosingTypeNames, path);
							}
						}
					} else {
						// TODO Parse and traverse AST, report all type declarations...
					}
				}
			}	
		} finally {
			if (progressMonitor != null) {
				progressMonitor.done();
			}
		}
	}
	
	private char[] squish(char[][] enclosingTypeNames) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < enclosingTypeNames.length; i++) {
			if (i != 0) buffer.append("::");
			buffer.append(enclosingTypeNames[i]);			
		}
		return buffer.toString().toCharArray();
	}

	private char[][] getEnclosingTypeNames(char[] packageName) {
		if (packageName == null || packageName.length == 0) return null;
		String raw = new String(packageName);
		String[] parts = Util.getTypeNameParts(raw);
		char[][] enclosing = new char[parts.length][];
		for (int i = 0; i < parts.length; i++) {
			enclosing[i] = parts[i].toCharArray();
		}
		return enclosing;
	}

	boolean match(char patternTypeSuffix, int modifiers) {
		switch(patternTypeSuffix) {
			case IIndexConstants.CLASS_SUFFIX :
				return (modifiers & (Flags.AccModule)) == 0;
			case IIndexConstants.TYPE_SUFFIX:
				return true;
			case IIndexConstants.MODULE_SUFFIX :
				return (modifiers & Flags.AccModule) != 0;
		}
		return true;
	}
	
	boolean match(char patternTypeSuffix, char[] patternPkg, char[] patternTypeName, int matchRule, int typeKind, char[] pkg, char[] typeName) {
		switch(patternTypeSuffix) {
			case IIndexConstants.CLASS_SUFFIX :
				if (typeKind != CLASS_DECL) return false;
				break;
			case IIndexConstants.TYPE_SUFFIX:
				if (typeKind != CLASS_DECL && typeKind != MODULE_DECL) return false;
				break;
			case IIndexConstants.MODULE_SUFFIX :
				if (typeKind != MODULE_DECL) return false;
				break;
		}
		
		if (patternPkg != null) {
			if (!doMatch(patternPkg, pkg, matchRule)) return false;
		}
		
		if (patternTypeName != null) {
			if (!doMatch(patternTypeName, typeName, matchRule)) return false;
		}
		return true;
	
	}

	private boolean isCaseSensitive(int matchRule) {
		return (matchRule & SearchPattern.R_CASE_SENSITIVE) != 0;
	}

	private boolean doMatch(char[] patternTypeName, char[] typeName, int matchRule) {
		boolean isCaseSensitive = isCaseSensitive(matchRule);
		boolean isCamelCase = (matchRule & SearchPattern.R_CAMELCASE_MATCH) != 0;
		int matchMode = matchRule & RubySearchPattern.MATCH_MODE_MASK;
		if (!isCaseSensitive && !isCamelCase) {
			patternTypeName = CharOperation.toLowerCase(patternTypeName);
		}
		boolean matchFirstChar = !isCaseSensitive || patternTypeName[0] == typeName[0];
		if (isCamelCase && matchFirstChar && CharOperation.camelCaseMatch(patternTypeName, typeName)) {
			return true;
		}
		switch(matchMode) {
			case SearchPattern.R_EXACT_MATCH :
				if (!isCamelCase) {
					return matchFirstChar && CharOperation.equals(patternTypeName, typeName, isCaseSensitive);
				}
				// fall through next case to match as prefix if camel case failed
			case SearchPattern.R_PREFIX_MATCH :
				return matchFirstChar && CharOperation.prefixEquals(patternTypeName, typeName, isCaseSensitive);
			case SearchPattern.R_PATTERN_MATCH :
				return CharOperation.match(patternTypeName, typeName, isCaseSensitive);
			case SearchPattern.R_REGEXP_MATCH :
				// TODO (frederic) implement regular expression match
				return true;
		}
		return true;
	}	
	
	/**
	 * @param matchRule
	 */
	public static String getMatchRuleString(final int matchRule) {
		if (matchRule == 0) {
			return "R_EXACT_MATCH"; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		for (int i=1; i<=8; i++) {
			int bit = matchRule & (1<<(i-1));
			if (bit != 0 && buffer.length()>0) buffer.append(" | "); //$NON-NLS-1$
			switch (bit) {
				case SearchPattern.R_PREFIX_MATCH:
					buffer.append("R_PREFIX_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_CASE_SENSITIVE:
					buffer.append("R_CASE_SENSITIVE"); //$NON-NLS-1$
					break;
				case SearchPattern.R_EQUIVALENT_MATCH:
					buffer.append("R_EQUIVALENT_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_ERASURE_MATCH:
					buffer.append("R_ERASURE_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_FULL_MATCH:
					buffer.append("R_FULL_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_PATTERN_MATCH:
					buffer.append("R_PATTERN_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_REGEXP_MATCH:
					buffer.append("R_REGEXP_MATCH"); //$NON-NLS-1$
					break;
				case SearchPattern.R_CAMELCASE_MATCH:
					buffer.append("R_CAMELCASE_MATCH"); //$NON-NLS-1$
					break;
			}
		}
		return buffer.toString();
	}

	/**
	 * @see SearchEngine#createHierarchyScope(IType) for detailed comment.
	 */
	public static IRubySearchScope createHierarchyScope(IType type) throws RubyModelException {
		return createHierarchyScope(type, DefaultWorkingCopyOwner.PRIMARY);
	}
	
	/**
	 * @see SearchEngine#createHierarchyScope(IType,WorkingCopyOwner) for detailed comment.
	 */
	public static IRubySearchScope createHierarchyScope(IType type, WorkingCopyOwner owner) throws RubyModelException {
		return new HierarchyScope(type, owner);
	}
}

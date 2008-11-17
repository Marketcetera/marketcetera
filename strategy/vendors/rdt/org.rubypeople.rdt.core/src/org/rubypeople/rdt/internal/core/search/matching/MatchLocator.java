package org.rubypeople.rdt.internal.core.search.matching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.FieldDeclarationMatch;
import org.rubypeople.rdt.core.search.FieldReferenceMatch;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.MethodDeclarationMatch;
import org.rubypeople.rdt.core.search.MethodReferenceMatch;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.core.search.SearchRequestor;
import org.rubypeople.rdt.core.search.TypeDeclarationMatch;
import org.rubypeople.rdt.core.search.TypeReferenceMatch;
import org.rubypeople.rdt.internal.compiler.util.SimpleLookupTable;
import org.rubypeople.rdt.internal.core.ExternalSourceFolderRoot;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.index.Index;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;
import org.rubypeople.rdt.internal.core.search.HandleFactory;
import org.rubypeople.rdt.internal.core.search.IndexQueryRequestor;
import org.rubypeople.rdt.internal.core.search.IndexSelector;
import org.rubypeople.rdt.internal.core.search.RubySearchDocument;
import org.rubypeople.rdt.internal.core.util.Util;

public class MatchLocator {
	
	public static final int MAX_AT_ONCE;
	static {
		long maxMemory = Runtime.getRuntime().maxMemory();		
		int ratio = (int) Math.round(((double) maxMemory) / (64 * 0x100000));
		switch (ratio) {
			case 0:
			case 1:
				MAX_AT_ONCE = 100;
				break;
			case 2:
				MAX_AT_ONCE = 200;
				break;
			case 3:
				MAX_AT_ONCE = 300;
				break;
			default:
				MAX_AT_ONCE = 400;
				break;
		}
	}

	// permanent state
	public SearchPattern pattern;
	public PatternLocator patternLocator;
	public int matchContainer;
	public SearchRequestor requestor;
	public IRubySearchScope scope;
	public IProgressMonitor progressMonitor;
	
	public IRubyScript[] workingCopies;
	public HandleFactory handleFactory;
	
	SimpleLookupTable bindings;
	
	// Progress information
	int progressStep;
	int progressWorked;
	private PossibleMatch currentPossibleMatch;
	
	public MatchLocator(
			SearchPattern pattern,
			SearchRequestor requestor,
			IRubySearchScope scope,
			IProgressMonitor progressMonitor) {				
			this.pattern = pattern;
			this.patternLocator = PatternLocator.patternLocator(this.pattern);
			this.matchContainer = this.patternLocator.matchContainer();
			this.requestor = requestor;
			this.scope = scope;
			this.progressMonitor = progressMonitor;
		}
	
	public static void findIndexMatches(InternalSearchPattern pattern, Index index,
			IndexQueryRequestor requestor, SearchParticipant participant,
			IRubySearchScope scope, IProgressMonitor monitor) throws IOException {
		pattern.findIndexMatches(index, requestor, participant, scope, monitor);	
	}

	public static IRubyElement getProjectOrJar(IRubyElement element) {
		while (!(element instanceof IRubyProject)) {
			element = element.getParent();
		}
		return element;
	}

	public static IRubyElement projectOrJarFocus(InternalSearchPattern pattern) {
		return pattern == null || pattern.focus == null ? null : getProjectOrJar(pattern.focus);
	}

	public static SearchDocument[] addWorkingCopies(InternalSearchPattern pattern,
		SearchDocument[] indexMatches, IRubyScript[] copies,
		SearchParticipant participant) {
// working copies take precedence over corresponding compilation units
		HashMap workingCopyDocuments = workingCopiesThatCanSeeFocus(copies, pattern.focus, pattern.isPolymorphicSearch(), participant);
		SearchDocument[] matches = null;
		int length = indexMatches.length;
		for (int i = 0; i < length; i++) {
			SearchDocument searchDocument = indexMatches[i];
			if (searchDocument.getParticipant() == participant) {
				SearchDocument workingCopyDocument = (SearchDocument) workingCopyDocuments.remove(searchDocument.getPath());
				if (workingCopyDocument != null) {
					if (matches == null) {
						System.arraycopy(indexMatches, 0, matches = new SearchDocument[length], 0, length);
					}
					matches[i] = workingCopyDocument;
				}
			}
		}
		if (matches == null) { // no working copy
			matches = indexMatches;
		}
		int remainingWorkingCopiesSize = workingCopyDocuments.size();
		if (remainingWorkingCopiesSize != 0) {
			System.arraycopy(matches, 0, matches = new SearchDocument[length+remainingWorkingCopiesSize], 0, length);
			Iterator iterator = workingCopyDocuments.values().iterator();
			int index = length;
			while (iterator.hasNext()) {
				matches[index++] = (SearchDocument) iterator.next();
			}
		}
		return matches;
	}
	
	public static void setFocus(InternalSearchPattern pattern, IRubyElement focus) {
		pattern.focus = focus;
	}
	
	/*
	 * Returns the working copies that can see the given focus.
	 */
	private static HashMap workingCopiesThatCanSeeFocus(IRubyScript[] copies, IRubyElement focus, boolean isPolymorphicSearch, SearchParticipant participant) {
		if (copies == null) return new HashMap();
		if (focus != null) {
			while (!(focus instanceof IRubyProject) && !(focus instanceof ExternalSourceFolderRoot)) {
				focus = focus.getParent();
			}
		}
		HashMap result = new HashMap();
		for (int i=0, length = copies.length; i<length; i++) {
			IRubyScript workingCopy = copies[i];
			IPath projectOrJar = MatchLocator.getProjectOrJar(workingCopy).getPath();
			if (focus == null || IndexSelector.canSeeFocus(focus, isPolymorphicSearch, projectOrJar)) {
				result.put(
					workingCopy.getPath().toString(),
					new WorkingCopyDocument(workingCopy, participant)
				);
			}
		}
		return result;
	}
	
	public static class WorkingCopyDocument extends RubySearchDocument {
		public IRubyScript workingCopy;
		WorkingCopyDocument(IRubyScript workingCopy, SearchParticipant participant) {
			super(workingCopy.getPath().toString(), participant);
			this.charContents = ((RubyScript)workingCopy).getContents();
			this.workingCopy = workingCopy;
		}
		public String toString() {
			return "WorkingCopyDocument for " + getPath(); //$NON-NLS-1$
		}
	}

	/**
	 * Locate the matches in the given files and report them using the search
	 * requestor.
	 */
	public void locateMatches(SearchDocument[] searchDocuments)
			throws CoreException {
		int docsLength = searchDocuments.length;
		if (BasicSearchEngine.VERBOSE) {
			System.out.println("Locating matches in documents ["); //$NON-NLS-1$
			for (int i = 0; i < docsLength; i++)
				System.out.println("\t" + searchDocuments[i]); //$NON-NLS-1$
			System.out.println("]"); //$NON-NLS-1$
		}

		// init infos for progress increasing
		int n = docsLength < 1000 ? Math.min(Math.max(docsLength / 200 + 1, 2),
				4) : 5 * (docsLength / 1000);
		this.progressStep = docsLength < n ? 1 : docsLength / n; // step
																	// should
																	// not be 0
		this.progressWorked = 0;

		// extract working copies
		ArrayList copies = new ArrayList();
		for (int i = 0; i < docsLength; i++) {
			SearchDocument document = searchDocuments[i];
			if (document instanceof WorkingCopyDocument) {
				copies.add(((WorkingCopyDocument) document).workingCopy);
			}
		}
		int copiesLength = copies.size();
		this.workingCopies = new IRubyScript[copiesLength];
		copies.toArray(this.workingCopies);

		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		this.bindings = new SimpleLookupTable();
		try {
			// optimize access to zip files during search operation
//			manager.cacheZipFiles();

			// initialize handle factory (used as a cache of handles so as to
			// optimize space)
			if (this.handleFactory == null)
				this.handleFactory = new HandleFactory();

			if (this.progressMonitor != null) {
				this.progressMonitor.beginTask("", searchDocuments.length); //$NON-NLS-1$
			}

			// initialize pattern for polymorphic search (ie. method reference
			// pattern)
//			this.patternLocator.initializePolymorphicSearch(this);

			RubyProject previousJavaProject = null;
			PossibleMatchSet matchSet = new PossibleMatchSet();
			Util.sort(searchDocuments, new Util.Comparer() {
				public int compare(Object a, Object b) {
					return ((SearchDocument) a).getPath().compareTo(
							((SearchDocument) b).getPath());
				}
			});
			int displayed = 0; // progress worked displayed
			String previousPath = null;
			for (int i = 0; i < docsLength; i++) {
				if (this.progressMonitor != null
						&& this.progressMonitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				// skip duplicate paths
				SearchDocument searchDocument = searchDocuments[i];
				searchDocuments[i] = null; // free current document
				String pathString = searchDocument.getPath();
				if (i > 0 && pathString.equals(previousPath)) {
					if (this.progressMonitor != null) {
						this.progressWorked++;
						if ((this.progressWorked % this.progressStep) == 0)
							this.progressMonitor.worked(this.progressStep);
					}
					displayed++;
					continue;
				}
				previousPath = pathString;

				Openable openable;
				IRubyScript workingCopy = null;
				if (searchDocument instanceof WorkingCopyDocument) {
					workingCopy = ((WorkingCopyDocument) searchDocument).workingCopy;
					openable = (Openable) workingCopy;
				} else {
					openable = this.handleFactory.createOpenable(pathString);
				}
				if (openable == null) {
					if (this.progressMonitor != null) {
						this.progressWorked++;
						if ((this.progressWorked % this.progressStep) == 0)
							this.progressMonitor.worked(this.progressStep);
					}
					displayed++;
					continue; // match is outside classpath
				}

				// create new parser and lookup environment if this is a new
				// project
				IResource resource = null;
				RubyProject javaProject = (RubyProject) openable
						.getRubyProject();
				resource = workingCopy != null ? workingCopy.getResource()
						: openable.getResource();
				if (resource == null)
					resource = javaProject.getProject(); // case of a file in
															// an external jar
				if (!javaProject.equals(previousJavaProject)) {
					// locate matches in previous project
					if (previousJavaProject != null) {
						try {
							locateMatches(previousJavaProject, matchSet, i
									- displayed);
							displayed = i;
						} catch (RubyModelException e) {
							// problem with classpath in this project -> skip it
						}
						matchSet.reset();
					}
					previousJavaProject = javaProject;
				}
				matchSet.add(new PossibleMatch(this, resource, openable,
						searchDocument,
						((InternalSearchPattern) this.pattern).mustResolve));
			}

			// last project
			if (previousJavaProject != null) {
				try {
					locateMatches(previousJavaProject, matchSet, docsLength
							- displayed);
				} catch (RubyModelException e) {
					// problem with loadpath in last project -> ignore
				}
			}

		} finally {
			if (this.progressMonitor != null)
				this.progressMonitor.done();
//			if (this.nameEnvironment != null)
//				this.nameEnvironment.cleanup();
//			manager.flushZipFiles();
			this.bindings = null;
		}
	}
	
	protected boolean encloses(IRubyElement element) {
		return element != null && this.scope.encloses(element);
	}
	
	protected void report(SearchMatch match) throws CoreException {
		long start = -1;
		if (BasicSearchEngine.VERBOSE) {
			start = System.currentTimeMillis();
			System.out.println("Reporting match"); //$NON-NLS-1$
			System.out.println("\tResource: " + match.getResource());//$NON-NLS-1$
			System.out.println("\tPositions: [offset=" + match.getOffset() + ", length=" + match.getLength() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//			try {
//				if (this.parser != null && match.getOffset() > 0 && match.getLength() > 0 && !(match.getElement() instanceof BinaryMember)) {
//					String selection = new String(this.parser.scanner.source, match.getOffset(), match.getLength());
//					System.out.println("\tSelection: -->" + selection + "<--"); //$NON-NLS-1$ //$NON-NLS-2$
//				}
//			} catch (Exception e) {
//				// it's just for debug purposes... ignore all exceptions in this area
//			}
			try {
				RubyElement javaElement = (RubyElement)match.getElement();
				System.out.println("\tRuby element: "+ javaElement.toStringWithAncestors()); //$NON-NLS-1$
				if (!javaElement.exists()) {
					System.out.println("\t\tWARNING: this element does NOT exist!"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				// it's just for debug purposes... ignore all exceptions in this area
			}
//			if (match instanceof TypeReferenceMatch) {
//				try {
//					TypeReferenceMatch typeRefMatch = (TypeReferenceMatch) match;
//					RubyElement local = (RubyElement) typeRefMatch.getLocalElement();
//					if (local != null) {
//						System.out.println("\tLocal element: "+ local.toStringWithAncestors()); //$NON-NLS-1$
//					}
//					IRubyElement[] others = typeRefMatch.getOtherElements();
//					if (others != null) {
//						int length = others.length;
//						if (length > 0) {
//							System.out.println("\tOther elements:"); //$NON-NLS-1$
//							for (int i=0; i<length; i++) {
//								RubyElement other = (RubyElement) others[i];
//								System.out.println("\t\t- "+ other.toStringWithAncestors()); //$NON-NLS-1$
//							}
//						}
//					}
//				} catch (Exception e) {
//					// it's just for debug purposes... ignore all exceptions in this area
//				}
//			}
			System.out.println(match.getAccuracy() == SearchMatch.A_ACCURATE
				? "\tAccuracy: EXACT_MATCH" //$NON-NLS-1$
				: "\tAccuracy: POTENTIAL_MATCH"); //$NON-NLS-1$
			System.out.print("\tRule: "); //$NON-NLS-1$
			if (match.isExact()) {
				System.out.print("EXACT"); //$NON-NLS-1$
			} else if (match.isEquivalent()) {
				System.out.print("EQUIVALENT"); //$NON-NLS-1$
			} else if (match.isErasure()) {
				System.out.print("ERASURE"); //$NON-NLS-1$
			} else {
				System.out.print("INVALID RULE"); //$NON-NLS-1$
			}
//			if (match instanceof MethodReferenceMatch) {
//				MethodReferenceMatch methodReferenceMatch = (MethodReferenceMatch) match;
//				if (methodReferenceMatch.isSuperInvocation()) {
//					System.out.print("+SUPER INVOCATION"); //$NON-NLS-1$
//				}
//				if (methodReferenceMatch.isImplicit()) {
//					System.out.print("+IMPLICIT"); //$NON-NLS-1$
//				}
//				if (methodReferenceMatch.isSynthetic()) {
//					System.out.print("+SYNTHETIC"); //$NON-NLS-1$
//				}
//			}
			System.out.println("\n\tRaw: "+match.isRaw()); //$NON-NLS-1$
		}
		this.requestor.acceptSearchMatch(match);
//		if (BasicSearchEngine.VERBOSE)
//			this.resultCollectorTime += System.currentTimeMillis()-start;
	}
	
	/**
	 * Locate the matches amongst the possible matches.
	 */
	protected void locateMatches(RubyProject javaProject, PossibleMatchSet matchSet, int expected) throws CoreException {
		PossibleMatch[] possibleMatches = matchSet.getPossibleMatches(javaProject.getSourceFolderRoots());
		int length = possibleMatches.length;
		// increase progress from duplicate matches not stored in matchSet while adding...
		if (this.progressMonitor != null && expected>length) {
			this.progressWorked += expected-length;
			this.progressMonitor.worked( expected-length);
		}
		// locate matches (processed matches are limited to avoid problem while using VM default memory heap size)
		for (int index = 0; index < length;) {
			int max = Math.min(MAX_AT_ONCE, length - index);
			locateMatches(javaProject, possibleMatches, index, max);
			index += max;
		}
		this.patternLocator.clear();
	}
	
	protected void locateMatches(RubyProject rubyProject, PossibleMatch[] possibleMatches, int start, int length) throws CoreException {
		for (int i = start, maxUnits = start + length; i < maxUnits; i++) {
			PossibleMatch possibleMatch = possibleMatches[i];
			process(possibleMatch);			
			possibleMatch.cleanUp();
		}
	}
	
	protected void process(PossibleMatch possibleMatch) {
		this.currentPossibleMatch = possibleMatch;
		RubyScript script = (RubyScript) possibleMatch.openable;
		this.patternLocator.reportMatches(script, this);
		this.currentPossibleMatch = null;
	}

	public SearchMatch newDeclarationMatch(
			IRubyElement element,
			int accuracy,
			int offset,  
			int length,
			SearchParticipant participant, 
			IResource resource) {
		switch (element.getElementType()) {
//			case IRubyElement.SOURCE_FOLDER:
//				return new PackageDeclarationMatch(element, accuracy, offset, length, participant, resource);
			case IRubyElement.TYPE:
				return new TypeDeclarationMatch(element, accuracy, offset, length, participant, resource);
			case IRubyElement.FIELD:
			case IRubyElement.INSTANCE_VAR:
			case IRubyElement.CLASS_VAR:
			case IRubyElement.GLOBAL:
			case IRubyElement.CONSTANT:
				return new FieldDeclarationMatch(element, accuracy, offset, length, participant, resource);
			case IRubyElement.METHOD:
				return new MethodDeclarationMatch(element, accuracy, offset, length, participant, resource);
//			case IRubyElement.LOCAL_VARIABLE:
//				return new LocalVariableDeclarationMatch(element, accuracy, offset, length, participant, resource);
			default:
				return null;
		}
	}
	
	public SearchMatch newDeclarationMatch(
			IRubyElement element,
			int accuracy,
			int offset,  
			int length) {
		SearchParticipant participant = getParticipant(); 
		IResource resource = this.currentPossibleMatch.resource;
		return newDeclarationMatch(element, accuracy, offset, length, participant, resource);
	}
	
	public SearchParticipant getParticipant() {
		return this.currentPossibleMatch.document.getParticipant();
	}
	
	public TypeReferenceMatch newTypeReferenceMatch(
			IRubyElement enclosingElement,
			int accuracy,
			int offset,  
			int length) {
		SearchParticipant participant = getParticipant(); 
		IResource resource = this.currentPossibleMatch.resource;
		return new TypeReferenceMatch(enclosingElement, accuracy, offset, length, participant, resource);
	}
	
	public SearchMatch newFieldReferenceMatch(
			IRubyElement enclosingElement,
			IRubyElement binding, int accuracy,
			int offset,  
			int length,
			Node reference) {
		boolean isReadAccess = false;
		boolean isWriteAccess = false;
		if ((reference instanceof GlobalAsgnNode) || (reference instanceof ClassVarAsgnNode) ||
				(reference instanceof InstAsgnNode)) {
			isWriteAccess = true;
		} else {
			isReadAccess = true;
		}
		SearchParticipant participant = getParticipant(); 
		IResource resource = this.currentPossibleMatch.resource;
		return new FieldReferenceMatch(enclosingElement, binding, accuracy, offset, length, isReadAccess, isWriteAccess, false, participant, resource);
	}
	

	public SearchMatch newMethodReferenceMatch(
			IRubyElement enclosingElement,
			IRubyElement binding,
			List<String> arguments,
			int accuracy,
			int offset,  
			int length,
			boolean isConstructor,
			Node reference) {
		SearchParticipant participant = getParticipant(); 
		IResource resource = this.currentPossibleMatch.resource;
		return new MethodReferenceMatch(enclosingElement, binding, arguments, accuracy, offset, length, isConstructor, false, participant, resource);
	}
	
}

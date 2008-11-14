package org.rubypeople.rdt.internal.core.hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.compiler.util.HashtableOfObject;
import org.rubypeople.rdt.internal.core.IPathRequestor;
import org.rubypeople.rdt.internal.core.Member;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.SourceFolder;
import org.rubypeople.rdt.internal.core.search.HandleFactory;
import org.rubypeople.rdt.internal.core.search.IndexQueryRequestor;
import org.rubypeople.rdt.internal.core.search.RubySearchParticipant;
import org.rubypeople.rdt.internal.core.search.SubTypeSearchJob;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;
import org.rubypeople.rdt.internal.core.search.matching.MatchLocator;
import org.rubypeople.rdt.internal.core.search.matching.SuperTypeReferencePattern;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public class IndexBasedHierarchyBuilder extends HierarchyBuilder {
	public static final int MAXTICKS = 800; // heuristic so that there still progress for deep hierachies
	
	private IRubySearchScope scope;
	private HashMap binariesFromIndexMatches;

	public IndexBasedHierarchyBuilder(TypeHierarchy hierarchy, IRubySearchScope scope) throws RubyModelException {
		super(hierarchy);
		this.binariesFromIndexMatches = new HashMap(10);
		this.scope = scope;
	}

	@Override
	public void build(boolean computeSubtypes) throws RubyModelException,
			CoreException {
				
			if (computeSubtypes) {
				// Note by construction there always is a focus type here
				IType focusType = getType();
				boolean focusIsObject = focusType.getElementName().equals(new String(IIndexConstants.OBJECT));
				int amountOfWorkForSubtypes = focusIsObject ? 5 : 80; // percentage of work needed to get possible subtypes
				IProgressMonitor possibleSubtypesMonitor = 
					this.hierarchy.progressMonitor == null ? 
						null : 
						new SubProgressMonitor(this.hierarchy.progressMonitor, amountOfWorkForSubtypes);
				HashSet localTypes = new HashSet(10); // contains the paths that have potential subtypes that are local/anonymous types
				String[] allPossibleSubtypes;
				if (((Member)focusType).getOuterMostLocalContext() == null) {
					// top level or member type
					allPossibleSubtypes = this.determinePossibleSubTypes(localTypes, possibleSubtypesMonitor);
				} else {
					// local or anonymous type
					allPossibleSubtypes = new String[0];
				}
				if (allPossibleSubtypes != null) {
					IProgressMonitor buildMonitor = 
						this.hierarchy.progressMonitor == null ? 
							null : 
							new SubProgressMonitor(this.hierarchy.progressMonitor, 100 - amountOfWorkForSubtypes);
					this.hierarchy.initialize(allPossibleSubtypes.length);
					buildFromPotentialSubtypes(allPossibleSubtypes, localTypes, buildMonitor);
				}
			} else {
				this.hierarchy.initialize(1);
				this.buildSupertypes();
			}
	}
	
	/**
	 * Configure this type hierarchy based on the given potential subtypes.
	 */
	private void buildFromPotentialSubtypes(String[] allPotentialSubTypes, HashSet localTypes, IProgressMonitor monitor) {
		IType focusType = this.getType();
			
		// substitute compilation units with working copies
		HashMap wcPaths = new HashMap(); // a map from path to working copies
		int wcLength;
		org.rubypeople.rdt.core.IRubyScript[] workingCopies = this.hierarchy.workingCopies;
		if (workingCopies != null && (wcLength = workingCopies.length) > 0) {
			String[] newPaths = new String[wcLength];
			for (int i = 0; i < wcLength; i++) {
				org.rubypeople.rdt.core.IRubyScript workingCopy = workingCopies[i];
				String path = workingCopy.getPath().toString();
				wcPaths.put(path, workingCopy);
				newPaths[i] = path;
			}
			int potentialSubtypesLength = allPotentialSubTypes.length;
			System.arraycopy(allPotentialSubTypes, 0, allPotentialSubTypes = new String[potentialSubtypesLength+wcLength], 0, potentialSubtypesLength);
			System.arraycopy(newPaths, 0, allPotentialSubTypes, potentialSubtypesLength, wcLength);
		}
				
		int length = allPotentialSubTypes.length;

		// inject the compilation unit of the focus type (so that types in
		// this cu have special visibility permission (this is also usefull
		// when the cu is a working copy)
		Openable focusCU = (Openable)focusType.getRubyScript();
		String focusPath = null;
		if (focusCU != null) {
			focusPath = focusCU.getPath().toString();
			if (length > 0) {
				System.arraycopy(allPotentialSubTypes, 0, allPotentialSubTypes = new String[length+1], 0, length);
				allPotentialSubTypes[length] = focusPath;	
			} else {
				allPotentialSubTypes = new String[] {focusPath};
			}
			length++;
		}
		
		// sort by projects
		/*
		 * NOTE: To workaround pb with hierarchy resolver that requests top  
		 * level types in the process of caching an enclosing type, this needs to
		 * be sorted in reverse alphabetical order so that top level types are cached
		 * before their inner types.
		 */
		org.rubypeople.rdt.internal.core.util.Util.sortReverseOrder(allPotentialSubTypes);
		
		ArrayList potentialSubtypes = new ArrayList();

		try {
			// create element infos for subtypes
			HandleFactory factory = new HandleFactory();
			IRubyProject currentProject = null;
			if (monitor != null) monitor.beginTask("", length*2 /* 1 for build binding, 1 for connect hierarchy*/); //$NON-NLS-1$
			for (int i = 0; i < length; i++) {
				try {
					String resourcePath = allPotentialSubTypes[i];
					
					// skip duplicate paths (e.g. if focus path was injected when it was already a potential subtype)
					if (i > 0 && resourcePath.equals(allPotentialSubTypes[i-1])) continue;
					
					Openable handle;
					org.rubypeople.rdt.core.IRubyScript workingCopy = (org.rubypeople.rdt.core.IRubyScript)wcPaths.get(resourcePath);
					if (workingCopy != null) {
						handle = (Openable)workingCopy;
					} else {
						handle = 
							resourcePath.equals(focusPath) ? 
								focusCU :
								factory.createOpenable(resourcePath);
						if (handle == null) continue; // match is outside loadpath
					}
					
					IRubyProject project = handle.getRubyProject();
					if (currentProject == null) {
						currentProject = project;
						potentialSubtypes = new ArrayList(5);
					} else if (!currentProject.equals(project)) {
						// build current project
						this.buildForProject((RubyProject)currentProject, potentialSubtypes, workingCopies, localTypes, monitor);
						currentProject = project;
						potentialSubtypes = new ArrayList(5);
					}
					
					potentialSubtypes.add(handle);
				} catch (RubyModelException e) {
					continue;
				}
			}
			
			// build last project
			try {
				if (currentProject == null) {
					// case of no potential subtypes
					currentProject = focusType.getRubyProject();
					potentialSubtypes.add(focusType.getRubyScript());
				}
				this.buildForProject((RubyProject)currentProject, potentialSubtypes, workingCopies, localTypes, monitor);
			} catch (RubyModelException e) {
				// ignore
			}
			
			// Compute hierarchy of focus type if not already done (case of a type with potential subtypes that are not real subtypes)
			if (!this.hierarchy.contains(focusType)) {
				try {
					currentProject = focusType.getRubyProject();
					potentialSubtypes = new ArrayList();
					potentialSubtypes.add(focusType.getRubyScript());
					
					this.buildForProject((RubyProject)currentProject, potentialSubtypes, workingCopies, localTypes, monitor);
				} catch (RubyModelException e) {
					// ignore
				}
			}
			
			// Add focus if not already in (case of a type with no explicit super type)
			if (!this.hierarchy.contains(focusType)) {
				this.hierarchy.addRootClass(focusType);
			}
		} finally {
			if (monitor != null) monitor.done();
		}
	}
	
	private void buildForProject(RubyProject project, ArrayList potentialSubtypes, org.rubypeople.rdt.core.IRubyScript[] workingCopies, HashSet localTypes, IProgressMonitor monitor) throws RubyModelException {
		// copy vectors into arrays
		int openablesLength = potentialSubtypes.size();
		Openable[] openables = new Openable[openablesLength];
		potentialSubtypes.toArray(openables);

		// resolve
		if (openablesLength > 0) {
			IType focusType = this.getType();
			boolean inProjectOfFocusType = focusType != null && focusType.getRubyProject().equals(project);
			org.rubypeople.rdt.core.IRubyScript[] unitsToLookInside = null;
			if (inProjectOfFocusType) {
				org.rubypeople.rdt.core.IRubyScript unitToLookInside = focusType.getRubyScript();
				if (unitToLookInside != null) {
					int wcLength = workingCopies == null ? 0 : workingCopies.length;
					if (wcLength == 0) {
						unitsToLookInside = new org.rubypeople.rdt.core.IRubyScript[] {unitToLookInside};
					} else {
						unitsToLookInside = new org.rubypeople.rdt.core.IRubyScript[wcLength+1];
						unitsToLookInside[0] = unitToLookInside;
						System.arraycopy(workingCopies, 0, unitsToLookInside, 1, wcLength);
					}
				} else {
					unitsToLookInside = workingCopies;
				}
			}

//			SearchableEnvironment searchableEnvironment = project.newSearchableNameEnvironment(unitsToLookInside);
//			this.nameLookup = searchableEnvironment.nameLookup;
//			Map options = project.getOptions(true);
//			// disable task tags to speed up parsing
//			options.put(RubyCore.COMPILER_TASK_TAGS, ""); //$NON-NLS-1$
//			this.hierarchyResolver = 
//				new HierarchyResolver(searchableEnvironment, options, this, new DefaultProblemFactory());
			if (focusType != null) {
				Member declaringMember = ((Member)focusType).getOuterMostLocalContext();
				if (declaringMember == null) {
					// top level or member type
					if (!inProjectOfFocusType) {
						char[] typeQualifiedName = focusType.getTypeQualifiedName("::").toCharArray();
						String[] packageName = ((SourceFolder) focusType.getSourceFolder()).names;
//						if (searchableEnvironment.findType(typeQualifiedName, Util.toCharArrays(packageName)) == null) {
//							// focus type is not visible in this project: no need to go further
//							return;
//						}
					}
				} else {
					// local or anonymous type
					Openable openable;
					openable = (Openable)declaringMember.getRubyScript();
					
					localTypes = new HashSet();
					localTypes.add(openable.getPath().toString());
					this.hierarchyResolver.resolve(new Openable[] {openable}, localTypes, monitor);
					return;
				}
			}
			this.hierarchyResolver.resolve(openables, localTypes, monitor);
		}
	}

	/**
	 * Returns all of the possible subtypes of this type hierarchy.
	 * Returns null if they could not be determine.
	 */
	private String[] determinePossibleSubTypes(final HashSet localTypes, IProgressMonitor monitor) {

		class PathCollector implements IPathRequestor {
			HashSet paths = new HashSet(10);
			public void acceptPath(String path, boolean containsLocalTypes) {
				this.paths.add(path);
				if (containsLocalTypes) {
					localTypes.add(path);
				}
			}
		}
		PathCollector collector = new PathCollector();
		
		try {
			if (monitor != null) monitor.beginTask("", MAXTICKS); //$NON-NLS-1$
			searchAllPossibleSubTypes(
				this.getType(),
				this.scope,
				this.binariesFromIndexMatches,
				collector,
				IRubySearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				monitor);
		} finally {
			if (monitor != null) monitor.done();
		}

		HashSet paths = collector.paths;
		int length = paths.size();
		String[] result = new String[length];
		int count = 0;
		for (Iterator iter = paths.iterator(); iter.hasNext();) {
			result[count++] = (String) iter.next();
		} 
		return result;
	}
	
	/**
	 * Collection used to queue subtype index queries
	 */
	static class Queue {
		public char[][] names = new char[10][];
		public int start = 0;
		public int end = -1;
		public void add(char[] name){
			if (++this.end == this.names.length){
				this.end -= this.start;
				System.arraycopy(this.names, this.start, this.names = new char[this.end*2][], 0, this.end);
				this.start = 0;
			}
			this.names[this.end] = name;
		}
		public char[] retrieve(){
			if (this.start > this.end) return null; // none
			
			char[] name = this.names[this.start++];
			if (this.start > this.end){
				this.start = 0;
				this.end = -1;
			}
			return name;
		}
		public String toString(){
			StringBuffer buffer = new StringBuffer("Queue:\n"); //$NON-NLS-1$
			for (int i = this.start; i <= this.end; i++){
				buffer.append(this.names[i]).append('\n');		
			}
			return buffer.toString();
		}
	}
	
	/**
	 * Find the set of candidate subtypes of a given type.
	 *
	 * The requestor is notified of super type references (with actual path of
	 * its occurrence) for all types which are potentially involved inside a particular
	 * hierarchy.
	 * The match locator is not used here to narrow down the results, the type hierarchy
	 * resolver is rather used to compute the whole hierarchy at once.
	 * @param type
	 * @param scope
	 * @param binariesFromIndexMatches
	 * @param pathRequestor
	 * @param waitingPolicy
	 * @param progressMonitor
	 */
	public static void searchAllPossibleSubTypes(
		IType type,
		IRubySearchScope scope,
		final Map binariesFromIndexMatches,
		final IPathRequestor pathRequestor,
		int waitingPolicy,	// WaitUntilReadyToSearch | ForceImmediateSearch | CancelIfNotReadyToSearch
		IProgressMonitor progressMonitor) {

		/* embed constructs inside arrays so as to pass them to (inner) collector */
		final Queue queue = new Queue();
		final HashtableOfObject foundSuperNames = new HashtableOfObject(5);

		IndexManager indexManager = RubyModelManager.getRubyModelManager().getIndexManager();

		/* use a special collector to collect paths and queue new subtype names */
		IndexQueryRequestor searchRequestor = new IndexQueryRequestor() {
			public boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant) {
				SuperTypeReferencePattern record = (SuperTypeReferencePattern)indexRecord;
				boolean isLocalOrAnonymous = record.enclosingTypeName == IIndexConstants.ONE_ZERO;
				pathRequestor.acceptPath(documentPath, isLocalOrAnonymous);
				char[] typeName = record.simpleName;
				if (!isLocalOrAnonymous // local or anonymous types cannot have subtypes outside the cu that define them
						&& !foundSuperNames.containsKey(typeName)){
					foundSuperNames.put(typeName, typeName);
					queue.add(typeName);
				}
				return true;
			}		
		};

		int superRefKind;
//		try {
			superRefKind = type.isClass() ? SuperTypeReferencePattern.ONLY_SUPER_CLASSES : SuperTypeReferencePattern.ALL_SUPER_TYPES;
//		} catch (RubyModelException e) {
//			superRefKind = SuperTypeReferencePattern.ALL_SUPER_TYPES;
//		}
		SuperTypeReferencePattern pattern =
			new SuperTypeReferencePattern(null, null, superRefKind, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		MatchLocator.setFocus(pattern, type);
		SubTypeSearchJob job = new SubTypeSearchJob(
			pattern, 
			new RubySearchParticipant(), // ruby search only
			scope, 
			searchRequestor);

		int ticks = 0;
		queue.add(type.getElementName().toCharArray());
		try {
			while (queue.start <= queue.end) {
				if (progressMonitor != null && progressMonitor.isCanceled()) return;

				// all subclasses of OBJECT are actually all types
				char[] currentTypeName = queue.retrieve();
				if (CharOperation.equals(currentTypeName, IIndexConstants.OBJECT))
					currentTypeName = null;

				// search all index references to a given supertype
				String simple = new String(currentTypeName);
				int index = simple.lastIndexOf("::");
				if (index != -1) {
					simple = simple.substring(index + 2);
				}
				pattern.superSimpleName = simple.toCharArray();
				
				indexManager.performConcurrentJob(job, waitingPolicy, null); // no sub progress monitor since its too costly for deep hierarchies
				if (progressMonitor != null && ++ticks <= MAXTICKS)
					progressMonitor.worked(1);

				// in case, we search all subtypes, no need to search further
				if (currentTypeName == null) break;
			}
		} finally {
			job.finished();
		}
	}
}

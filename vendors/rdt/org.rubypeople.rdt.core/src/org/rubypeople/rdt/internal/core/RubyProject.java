package org.rubypeople.rdt.internal.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRegion;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.search.CollectingSearchRequestor;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.compiler.util.ObjectVector;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RubyProject extends Openable implements IProjectNature, IRubyElement, IRubyProject {

	protected IProject project;
	protected boolean scratched;

	/**
	 * Name of file containing custom project preferences
	 */
	private static final String PREF_FILENAME = ".rprefs"; //$NON-NLS-1$

	/*
	 * Value of project's resolved loadpath while it is being resolved
	 */
	private static final ILoadpathEntry[] RESOLUTION_IN_PROGRESS = new ILoadpathEntry[0];
	static final String LOADPATH_FILENAME = ".loadpath";
	static final ILoadpathEntry[] INVALID_LOADPATH = new ILoadpathEntry[0];

	/**
	 * Whether the underlying file system is case sensitive.
	 */
	protected static final boolean IS_CASE_SENSITIVE = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * An empty array of strings indicating that a project doesn't have any
	 * prerequesite projects.
	 */
	protected static final String[] NO_PREREQUISITES = new String[0];

	/*
	 * Value of project's resolved loadpath while it is being resolved
	 */

	public RubyProject() {
		super(null);
	}

	/**
	 * @param aProject
	 */
	public RubyProject(IProject aProject, RubyElement parent) {
		super(parent);
		setProject(aProject);
	}

	/**
	 * Configure the project with Ruby nature.
	 */
	public void configure() throws CoreException {
		// register Ruby builder
		addToBuildSpec(RubyCore.BUILDER_ID);
	}

	/**
	 * Adds a builder to the build spec for the given project.
	 */
	protected boolean addToBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = this.project.getDescription();
		int commandIndex = getRubyCommandIndex(description.getBuildSpec());

		if (commandIndex == -1) {

			// Add a Ruby command to the build spec
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			setRubyCommand(description, command);
			return true;
		}
		return false;
	}

	/**
	 * Find the specific Ruby command amongst the given build spec and return
	 * its index or -1 if not found.
	 */
	private int getRubyCommandIndex(ICommand[] buildSpec) {

		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(RubyCore.BUILDER_ID)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Update the Ruby command in the build spec (replace existing one if
	 * present, add one first if none).
	 */
	private void setRubyCommand(IProjectDescription description, ICommand newCommand) throws CoreException {

		ICommand[] oldBuildSpec = description.getBuildSpec();
		int oldRubyCommandIndex = getRubyCommandIndex(oldBuildSpec);
		ICommand[] newCommands;

		if (oldRubyCommandIndex == -1) {
			// Add a Ruby build spec before other builders (1FWJK7I)
			newCommands = new ICommand[oldBuildSpec.length + 1];
			System.arraycopy(oldBuildSpec, 0, newCommands, 1, oldBuildSpec.length);
			newCommands[0] = newCommand;
		} else {
			oldBuildSpec[oldRubyCommandIndex] = newCommand;
			newCommands = oldBuildSpec;
		}

		// Commit the spec change into the project
		description.setBuildSpec(newCommands);
		this.project.setDescription(description, null);
	}

	/**
	 * /** Removes the Java nature from the project.
	 */
	public void deconfigure() throws CoreException {

		// deregister Ruby builder
		removeFromBuildSpec(RubyCore.BUILDER_ID);
	}

	/**
	 * Removes the given builder from the build spec for the given project.
	 */
	protected void removeFromBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = this.project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
				description.setBuildSpec(newCommands);
				this.project.setDescription(description, null);
				return;
			}
		}
	}

	/**
	 * Returns true if this handle represents the same Ruby project as the given
	 * handle. Two handles represent the same project if they are identical or
	 * if they represent a project with the same underlying resource and
	 * occurrence counts.
	 * 
	 * @see RubyElement#equals(Object)
	 */
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (!(o instanceof RubyProject))
			return false;

		RubyProject other = (RubyProject) o;
		return this.project.equals(other.getProject());
	}

	public int hashCode() {
		if (this.project == null) {
			return super.hashCode() * 10 + 1;
		}
		return this.project.hashCode() * 10 + 2;
	}

	public boolean exists() {
		return hasRubyNature(this.project);
	}

	public RubyModelManager.PerProjectInfo getPerProjectInfo() throws RubyModelException {
		return RubyModelManager.getRubyModelManager().getPerProjectInfoCheckExistence(this.project);
	}

	private IPath getPluginWorkingLocation() {
		return this.project.getWorkingLocation(RubyCore.PLUGIN_ID);
	}

	public IProject getProject() {
		return project;
	}

	/**
	 * @see IRubyElement
	 */
	public IPath getPath() {
		return this.project.getFullPath();
	}

	protected IProject getProject(String name) {
		return RubyCore.getWorkspace().getRoot().getProject(name);
	}

	public void setProject(IProject aProject) {
		project = aProject;
	}

	public IResource getResource() {
		return this.project;
	}

	public String[] getRequiredProjectNames() throws RubyModelException {
		return this.projectPrerequisites(getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																									 * don't
																									 * generateMarkerOnError
																									 */, false/*
																																		 * don't
																																		 * returnResolutionInProgress
																																		 */));
	}

	public String[] projectPrerequisites(ILoadpathEntry[] entries) throws RubyModelException {

		ArrayList prerequisites = new ArrayList();
		// need resolution
		entries = getResolvedLoadpath(entries, null, true, false, null/*
																		 * no
																		 * reverse
																		 * map
																		 */);
		for (int i = 0, length = entries.length; i < length; i++) {
			ILoadpathEntry entry = entries[i];
			if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
				prerequisites.add(entry.getPath().lastSegment());
			}
		}
		int size = prerequisites.size();
		if (size == 0) {
			return NO_PREREQUISITES;
		} else {
			String[] result = new String[size];
			prerequisites.toArray(result);
			return result;
		}
	}

	/**
	 * @see IRubyElement
	 */
	public IResource getUnderlyingResource() throws RubyModelException {
		if (!exists())
			throw newNotPresentException();
		return this.project;
	}

	/**
	 * Returns the project custom preference pool. Project preferences may
	 * include custom encoding.
	 * 
	 * @return IEclipsePreferences
	 */
	public IEclipsePreferences getEclipsePreferences() {
		if (!RubyProject.hasRubyNature(this.project))
			return null;
		// Get cached preferences if exist
		RubyModelManager.PerProjectInfo perProjectInfo = RubyModelManager.getRubyModelManager().getPerProjectInfo(this.project, true);
		if (perProjectInfo.preferences != null)
			return perProjectInfo.preferences;
		// Init project preferences
		IScopeContext context = new ProjectScope(getProject());
		final IEclipsePreferences eclipsePreferences = context.getNode(RubyCore.PLUGIN_ID);
		updatePreferences(eclipsePreferences);
		perProjectInfo.preferences = eclipsePreferences;

		// Listen to node removal from parent in order to reset cache (see bug
		// 68993)
		IEclipsePreferences.INodeChangeListener nodeListener = new IEclipsePreferences.INodeChangeListener() {
			public void added(IEclipsePreferences.NodeChangeEvent event) {
			// do nothing
			}

			public void removed(IEclipsePreferences.NodeChangeEvent event) {
				if (event.getChild() == eclipsePreferences) {
					RubyModelManager.getRubyModelManager().resetProjectPreferences(RubyProject.this);
				}
			}
		};
		((IEclipsePreferences) eclipsePreferences.parent()).addNodeChangeListener(nodeListener);

		// Listen to preference changes
		IEclipsePreferences.IPreferenceChangeListener preferenceListener = new IEclipsePreferences.IPreferenceChangeListener() {
			public void preferenceChange(IEclipsePreferences.PreferenceChangeEvent event) {
				RubyModelManager.getRubyModelManager().resetProjectOptions(RubyProject.this);
			}
		};
		eclipsePreferences.addPreferenceChangeListener(preferenceListener);
		return eclipsePreferences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#getElementName()
	 */
	public String getElementName() {
		if (project == null) {
			return super.getElementName();
		}
		return project.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.RubyElement#getElementType()
	 */
	public int getElementType() {
		return IRubyElement.RUBY_PROJECT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#hasChildren()
	 */
	public boolean hasChildren() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyProject#findType(java.lang.String)
	 */
	public IType findType(String fullyQualifiedName) {
		return findType(fullyQualifiedName, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyProject#findType(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IType findType(String fullyQualifiedName, IProgressMonitor monitor) {
		try {
			SearchEngine engine = new SearchEngine();
			SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, fullyQualifiedName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
			SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
			IRubySearchScope scope = SearchEngine.createWorkspaceScope();
			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
			engine.search(pattern, participants, scope, requestor, monitor);
			List<SearchMatch> matches = requestor.getResults();
			for (SearchMatch match : matches) {
				IType type = (IType) match.getElement();
				if (type.getFullyQualifiedName().equals(fullyQualifiedName))
					return type;
			}
		} catch (CoreException e) {
			RubyCore.log(e);
		}		
		return null;
	}

	/**
	 * @param project
	 * @return
	 */
	public static boolean hasRubyNature(IProject project) {
		try {
			return project.hasNature(RubyCore.NATURE_ID);
		} catch (CoreException e) {
			// project does not exist or is not open
		}
		return false;
	}

	/**
	 * @see Openable
	 */
	protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws RubyModelException {

		// check whether the ruby project can be opened
		if (!hasRubyNature((IProject) underlyingResource)) {
			throw new RubyModelException(new RubyModelStatus(IRubyModelStatusConstants.PROJECT_HAS_NO_RUBY_NATURE, this));
		}

		// cannot refresh cp markers on opening (emulate cp check on startup)
		// since can create deadlocks (see bug 37274)
		ILoadpathEntry[] resolvedClasspath = getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																										 * don't
																										 * generateMarkerOnError
																										 */, false/*
																																			 * don't
																																			 * returnResolutionInProgress
																																			 */);

		// compute the src folder roots
		info.setChildren(computeSourceFolderRoots(resolvedClasspath, false, null /*
																					 * no
																					 * reverse
																					 * map
																					 */));

		// remember the timestamps of external libraries the first time they are
		// looked up
		getPerProjectInfo().rememberExternalLibTimestamps();

		return true;
	}

	/**
	 * Returns (local/all) the package fragment roots identified by the given
	 * project's classpath. Note: this follows project classpath references to
	 * find required project contributions, eliminating duplicates silently.
	 * Only works with resolved entries
	 * 
	 * @param resolvedClasspath
	 *            ILoadpathEntry[]
	 * @param retrieveExportedRoots
	 *            boolean
	 * @return IPackageFragmentRoot[]
	 * @throws RubyModelException
	 */
	public ISourceFolderRoot[] computeSourceFolderRoots(ILoadpathEntry[] resolvedClasspath, boolean retrieveExportedRoots, Map rootToResolvedEntries) throws RubyModelException {

		ObjectVector accumulatedRoots = new ObjectVector();
		computeSourceFolderRoots(resolvedClasspath, accumulatedRoots, new HashSet(5), // rootIDs
				null, // inside original project
				true, // check existency
				retrieveExportedRoots, rootToResolvedEntries);
		ISourceFolderRoot[] rootArray = new ISourceFolderRoot[accumulatedRoots.size()];
		accumulatedRoots.copyInto(rootArray);
		return rootArray;
	}

	/**
	 * Returns (local/all) the package fragment roots identified by the given
	 * project's classpath. Note: this follows project classpath references to
	 * find required project contributions, eliminating duplicates silently.
	 * Only works with resolved entries
	 * 
	 * @param resolvedClasspath
	 *            IClasspathEntry[]
	 * @param accumulatedRoots
	 *            ObjectVector
	 * @param rootIDs
	 *            HashSet
	 * @param referringEntry
	 *            project entry referring to this CP or null if initial project
	 * @param checkExistency
	 *            boolean
	 * @param retrieveExportedRoots
	 *            boolean
	 * @throws RubyModelException
	 */
	public void computeSourceFolderRoots(ILoadpathEntry[] resolvedClasspath, ObjectVector accumulatedRoots, HashSet rootIDs, ILoadpathEntry referringEntry, boolean checkExistency, boolean retrieveExportedRoots, Map rootToResolvedEntries) throws RubyModelException {

		if (referringEntry == null) {
			rootIDs.add(rootID());
		}
		for (int i = 0, length = resolvedClasspath.length; i < length; i++) {
			computeSourceFolderRoots(resolvedClasspath[i], accumulatedRoots, rootIDs, referringEntry, checkExistency, retrieveExportedRoots, rootToResolvedEntries);
		}
	}

	/**
	 * Returns the package fragment roots identified by the given entry. In case
	 * it refers to a project, it will follow its classpath so as to find
	 * exported roots as well. Only works with resolved entry
	 * 
	 * @param resolvedEntry
	 *            IClasspathEntry
	 * @param accumulatedRoots
	 *            ObjectVector
	 * @param rootIDs
	 *            HashSet
	 * @param referringEntry
	 *            the CP entry (project) referring to this entry, or null if
	 *            initial project
	 * @param checkExistency
	 *            boolean
	 * @param retrieveExportedRoots
	 *            boolean
	 * @throws RubyModelException
	 */
	public void computeSourceFolderRoots(ILoadpathEntry resolvedEntry, ObjectVector accumulatedRoots, HashSet rootIDs, ILoadpathEntry referringEntry, boolean checkExistency, boolean retrieveExportedRoots, Map rootToResolvedEntries) throws RubyModelException {

		String rootID = ((LoadpathEntry) resolvedEntry).rootID();
		if (rootIDs.contains(rootID))
			return;

		IPath projectPath = this.project.getFullPath();
		IPath entryPath = resolvedEntry.getPath();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		ISourceFolderRoot root = null;

		switch (resolvedEntry.getEntryKind()) {

		// source folder
		case ILoadpathEntry.CPE_SOURCE:

			if (projectPath.isPrefixOf(entryPath)) {
				if (checkExistency) {
					Object target = RubyModel.getTarget(workspaceRoot, entryPath, checkExistency);
					if (target == null)
						return;

					if (target instanceof IFolder || target instanceof IProject) {
						root = getSourceFolderRoot((IResource) target);
					}
				} else {
					root = getFolderSourceFolderRoot(entryPath);
				}
			}
			break;

		// internal/external JAR or folder
		case ILoadpathEntry.CPE_LIBRARY:

			if (referringEntry != null && !resolvedEntry.isExported())
				return;

			if (checkExistency) {
				Object target = RubyModel.getTarget(workspaceRoot, entryPath, checkExistency);
				if (target == null)
					return;

				if (target instanceof IResource) {
					// internal target
					root = getSourceFolderRoot((IResource) target);
				} else {
					// external target
					if (RubyModel.isFolder(target)) {
						root = new ExternalSourceFolderRoot(entryPath, this);
					}
				}
			} else {
				root = getSourceFolderRoot(entryPath);
			}
			break;

		// recurse into required project
		case ILoadpathEntry.CPE_PROJECT:

			if (!retrieveExportedRoots)
				return;
			if (referringEntry != null && !resolvedEntry.isExported())
				return;

			IResource member = workspaceRoot.findMember(entryPath);
			if (member != null && member.getType() == IResource.PROJECT) {// double
																			// check
																			// if
																			// bound
																			// to
																			// project
																			// (23977)
				IProject requiredProjectRsc = (IProject) member;
				if (RubyProject.hasRubyNature(requiredProjectRsc)) { // special
																		// builder
																		// binary
																		// output
					rootIDs.add(rootID);
					RubyProject requiredProject = (RubyProject) RubyCore.create(requiredProjectRsc);
					requiredProject.computeSourceFolderRoots(requiredProject.getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																																		 * don't
																																		 * generateMarkerOnError
																																		 */, false/*
																																											 * don't
																																											 * returnResolutionInProgress
																																											 */), accumulatedRoots, rootIDs, rootToResolvedEntries == null ? resolvedEntry : ((LoadpathEntry) resolvedEntry).combineWith((LoadpathEntry) referringEntry), // only
																																																																																											// combine
																																																																																											// if
																																																																																											// need
																																																																																											// to
																																																																																											// build
																																																																																											// the
																																																																																											// reverse
																																																																																											// map
							checkExistency, retrieveExportedRoots, rootToResolvedEntries);
				}
				break;
			}
		}
		if (root != null) {
			accumulatedRoots.add(root);
			rootIDs.add(rootID);
			if (rootToResolvedEntries != null)
				rootToResolvedEntries.put(root, ((LoadpathEntry) resolvedEntry).combineWith((LoadpathEntry) referringEntry));
		}
	}

	/**
	 * @param path
	 *            IPath
	 * @return A handle to the package fragment root identified by the given
	 *         path. This method is handle-only and the element may or may not
	 *         exist. Returns <code>null</code> if unable to generate a handle
	 *         from the path (for example, an absolute path that has less than 1
	 *         segment. The path may be relative or absolute.
	 */
	public ISourceFolderRoot getSourceFolderRoot(IPath path) {
		if (!path.isAbsolute()) {
			path = getPath().append(path);
		}
		int segmentCount = path.segmentCount();
		switch (segmentCount) {
		case 0:
			return null;
		case 1:
			if (path.equals(getPath())) { // see
											// https://bugs.eclipse.org/bugs/show_bug.cgi?id=75814
				// default root
				return getSourceFolderRoot(this.project);
			}
		default:
			if (segmentCount == 1) {
				// lib being another project
				return getSourceFolderRoot(this.project.getWorkspace().getRoot().getProject(path.lastSegment()));
			} else {
				// lib being a folder
				return getSourceFolderRoot(this.project.getWorkspace().getRoot().getFolder(path));
			}
		}
	}

	public boolean contains(IResource resource) {
		// XXX Check the paths to see if this is true or not!
		return true;
	}

	/**
	 * Answers an ID which is used to distinguish project/entries during package
	 * fragment root computations
	 * 
	 * @return String
	 */
	public String rootID() {
		return "[PRJ]" + this.project.getFullPath(); //$NON-NLS-1$
	}

	/**
	 * Returns a new element info for this element.
	 */
	protected Object createElementInfo() {
		return new RubyProjectElementInfo();
	}

	/**
	 * @see org.rubypeople.rdt.core.IRubyProject#getOption(String, boolean)
	 */
	public String getOption(String optionName, boolean inheritRubyCoreOptions) {

		String propertyName = optionName;
		if (RubyModelManager.getRubyModelManager().optionNames.contains(propertyName)) {
			IEclipsePreferences projectPreferences = getEclipsePreferences();
			String javaCoreDefault = inheritRubyCoreOptions ? RubyCore.getOption(propertyName) : null;
			if (projectPreferences == null)
				return javaCoreDefault;
			String value = projectPreferences.get(propertyName, javaCoreDefault);
			return value == null ? null : value.trim();
		}
		return null;
	}

	/**
	 * @see org.rubypeople.rdt.core.IRubyProject#getOptions(boolean)
	 */
	public Map getOptions(boolean inheritRubyCoreOptions) {

		// initialize to the defaults from RubyCore options pool
		Map options = inheritRubyCoreOptions ? RubyCore.getOptions() : new Hashtable(5);

		// Get project specific options
		RubyModelManager.PerProjectInfo perProjectInfo = null;
		Hashtable projectOptions = null;
		HashSet optionNames = RubyModelManager.getRubyModelManager().optionNames;
		try {
			perProjectInfo = getPerProjectInfo();
			projectOptions = perProjectInfo.options;
			if (projectOptions == null) {
				// get eclipse preferences
				IEclipsePreferences projectPreferences = getEclipsePreferences();
				if (projectPreferences == null)
					return options; // cannot do better (non-Ruby project)
				// create project options
				String[] propertyNames = projectPreferences.keys();
				projectOptions = new Hashtable(propertyNames.length);
				for (int i = 0; i < propertyNames.length; i++) {
					String propertyName = propertyNames[i];
					String value = projectPreferences.get(propertyName, null);
					if (value != null && optionNames.contains(propertyName)) {
						projectOptions.put(propertyName, value.trim());
					}
				}
				// cache project options
				perProjectInfo.options = projectOptions;
			}
		} catch (RubyModelException jme) {
			projectOptions = new Hashtable();
		} catch (BackingStoreException e) {
			projectOptions = new Hashtable();
		}

		// Inherit from RubyCore options if specified
		if (inheritRubyCoreOptions) {
			Iterator propertyNames = projectOptions.keySet().iterator();
			while (propertyNames.hasNext()) {
				String propertyName = (String) propertyNames.next();
				String propertyValue = (String) projectOptions.get(propertyName);
				if (propertyValue != null && optionNames.contains(propertyName)) {
					options.put(propertyName, propertyValue.trim());
				}
			}
			return options;
		}
		return projectOptions;
	}

	/*
	 * Update eclipse preferences from old preferences.
	 */
	private void updatePreferences(IEclipsePreferences preferences) {

		Preferences oldPreferences = loadPreferences();
		if (oldPreferences != null) {
			String[] propertyNames = oldPreferences.propertyNames();
			for (int i = 0; i < propertyNames.length; i++) {
				String propertyName = propertyNames[i];
				String propertyValue = oldPreferences.getString(propertyName);
				if (!"".equals(propertyValue)) { //$NON-NLS-1$
					preferences.put(propertyName, propertyValue);
				}
			}
			try {
				// save immediately old preferences
				preferences.flush();
			} catch (BackingStoreException e) {
				// fails silently
			}
		}
	}

	/**
	 * load preferences from a shareable format (VCM-wise)
	 */
	private Preferences loadPreferences() {

		Preferences preferences = new Preferences();
		IPath projectMetaLocation = getPluginWorkingLocation();
		if (projectMetaLocation != null) {
			File prefFile = projectMetaLocation.append(PREF_FILENAME).toFile();
			if (prefFile.exists()) { // load preferences from file
				InputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(prefFile));
					preferences.load(in);
				} catch (IOException e) { // problems loading preference store
											// - quietly ignore
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) { // ignore problems with
													// close
						}
					}
				}
				// one shot read, delete old preferences
				prefFile.delete();
				return preferences;
			}
		}
		return null;
	}

	/*
	 * Resets this project's caches
	 */
	public void resetCaches() {
		RubyProjectElementInfo info = (RubyProjectElementInfo) RubyModelManager.getRubyModelManager().peekAtInfo(this);
		if (info != null) {
			info.resetCaches();
		}
	}

	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	public Object[] getNonRubyResources() throws RubyModelException {
		return ((RubyProjectElementInfo) getElementInfo()).getNonRubyResources(this);
	}

	public ISourceFolder[] getSourceFolders() throws RubyModelException {
		ISourceFolderRoot[] roots = getSourceFolderRoots();
		return getSourceFoldersInRoots(roots);
	}
	
	/**
	 * Returns all the source folders found in the specified
	 * source folder roots.
	 * @param roots ISourceFolderRoot[]
	 * @return ISourceFolder[]
	 */
	public ISourceFolder[] getSourceFoldersInRoots(ISourceFolderRoot[] roots) {

		ArrayList frags = new ArrayList();
		for (int i = 0; i < roots.length; i++) {
			ISourceFolderRoot root = roots[i];
			try {
				IRubyElement[] rootFragments = root.getChildren();
				for (int j = 0; j < rootFragments.length; j++) {
					frags.add(rootFragments[j]);
				}
			} catch (RubyModelException e) {
				// do nothing
			}
		}
		ISourceFolder[] fragments = new ISourceFolder[frags.size()];
		frags.toArray(fragments);
		return fragments;
	}

	/*
	 * Internal variant allowing to parameterize problem creation/logging
	 */
	public ILoadpathEntry[] getRawLoadpath(boolean createMarkers, boolean logProblems) throws RubyModelException {

		RubyModelManager.PerProjectInfo perProjectInfo = null;
		ILoadpathEntry[] classpath;
		if (createMarkers) {
			this.flushLoadpathProblemMarkers(false/* cycle */, true/* format */);
			classpath = this.readLoadpathFile(createMarkers, logProblems);
		} else {
			perProjectInfo = getPerProjectInfo();
			classpath = perProjectInfo.rawLoadpath;
			if (classpath != null)
				return classpath;
			classpath = this.readLoadpathFile(createMarkers, logProblems);
		}
		if (classpath == null) {
			return defaultLoadpath();
		}
		/*
		 * Disable validate: classpath can contain CP variables and container
		 * that need to be resolved if (classpath != INVALID_CLASSPATH &&
		 * !JavaConventions.validateClasspath(this, classpath,
		 * outputLocation).isOK()) { classpath = INVALID_CLASSPATH; }
		 */
		if (!createMarkers) {
			perProjectInfo.rawLoadpath = classpath;
			perProjectInfo.outputLocation = null;
		}
		return classpath;
	}

	/**
	 * Returns a default load path. This is the root of the project
	 */
	protected ILoadpathEntry[] defaultLoadpath() {

		return new ILoadpathEntry[] { RubyCore.newSourceEntry(this.project.getFullPath()) };
	}
	
	/**
	 * @see IRubyProject
	 */
	public ILoadpathEntry[] readRawLoadpath() {
		// Read loadpath file without creating markers nor logging problems
		return this.readLoadpathFile(false, false);
	}

	/**
	 * Reads the .classpath file from disk and returns the list of entries it
	 * contains (including output location entry) Returns null if .classfile is
	 * not present. Returns INVALID_CLASSPATH if it has a format problem.
	 */
	protected ILoadpathEntry[] readLoadpathFile(boolean createMarker, boolean logProblems) {
		return readLoadpathFile(createMarker, logProblems, null/*
																 * not
																 * interested in
																 * unknown
																 * elements
																 */);
	}

	protected ILoadpathEntry[] readLoadpathFile(boolean createMarker, boolean logProblems, Map unknownElements) {

		try {
			String xmlClasspath = getSharedProperty(LOADPATH_FILENAME);
			if (xmlClasspath == null) {
				if (createMarker && this.project.isAccessible()) {
					this.createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_cannotReadClasspathFile, this.getElementName())));
				}
				return null;
			}
			return decodeLoadpath(xmlClasspath, createMarker, logProblems, unknownElements);
		} catch (CoreException e) {
			// file does not exist (or not accessible)
			if (createMarker && this.project.isAccessible()) {
				this.createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_cannotReadClasspathFile, this.getElementName())));
			}
			if (logProblems) {
				Util.log(e, "Exception while retrieving " + this.getPath() //$NON-NLS-1$
						+ "/.loadpath, will revert to default loadpath"); //$NON-NLS-1$
			}
		}
		return null;
	}

	/**
	 * Reads and decode an XML classpath string
	 */
	protected ILoadpathEntry[] decodeLoadpath(String xmlClasspath, boolean createMarker, boolean logProblems, Map unknownElements) {

		ArrayList paths = new ArrayList();
		ILoadpathEntry defaultOutput = null;
		try {
			if (xmlClasspath == null)
				return null;
			StringReader reader = new StringReader(xmlClasspath);
			Element cpElement;

			try {
				DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
			} catch (SAXException e) {
				throw new IOException(Messages.file_badFormat);
			} catch (ParserConfigurationException e) {
				throw new IOException(Messages.file_badFormat);
			} finally {
				reader.close();
			}

			if (!cpElement.getNodeName().equalsIgnoreCase(LoadpathEntry.TAG_LOADPATH)) {
				throw new IOException(Messages.file_badFormat);
			}
			NodeList list = cpElement.getElementsByTagName(LoadpathEntry.TAG_LOADPATHENTRY);
			int length = list.getLength();

			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					ILoadpathEntry entry = LoadpathEntry.elementDecode((Element) node, this, unknownElements);
					if (entry != null) {
						paths.add(entry);

					}
				}
			}
		} catch (IOException e) {
			// bad format
			if (createMarker && this.project.isAccessible()) {
				this.createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_xmlFormatError, new String[] { this.getElementName(), e.getMessage() })));
			}
			if (logProblems) {
				Util.log(e, "Exception while retrieving " + this.getPath() //$NON-NLS-1$
						+ "/.classpath, will mark classpath as invalid"); //$NON-NLS-1$
			}
			return INVALID_LOADPATH;
		} catch (AssertionFailedException e) {
			// failed creating CP entries from file
			if (createMarker && this.project.isAccessible()) {
				this.createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_illegalEntryInClasspathFile, new String[] { this.getElementName(), e.getMessage() })));
			}
			if (logProblems) {
				Util.log(e, "Exception while retrieving " + this.getPath() //$NON-NLS-1$
						+ "/.classpath, will mark classpath as invalid"); //$NON-NLS-1$
			}
			return INVALID_LOADPATH;
		}
		// return an empty classpath is it size is 0, to differenciate from a
		// null classpath
		int pathSize = paths.size();
		ILoadpathEntry[] entries = new ILoadpathEntry[pathSize + (defaultOutput == null ? 0 : 1)];
		paths.toArray(entries);
		if (defaultOutput != null)
			entries[pathSize] = defaultOutput; // ensure output is last item
		return entries;
	}

	/**
	 * Retrieve a shared property on a project. If the property is not defined,
	 * answers null. Note that it is orthogonal to IResource persistent
	 * properties, and client code has to decide which form of storage to use
	 * appropriately. Shared properties produce real resource files which can be
	 * shared through a VCM onto a server. Persistent properties are not
	 * shareable.
	 * 
	 * @param key
	 *            String
	 * @see JavaProject#setSharedProperty(String, String)
	 * @return String
	 * @throws CoreException
	 */
	public String getSharedProperty(String key) throws CoreException {

		String property = null;
		IFile rscFile = this.project.getFile(key);
		if (rscFile.exists()) {
			byte[] bytes = Util.getResourceContentsAsByteArray(rscFile);
			try {
				property = new String(bytes, org.rubypeople.rdt.core.util.Util.UTF_8); // .classpath
																									// always
																									// encoded
																									// with
																									// UTF-8
			} catch (UnsupportedEncodingException e) {
				Util.log(e, "Could not read .classpath with UTF-8 encoding"); //$NON-NLS-1$
				// fallback to default
				property = new String(bytes);
			}
		} else {
			// when a project is imported, we get a first delta for the addition
			// of the .project, but the .classpath is not accessible
			// so default to using java.io.File
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=96258
			URI location = rscFile.getLocationURI();
			if (location != null) {
				File file = Util.toLocalFile(location, null/*
															 * no progress
															 * monitor available
															 */);
				if (file != null && file.exists()) {
					byte[] bytes;
					try {
						bytes = org.rubypeople.rdt.core.util.Util.getFileByteContent(file);
					} catch (IOException e) {
						return null;
					}
					try {
						property = new String(bytes, org.rubypeople.rdt.core.util.Util.UTF_8); // .classpath
																											// always
																											// encoded
																											// with
																											// UTF-8
					} catch (UnsupportedEncodingException e) {
						Util.log(e, "Could not read .classpath with UTF-8 encoding"); //$NON-NLS-1$
						// fallback to default
						property = new String(bytes);
					}
				}
			}
		}
		return property;
	}

	/**
	 * @see IRubyProject
	 */
	public ILoadpathEntry[] getResolvedLoadpath(boolean ignoreUnresolvedEntry, boolean generateMarkerOnError) throws RubyModelException {

		return getResolvedLoadpath(ignoreUnresolvedEntry, generateMarkerOnError, true // returnResolutionInProgress
		);
	}

	public ILoadpathEntry[] getResolvedLoadpath(boolean ignoreUnresolvedEntry, boolean generateMarkerOnError, boolean returnResolutionInProgress) throws RubyModelException {
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		RubyModelManager.PerProjectInfo perProjectInfo = null;
		if (ignoreUnresolvedEntry && !generateMarkerOnError) {
			perProjectInfo = getPerProjectInfo();
			if (perProjectInfo != null) {
				// resolved path is cached on its info
				ILoadpathEntry[] infoPath = perProjectInfo.resolvedLoadpath;
				if (infoPath != null) {
					return infoPath;
				} else if (returnResolutionInProgress && manager.isLoadpathBeingResolved(this)) {
					if (RubyModelManager.CP_RESOLVE_VERBOSE) {
						Util.verbose("CPResolution: reentering raw loadpath resolution, will use empty loadpath instead" + //$NON-NLS-1$
								"	project: " + getElementName() + '\n' + //$NON-NLS-1$
								"	invocation stack trace:"); //$NON-NLS-1$
						new Exception("<Fake exception>").printStackTrace(System.out); //$NON-NLS-1$
					}
					return RESOLUTION_IN_PROGRESS;
				}
			}
		}
		Map rawReverseMap = perProjectInfo == null ? null : new HashMap(5);
		ILoadpathEntry[] resolvedPath = null;
		boolean nullOldResolvedCP = perProjectInfo != null && perProjectInfo.resolvedLoadpath == null;
		try {
			// protect against misbehaving clients (see
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=61040)
			if (nullOldResolvedCP)
				manager.setLoadpathBeingResolved(this, true);
			resolvedPath = getResolvedLoadpath(getRawLoadpath(generateMarkerOnError, !generateMarkerOnError), null, ignoreUnresolvedEntry, generateMarkerOnError, rawReverseMap);
		} finally {
			if (nullOldResolvedCP)
				perProjectInfo.resolvedLoadpath = null;
		}

		if (perProjectInfo != null) {
			if (perProjectInfo.rawLoadpath == null // .loadpath file could not
													// be read
					&& generateMarkerOnError && RubyProject.hasRubyNature(this.project)) {
				// flush .loadpath format markers (bug 39877), but only when
				// file cannot be read (bug 42366)
				this.flushLoadpathProblemMarkers(false, true);
				this.createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_cannotReadClasspathFile, this.getElementName())));
			}

			perProjectInfo.resolvedLoadpath = resolvedPath;
			perProjectInfo.resolvedPathToRawEntries = rawReverseMap;
			manager.setLoadpathBeingResolved(this, false);
		}
		return resolvedPath;
	}

	/**
	 * Internal variant which can process any arbitrary classpath
	 * 
	 * @param classpathEntries
	 *            IClasspathEntry[]
	 * @param projectOutputLocation
	 *            IPath
	 * @param ignoreUnresolvedEntry
	 *            boolean
	 * @param generateMarkerOnError
	 *            boolean
	 * @param rawReverseMap
	 *            Map
	 * @return IClasspathEntry[]
	 * @throws JavaModelException
	 */
	public ILoadpathEntry[] getResolvedLoadpath(ILoadpathEntry[] classpathEntries, IPath projectOutputLocation, // only
																												// set
																												// if
																												// needing
																												// full
																												// classpath
																												// validation
																												// (and
																												// markers)
			boolean ignoreUnresolvedEntry, // if unresolved entries are met,
											// should it trigger initializations
			boolean generateMarkerOnError, Map rawReverseMap) // can be null
																// if not
																// interested in
																// reverse
																// mapping
			throws RubyModelException {

		IRubyModelStatus status;
		if (generateMarkerOnError) {
			flushLoadpathProblemMarkers(false, false);
		}

		int length = classpathEntries.length;
		ArrayList resolvedEntries = new ArrayList();

		for (int i = 0; i < length; i++) {

			ILoadpathEntry rawEntry = classpathEntries[i];
			IPath resolvedPath;
			status = null;

			/* validation if needed */
			if (generateMarkerOnError || !ignoreUnresolvedEntry) {
				status = LoadpathEntry.validateLoadpathEntry(this, rawEntry, false /*
																					 * ignore
																					 * src
																					 * attach
																					 */, false /*
																												 * do
																												 * not
																												 * recurse
																												 * in
																												 * containers,
																												 * done
																												 * later
																												 * to
																												 * accumulate
																												 */);
				if (generateMarkerOnError && !status.isOK()) {
					if (status.getCode() == IRubyModelStatusConstants.INVALID_CLASSPATH && ((LoadpathEntry) rawEntry).isOptional())
						continue; // ignore this entry
					createLoadpathProblemMarker(status);
				}
			}

			switch (rawEntry.getEntryKind()) {

			case ILoadpathEntry.CPE_VARIABLE:

				ILoadpathEntry resolvedEntry = null;
				try {
					resolvedEntry = RubyCore.getResolvedLoadpathEntry(rawEntry);
				} catch (AssertionFailedException e) {
					// Catch the assertion failure and throw java model
					// exception instead
					// see bug
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=55992
					// if ignoredUnresolvedEntry is false, status is set by by
					// ClasspathEntry.validateClasspathEntry
					// called above as validation was needed
					if (!ignoreUnresolvedEntry)
						throw new RubyModelException(status);
				}
				if (resolvedEntry == null) {
					if (!ignoreUnresolvedEntry)
						throw new RubyModelException(status);
				} else {
					if (rawReverseMap != null) {
						if (rawReverseMap.get(resolvedPath = resolvedEntry.getPath()) == null)
							rawReverseMap.put(resolvedPath, rawEntry);
					}
					resolvedEntries.add(resolvedEntry);
				}
				break;

			case ILoadpathEntry.CPE_CONTAINER:

				ILoadpathContainer container = RubyCore.getLoadpathContainer(rawEntry.getPath(), this);
				if (container == null) {
					if (!ignoreUnresolvedEntry)
						throw new RubyModelException(status);
					break;
				}

				ILoadpathEntry[] containerEntries = container.getLoadpathEntries();
				if (containerEntries == null)
					break;

				// container was bound
				for (int j = 0, containerLength = containerEntries.length; j < containerLength; j++) {
					LoadpathEntry cEntry = (LoadpathEntry) containerEntries[j];
					if (generateMarkerOnError) {
						IRubyModelStatus containerStatus = LoadpathEntry.validateLoadpathEntry(this, cEntry, false, true /* recurse */);
						if (!containerStatus.isOK())
							createLoadpathProblemMarker(containerStatus);
					}
					// if container is exported or restricted, then its nested
					// entries must in turn be exported (21749) and/or propagate
					// restrictions
					cEntry = cEntry.combineWith((LoadpathEntry) rawEntry);
					if (rawReverseMap != null) {
						if (rawReverseMap.get(resolvedPath = cEntry.getPath()) == null)
							rawReverseMap.put(resolvedPath, rawEntry);
					}
					resolvedEntries.add(cEntry);
				}
				break;

			default:

				if (rawReverseMap != null) {
					if (rawReverseMap.get(resolvedPath = rawEntry.getPath()) == null)
						rawReverseMap.put(resolvedPath, rawEntry);
				}
				resolvedEntries.add(rawEntry);

			}
		}

		ILoadpathEntry[] resolvedPath = new ILoadpathEntry[resolvedEntries.size()];
		resolvedEntries.toArray(resolvedPath);

		if (generateMarkerOnError && projectOutputLocation != null) {
			status = LoadpathEntry.validateLoadpath(this, resolvedPath, projectOutputLocation);
			if (!status.isOK())
				createLoadpathProblemMarker(status);
		}
		return resolvedPath;
	}

	public ISourceFolderRoot getFolderSourceFolderRoot(IPath path) {
		if (path.segmentCount() == 1) { // default project root
			return getSourceFolderRoot(this.project);
		}
		return getSourceFolderRoot(this.project.getWorkspace().getRoot().getFolder(path));
	}

	public ISourceFolderRoot getSourceFolderRoot(IResource resource) {
		switch (resource.getType()) {
		case IResource.FILE:
			return null;
		case IResource.FOLDER:
			return new SourceFolderRoot(resource, this);
		case IResource.PROJECT:
			return new SourceFolderRoot(resource, this);
		default:
			return null;
		}
	}

	/**
	 * Record a new marker denoting a classpath problem
	 */
	void createLoadpathProblemMarker(IRubyModelStatus status) {

		IMarker marker = null;
		int severity;
		String[] arguments = new String[0];
		boolean isCycleProblem = false, isClasspathFileFormatProblem = false;
		switch (status.getCode()) {

		case IRubyModelStatusConstants.CLASSPATH_CYCLE:
			isCycleProblem = true;
			if (RubyCore.ERROR.equals(getOption(RubyCore.CORE_CIRCULAR_CLASSPATH, true))) {
				severity = IMarker.SEVERITY_ERROR;
			} else {
				severity = IMarker.SEVERITY_WARNING;
			}
			break;

		case IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT:
			isClasspathFileFormatProblem = true;
			severity = IMarker.SEVERITY_ERROR;
			break;

		case IRubyModelStatusConstants.INCOMPATIBLE_JDK_LEVEL:
			String setting = getOption(RubyCore.CORE_INCOMPATIBLE_JDK_LEVEL, true);
			if (RubyCore.ERROR.equals(setting)) {
				severity = IMarker.SEVERITY_ERROR;
			} else if (RubyCore.WARNING.equals(setting)) {
				severity = IMarker.SEVERITY_WARNING;
			} else {
				return; // setting == IGNORE
			}
			break;

		default:
			IPath path = status.getPath();
			if (path != null)
				arguments = new String[] { path.toString() };
			if (RubyCore.ERROR.equals(getOption(RubyCore.CORE_INCOMPLETE_CLASSPATH, true))) {
				severity = IMarker.SEVERITY_ERROR;
			} else {
				severity = IMarker.SEVERITY_WARNING;
			}
			break;
		}

		try {
			marker = this.project.createMarker(IRubyModelMarker.BUILDPATH_PROBLEM_MARKER);
			marker.setAttributes(new String[] { IMarker.MESSAGE, IMarker.SEVERITY, IMarker.LOCATION, IRubyModelMarker.CYCLE_DETECTED, IRubyModelMarker.CLASSPATH_FILE_FORMAT, IRubyModelMarker.ID, IRubyModelMarker.ARGUMENTS, IRubyModelMarker.CATEGORY_ID, }, new Object[] { status.getMessage(), new Integer(severity), Messages.classpath_buildPath, isCycleProblem ? "true" : "false",//$NON-NLS-1$ //$NON-NLS-2$
					isClasspathFileFormatProblem ? "true" : "false",//$NON-NLS-1$ //$NON-NLS-2$
					new Integer(status.getCode()), Util.getProblemArgumentsForMarker(arguments), new Integer(CategorizedProblem.CAT_BUILDPATH) });
		} catch (CoreException e) {
			// could not create marker: cannot do much
			if (RubyModelManager.VERBOSE) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Remove all markers denoting classpath problems
	 */
	// TODO (philippe) should improve to use a bitmask instead of booleans
	// (CYCLE, FORMAT, VALID)
	protected void flushLoadpathProblemMarkers(boolean flushCycleMarkers, boolean flushClasspathFormatMarkers) {
		try {
			if (this.project.isAccessible()) {
				IMarker[] markers = this.project.findMarkers(IRubyModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
				for (int i = 0, length = markers.length; i < length; i++) {
					IMarker marker = markers[i];
					if (flushCycleMarkers && flushClasspathFormatMarkers) {
						marker.delete();
					} else {
						String cycleAttr = (String) marker.getAttribute(IRubyModelMarker.CYCLE_DETECTED);
						String classpathFileFormatAttr = (String) marker.getAttribute(IRubyModelMarker.CLASSPATH_FILE_FORMAT);
						if ((flushCycleMarkers == (cycleAttr != null && cycleAttr.equals("true"))) //$NON-NLS-1$
								&& (flushClasspathFormatMarkers == (classpathFileFormatAttr != null && classpathFileFormatAttr.equals("true")))) { //$NON-NLS-1$
							marker.delete();
						}
					}
				}
			}
		} catch (CoreException e) {
			// could not flush markers: not much we can do
			if (RubyModelManager.VERBOSE) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns a canonicalized path from the given external path. Note that the
	 * return path contains the same number of segments and it contains a device
	 * only if the given path contained one.
	 * 
	 * @param externalPath
	 *            IPath
	 * @see java.io.File for the definition of a canonicalized path
	 * @return IPath
	 */
	public static IPath canonicalizedPath(IPath externalPath) {

		if (externalPath == null)
			return null;

		// if (JavaModelManager.VERBOSE) {
		// System.out.println("JAVA MODEL - Canonicalizing " +
		// externalPath.toString());
		// }

		if (IS_CASE_SENSITIVE) {
			// if (JavaModelManager.VERBOSE) {
			// System.out.println("JAVA MODEL - Canonical path is original path
			// (file system is case sensitive)");
			// }
			return externalPath;
		}

		// if not external path, return original path
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null)
			return externalPath; // protection during shutdown (30487)
		if (workspace.getRoot().findMember(externalPath) != null) {
			// if (JavaModelManager.VERBOSE) {
			// System.out.println("JAVA MODEL - Canonical path is original path
			// (member of workspace)");
			// }
			return externalPath;
		}

		IPath canonicalPath = null;
		try {
			canonicalPath = new Path(new File(externalPath.toOSString()).getCanonicalPath());
		} catch (IOException e) {
			// default to original path
			// if (JavaModelManager.VERBOSE) {
			// System.out.println("JAVA MODEL - Canonical path is original path
			// (IOException)");
			// }
			return externalPath;
		}

		IPath result;
		int canonicalLength = canonicalPath.segmentCount();
		if (canonicalLength == 0) {
			// the java.io.File canonicalization failed
			// if (JavaModelManager.VERBOSE) {
			// System.out.println("JAVA MODEL - Canonical path is original path
			// (canonical path is empty)");
			// }
			return externalPath;
		} else if (externalPath.isAbsolute()) {
			result = canonicalPath;
		} else {
			// if path is relative, remove the first segments that were added by
			// the java.io.File canonicalization
			// e.g. 'lib/classes.zip' was converted to
			// 'd:/myfolder/lib/classes.zip'
			int externalLength = externalPath.segmentCount();
			if (canonicalLength >= externalLength) {
				result = canonicalPath.removeFirstSegments(canonicalLength - externalLength);
			} else {
				// if (JavaModelManager.VERBOSE) {
				// System.out.println("JAVA MODEL - Canonical path is original
				// path (canonical path is " + canonicalPath.toString() + ")");
				// }
				return externalPath;
			}
		}

		// keep device only if it was specified (this is because
		// File.getCanonicalPath() converts '/lib/classed.zip' to
		// 'd:/lib/classes/zip')
		if (externalPath.getDevice() == null) {
			result = result.setDevice(null);
		}
		// if (JavaModelManager.VERBOSE) {
		// System.out.println("JAVA MODEL - Canonical path is " +
		// result.toString());
		// }
		return result;
	}

	/**
	 * @see IRubyProject
	 */
	public ILoadpathEntry[] getRawLoadpath() throws RubyModelException {
		// Do not create marker but log problems while getting raw loadpath
		return getRawLoadpath(false, true);
	}

	public ISourceFolderRoot[] getSourceFolderRoots() throws RubyModelException {
		Object[] children;
		int length;
		ISourceFolderRoot[] roots;

		System.arraycopy(children = getChildren(), 0, roots = new ISourceFolderRoot[length = children.length], 0, length);

		return roots;
	}

	public boolean isOnLoadpath(IRubyElement element) {
		ILoadpathEntry[] rawClasspath;
		try {
			rawClasspath = getRawLoadpath();
		} catch (RubyModelException e) {
			return false; // not a Ruby project
		}
		int elementType = element.getElementType();
		boolean isPackageFragmentRoot = false;
		boolean isFolderPath = false;
		boolean isSource = false;
		switch (elementType) {
		case IRubyElement.RUBY_MODEL:
			return false;
		case IRubyElement.RUBY_PROJECT:
			break;
		case IRubyElement.SOURCE_FOLDER_ROOT:
			isPackageFragmentRoot = true;
			break;
		case IRubyElement.SOURCE_FOLDER:
			isFolderPath = !((ISourceFolderRoot) element.getParent()).isArchive();
			break;
		case IRubyElement.SCRIPT:
			isSource = true;
			break;
		default:
			isSource = element.getAncestor(IRubyElement.SCRIPT) != null;
			break;
		}
		IPath elementPath = element.getPath();

		// first look at unresolved entries
		int length = rawClasspath.length;
		for (int i = 0; i < length; i++) {
			ILoadpathEntry entry = rawClasspath[i];
			switch (entry.getEntryKind()) {
			case ILoadpathEntry.CPE_LIBRARY:
			case ILoadpathEntry.CPE_PROJECT:
			case ILoadpathEntry.CPE_SOURCE:
				if (isOnLoadpathEntry(elementPath, isFolderPath, isPackageFragmentRoot, entry))
					return true;
				break;
			}
		}

		// no need to go further for compilation units and elements inside a
		// compilation unit
		// it can only be in a source folder, thus on the raw classpath
		if (isSource)
			return false;

		// then look at resolved entries
		for (int i = 0; i < length; i++) {
			ILoadpathEntry rawEntry = rawClasspath[i];
			switch (rawEntry.getEntryKind()) {
			case ILoadpathEntry.CPE_CONTAINER:
				ILoadpathContainer container;
				try {
					container = RubyCore.getLoadpathContainer(rawEntry.getPath(), this);
				} catch (RubyModelException e) {
					break;
				}
				if (container == null)
					break;
				ILoadpathEntry[] containerEntries = container.getLoadpathEntries();
				if (containerEntries == null)
					break;
				// container was bound
				for (int j = 0, containerLength = containerEntries.length; j < containerLength; j++) {
					ILoadpathEntry resolvedEntry = containerEntries[j];
					if (isOnLoadpathEntry(elementPath, isFolderPath, isPackageFragmentRoot, resolvedEntry))
						return true;
				}
				break;
			case ILoadpathEntry.CPE_VARIABLE:
				ILoadpathEntry resolvedEntry = RubyCore.getResolvedLoadpathEntry(rawEntry);
				if (resolvedEntry == null)
					break;
				if (isOnLoadpathEntry(elementPath, isFolderPath, isPackageFragmentRoot, resolvedEntry))
					return true;
				break;
			}
		}

		return false;
	}

	private boolean isOnLoadpathEntry(IPath elementPath, boolean isFolderPath, boolean isPackageFragmentRoot, ILoadpathEntry entry) {
		IPath entryPath = entry.getPath();
		if (isPackageFragmentRoot) {
			// package fragment roots must match exactly entry pathes (no
			// exclusion there)
			if (entryPath.equals(elementPath))
				return true;
		} else {
			if (entryPath.isPrefixOf(elementPath) && !Util.isExcluded(elementPath, ((LoadpathEntry) entry).fullInclusionPatternChars(), ((LoadpathEntry) entry).fullExclusionPatternChars(), isFolderPath))
				return true;
		}
		return false;
	}

	public ILoadpathEntry[] getResolvedLoadpath(boolean ignoreUnresolvedEntry) throws RubyModelException {
		return getResolvedLoadpath(ignoreUnresolvedEntry, false, // don't
				// generateMarkerOnError
				true // returnResolutionInProgress
		);
	}

	public ISourceFolderRoot[] computeSourceFolderRoots(ILoadpathEntry resolvedEntry) {
		try {
			return computeSourceFolderRoots(new ILoadpathEntry[] { resolvedEntry }, false, // don't
					// retrieve
					// exported
					// roots
					null /* no reverse map */
			);
		} catch (RubyModelException e) {
			return new ISourceFolderRoot[] {};
		}
	}

	public ILoadpathEntry[] getExpandedLoadpath(boolean ignoreUnresolvedVariable) throws RubyModelException {

		return getExpandedLoadpath(ignoreUnresolvedVariable, false/*
																	 * don't
																	 * create
																	 * markers
																	 */, null, null);

	}

	private ILoadpathEntry[] getExpandedLoadpath(boolean ignoreUnresolvedVariable, boolean generateMarkerOnError, Map preferredClasspaths, Map preferredOutputs) throws RubyModelException {

		ObjectVector accumulatedEntries = new ObjectVector();
		computeExpandedLoadpath(null, ignoreUnresolvedVariable, generateMarkerOnError, new HashSet(5), accumulatedEntries, preferredClasspaths, preferredOutputs);

		ILoadpathEntry[] expandedPath = new ILoadpathEntry[accumulatedEntries.size()];
		accumulatedEntries.copyInto(expandedPath);

		return expandedPath;
	}

	private void computeExpandedLoadpath(LoadpathEntry referringEntry, boolean ignoreUnresolvedVariable, boolean generateMarkerOnError, HashSet rootIDs, ObjectVector accumulatedEntries, Map preferredClasspaths, Map preferredOutputs) throws RubyModelException {

		String projectRootId = this.rootID();
		if (rootIDs.contains(projectRootId)) {
			return; // break cycles if any
		}
		rootIDs.add(projectRootId);

		ILoadpathEntry[] preferredClasspath = preferredClasspaths != null ? (ILoadpathEntry[]) preferredClasspaths.get(this) : null;
		IPath preferredOutput = preferredOutputs != null ? (IPath) preferredOutputs.get(this) : null;
		ILoadpathEntry[] immediateClasspath = preferredClasspath != null ? getResolvedLoadpath(preferredClasspath, preferredOutput, ignoreUnresolvedVariable, generateMarkerOnError, null /*
																																															 * no
																																															 * reverse
																																															 * map
																																															 */) : getResolvedLoadpath(ignoreUnresolvedVariable, generateMarkerOnError, false/*
																																																																							 * don't
																																																																							 * returnResolutionInProgress
																																																																							 */);

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		boolean isInitialProject = referringEntry == null;
		for (int i = 0, length = immediateClasspath.length; i < length; i++) {
			LoadpathEntry entry = (LoadpathEntry) immediateClasspath[i];
			if (isInitialProject || entry.isExported()) {
				String rootID = entry.rootID();
				if (rootIDs.contains(rootID)) {
					continue;
				}
				// combine restrictions along the project chain
				LoadpathEntry combinedEntry = entry.combineWith(referringEntry);
				accumulatedEntries.add(combinedEntry);

				// recurse in project to get all its indirect exports (only
				// consider exported entries from there on)
				if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
					IResource member = workspaceRoot.findMember(entry.getPath());
					if (member != null && member.getType() == IResource.PROJECT) { // double
																					// check
																					// if
																					// bound
																					// to
																					// project
																					// (23977)
						IProject projRsc = (IProject) member;
						if (RubyProject.hasRubyNature(projRsc)) {
							RubyProject javaProject = (RubyProject) RubyCore.create(projRsc);
							javaProject.computeExpandedLoadpath(combinedEntry, ignoreUnresolvedVariable, false /*
																												 * no
																												 * marker
																												 * when
																												 * recursing
																												 * in
																												 * prereq
																												 */, rootIDs, accumulatedEntries, preferredClasspaths, preferredOutputs);
						}
					}
				} else {
					rootIDs.add(rootID);
				}
			}
		}
	}

	public void updateSourceFolderRoots() {

		if (this.isOpen()) {
			try {
				RubyProjectElementInfo info = getRubyProjectElementInfo();
				computeChildren(info);
				info.resetCaches(); // discard caches (hold onto roots and pkg
									// fragments)
			} catch (RubyModelException e) {
				try {
					close(); // could not do better
				} catch (RubyModelException ex) {
					// ignore
				}
			}
		}
	}

	/**
	 * Convenience method that returns the specific type of info for a Java
	 * project.
	 */
	protected RubyProjectElementInfo getRubyProjectElementInfo() throws RubyModelException {

		return (RubyProjectElementInfo) getElementInfo();
	}

	/**
	 * Computes the collection of package fragment roots (local ones) and set it
	 * on the given info. Need to check *all* package fragment roots in order to
	 * reset NameLookup
	 * 
	 * @param info
	 *            JavaProjectElementInfo
	 * @throws JavaModelException
	 */
	public void computeChildren(RubyProjectElementInfo info) throws RubyModelException {
		ILoadpathEntry[] classpath = getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																								 * don't
																								 * generateMarkerOnError
																								 */, false/*
																																	 * don't
																																	 * returnResolutionInProgress
																																	 */);
		RubyProjectElementInfo.ProjectCache projectCache = info.projectCache;
		if (projectCache != null) {
			ISourceFolderRoot[] newRoots = computeSourceFolderRoots(classpath, true, null /*
																							 * no
																							 * reverse
																							 * map
																							 */);
			checkIdentical: { // compare all pkg fragment root lists
				ISourceFolderRoot[] oldRoots = projectCache.allPkgFragmentRootsCache;
				if (oldRoots.length == newRoots.length) {
					for (int i = 0, length = oldRoots.length; i < length; i++) {
						if (!oldRoots[i].equals(newRoots[i])) {
							break checkIdentical;
						}
					}
					return; // no need to update
				}
			}
		}
		info.setNonRubyResources(null);
		info.setChildren(computeSourceFolderRoots(classpath, false, null /*
																			 * no
																			 * reverse
																			 * map
																			 */));
	}

	public ISourceFolderRoot[] getAllSourceFolderRoots(Map rootToResolvedEntries) throws RubyModelException {

		return computeSourceFolderRoots(getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																								 * don't
																								 * generateMarkerOnError
																								 */, false/*
																																		 * don't
																																		 * returnResolutionInProgress
																																		 */), true/* retrieveExportedRoots */, rootToResolvedEntries);
	}

	public boolean hasCycleMarker() {
		return this.getCycleMarker() != null;
	}

	/*
	 * Returns the cycle marker associated with this project or null if none.
	 */
	public IMarker getCycleMarker() {
		try {
			if (this.project.isAccessible()) {
				IMarker[] markers = this.project.findMarkers(IRubyModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
				for (int i = 0, length = markers.length; i < length; i++) {
					IMarker marker = markers[i];
					String cycleAttr = (String) marker.getAttribute(IRubyModelMarker.CYCLE_DETECTED);
					if (cycleAttr != null && cycleAttr.equals("true")) { //$NON-NLS-1$
						return marker;
					}
				}
			}
		} catch (CoreException e) {
			// could not get markers: return null
		}
		return null;
	}

	public boolean hasLoadpathCycle(ILoadpathEntry[] preferredClasspath) {
		HashSet cycleParticipants = new HashSet();
		HashMap preferredClasspaths = new HashMap(1);
		preferredClasspaths.put(this, preferredClasspath);
		updateCycleParticipants(new ArrayList(2), cycleParticipants, ResourcesPlugin.getWorkspace().getRoot(), new HashSet(2), preferredClasspaths);
		return !cycleParticipants.isEmpty();
	}

	/**
	 * If a cycle is detected, then cycleParticipants contains all the paths of
	 * projects involved in this cycle (directly and indirectly), no cycle if
	 * the set is empty (and started empty)
	 * 
	 * @param prereqChain
	 *            ArrayList
	 * @param cycleParticipants
	 *            HashSet
	 * @param workspaceRoot
	 *            IWorkspaceRoot
	 * @param traversed
	 *            HashSet
	 * @param preferredClasspaths
	 *            Map
	 */
	public void updateCycleParticipants(ArrayList prereqChain, HashSet cycleParticipants, IWorkspaceRoot workspaceRoot, HashSet traversed, Map preferredClasspaths) {

		IPath path = this.getPath();
		prereqChain.add(path);
		traversed.add(path);
		try {
			ILoadpathEntry[] classpath = null;
			if (preferredClasspaths != null)
				classpath = (ILoadpathEntry[]) preferredClasspaths.get(this);
			if (classpath == null)
				classpath = getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																					 * don't
																					 * generateMarkerOnError
																					 */, false/*
																															 * don't
																															 * returnResolutionInProgress
																															 */);
			for (int i = 0, length = classpath.length; i < length; i++) {
				ILoadpathEntry entry = classpath[i];

				if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
					IPath prereqProjectPath = entry.getPath();
					int index = cycleParticipants.contains(prereqProjectPath) ? 0 : prereqChain.indexOf(prereqProjectPath);
					if (index >= 0) { // refer to cycle, or in cycle itself
						for (int size = prereqChain.size(); index < size; index++) {
							cycleParticipants.add(prereqChain.get(index));
						}
					} else {
						if (!traversed.contains(prereqProjectPath)) {
							IResource member = workspaceRoot.findMember(prereqProjectPath);
							if (member != null && member.getType() == IResource.PROJECT) {
								RubyProject javaProject = (RubyProject) RubyCore.create((IProject) member);
								javaProject.updateCycleParticipants(prereqChain, cycleParticipants, workspaceRoot, traversed, preferredClasspaths);
							}
						}
					}
				}
			}
		} catch (RubyModelException e) {
			// project doesn't exist: ignore
		}
		prereqChain.remove(path);
	}

	/**
	 * Update cycle markers for all java projects
	 * 
	 * @param preferredClasspaths
	 *            Map
	 * @throws JavaModelException
	 */
	public static void updateAllCycleMarkers(Map preferredClasspaths) throws RubyModelException {

		// long start = System.currentTimeMillis();

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] rscProjects = workspaceRoot.getProjects();
		int length = rscProjects.length;
		RubyProject[] projects = new RubyProject[length];

		HashSet cycleParticipants = new HashSet();
		HashSet traversed = new HashSet();

		// compute cycle participants
		ArrayList prereqChain = new ArrayList();
		for (int i = 0; i < length; i++) {
			if (hasRubyNature(rscProjects[i])) {
				RubyProject project = (projects[i] = (RubyProject) RubyCore.create(rscProjects[i]));
				if (!traversed.contains(project.getPath())) {
					prereqChain.clear();
					project.updateCycleParticipants(prereqChain, cycleParticipants, workspaceRoot, traversed, preferredClasspaths);
				}
			}
		}
		// System.out.println("updateAllCycleMarkers: " +
		// (System.currentTimeMillis() - start) + " ms");

		for (int i = 0; i < length; i++) {
			RubyProject project = projects[i];
			if (project != null) {
				if (cycleParticipants.contains(project.getPath())) {
					IMarker cycleMarker = project.getCycleMarker();
					String circularCPOption = project.getOption(RubyCore.CORE_CIRCULAR_CLASSPATH, true);
					int circularCPSeverity = RubyCore.ERROR.equals(circularCPOption) ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING;
					if (cycleMarker != null) {
						// update existing cycle marker if needed
						try {
							int existingSeverity = ((Integer) cycleMarker.getAttribute(IMarker.SEVERITY)).intValue();
							if (existingSeverity != circularCPSeverity) {
								cycleMarker.setAttribute(IMarker.SEVERITY, circularCPSeverity);
							}
						} catch (CoreException e) {
							throw new RubyModelException(e);
						}
					} else {
						// create new marker
						project.createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.CLASSPATH_CYCLE, project));
					}
				} else {
					project.flushLoadpathProblemMarkers(true, false);
				}
			}
		}
	}

	public void setRawLoadpath(ILoadpathEntry[] newEntries, IPath newOutputLocation, IProgressMonitor monitor, boolean canChangeResource, ILoadpathEntry[] oldResolvedPath, boolean needValidation, boolean needSave) throws RubyModelException {
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		try {
			ILoadpathEntry[] newRawPath = newEntries;
			if (newRawPath == null) { // are we already with the default
										// loadpath
				newRawPath = defaultLoadpath();
			}
			SetLoadpathOperation op = new SetLoadpathOperation(this, oldResolvedPath, newRawPath, newOutputLocation, canChangeResource, needValidation, needSave);
			op.runOperation(monitor);

		} catch (RubyModelException e) {
			manager.getDeltaProcessor().flush();
			throw e;
		}
	}

	public boolean saveLoadpath(ILoadpathEntry[] newLoadpath, IPath newOutputLocation) throws RubyModelException {
		if (!this.project.isAccessible())
			return false;

		Map unknownElements = new HashMap();
		ILoadpathEntry[] fileEntries = readLoadpathFile(false /*
																 * don't create
																 * markers
																 */, false/*
																								 * don't
																								 * log
																								 * problems
																								 */, unknownElements);
		if (fileEntries != null && isLoadpathEqualsTo(newLoadpath, newOutputLocation, fileEntries)) {
			// no need to save it, it is the same
			return false;
		}

		// actual file saving
		try {
			setSharedProperty(LOADPATH_FILENAME, encodeLoadpath(newLoadpath, newOutputLocation, true, unknownElements));
			return true;
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}

	/**
	 * Record a shared persistent property onto a project. Note that it is
	 * orthogonal to IResource persistent properties, and client code has to
	 * decide which form of storage to use appropriately. Shared properties
	 * produce real resource files which can be shared through a VCM onto a
	 * server. Persistent properties are not shareable.
	 * 
	 * shared properties end up in resource files, and thus cannot be modified
	 * during delta notifications (a CoreException would then be thrown).
	 * 
	 * @param key
	 *            String
	 * @param value
	 *            String
	 * @see JavaProject#getSharedProperty(String key)
	 * @throws CoreException
	 */
	public void setSharedProperty(String key, String value) throws CoreException {

		IFile rscFile = this.project.getFile(key);
		byte[] bytes = null;
		try {
			bytes = value.getBytes(org.rubypeople.rdt.core.util.Util.UTF_8); // .loadpath
																							// always
																							// encoded
																							// with
																							// UTF-8
		} catch (UnsupportedEncodingException e) {
			Util.log(e, "Could not write .loadpath with UTF-8 encoding "); //$NON-NLS-1$
			// fallback to default
			bytes = value.getBytes();
		}
		InputStream inputStream = new ByteArrayInputStream(bytes);
		// update the resource content
		if (rscFile.exists()) {
			if (rscFile.isReadOnly()) {
				// provide opportunity to checkout read-only .loadpath file
				// (23984)
				ResourcesPlugin.getWorkspace().validateEdit(new IFile[] { rscFile }, null);
			}
			rscFile.setContents(inputStream, IResource.FORCE, null);
		} else {
			rscFile.create(inputStream, IResource.FORCE, null);
		}
	}

	/**
	 * Compare current classpath with given one to see if any different. Note
	 * that the argument classpath contains its binary output.
	 * 
	 * @param newClasspath
	 *            IClasspathEntry[]
	 * @param newOutputLocation
	 *            IPath
	 * @param otherClasspathWithOutput
	 *            IClasspathEntry[]
	 * @return boolean
	 */
	public boolean isLoadpathEqualsTo(ILoadpathEntry[] newClasspath, IPath newOutputLocation, ILoadpathEntry[] otherClasspathWithOutput) {

		if (otherClasspathWithOutput == null || otherClasspathWithOutput.length == 0)
			return false;

		int length = otherClasspathWithOutput.length;
		if (length != newClasspath.length + 1)
			// output is amongst file entries (last one)
			return false;

		// compare classpath entries
		for (int i = 0; i < length - 1; i++) {
			if (!otherClasspathWithOutput[i].equals(newClasspath[i]))
				return false;
		}
		return true;
	}

	/**
	 * Returns the XML String encoding of the class path.
	 */
	protected String encodeLoadpath(ILoadpathEntry[] classpath, IPath outputLocation, boolean indent, Map unknownElements) throws RubyModelException {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(s, "UTF8"); //$NON-NLS-1$
			XMLWriter xmlWriter = new XMLWriter(writer, this, true/*
																	 * print XML
																	 * version
																	 */);

			xmlWriter.startTag(LoadpathEntry.TAG_LOADPATH, indent);
			for (int i = 0; i < classpath.length; ++i) {
				((LoadpathEntry) classpath[i]).elementEncode(xmlWriter, this.project.getFullPath(), indent, true, unknownElements);
			}

			xmlWriter.endTag(LoadpathEntry.TAG_LOADPATH, indent, true/*
																		 * insert
																		 * new
																		 * line
																		 */);
			writer.flush();
			writer.close();
			return s.toString("UTF8");//$NON-NLS-1$
		} catch (IOException e) {
			throw new RubyModelException(e, IRubyModelStatusConstants.IO_EXCEPTION);
		}
	}

	public void setRawLoadpath(ILoadpathEntry[] entries, boolean canModifyResources, IProgressMonitor monitor) throws RubyModelException {
		setRawLoadpath(entries, SetLoadpathOperation.DO_NOT_SET_OUTPUT, monitor, canModifyResources, getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																																								 * don't
																																								 * generateMarkerOnError
																																								 */, false/*
																																																	 * don't
																																																	 * returnResolutionInProgress
																																																	 */), true, // needValidation
				canModifyResources); // save only if modifying resources is
										// allowed
	}

	public void setRawLoadpath(ILoadpathEntry[] entries, IProgressMonitor monitor) throws RubyModelException {
		setRawLoadpath(entries, SetLoadpathOperation.DO_NOT_SET_OUTPUT, monitor, true, // canChangeResource
																						// (as
																						// per
																						// API
																						// contract)
				getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																		 * don't
																		 * generateMarkerOnError
																		 */, false/*
																												 * don't
																												 * returnResolutionInProgress
																												 */), true, // needValidation
				true); // need to save
	}

	public void setRawLoadpath(ILoadpathEntry[] entries, IPath outputLocation, IProgressMonitor monitor) throws RubyModelException {
		setRawLoadpath(entries, outputLocation, monitor, true, // canChangeResource
																// (as per API
																// contract)
				getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																		 * don't
																		 * generateMarkerOnError
																		 */, false/*
																												 * don't
																												 * returnResolutionInProgress
																												 */), true, // needValidation
				true); // need to save
	}

	public ISourceFolderRoot getSourceFolderRoot(String string) {
		return getPackageFragmentRoot0(RubyProject.canonicalizedPath(new Path(string)));
	}

	private ISourceFolderRoot getPackageFragmentRoot0(IPath path) {
		return new ExternalSourceFolderRoot(path, this);
	}

	/*
	 * Force the project to reload its <code>.classpath</code> file from disk
	 * and update the classpath accordingly. Usually, a change to the <code>.classpath</code>
	 * file is automatically noticed and reconciled at the next resource change
	 * notification event. If required to consider such a change prior to the
	 * next automatic refresh, then this functionnality should be used to
	 * trigger a refresh. In particular, if a change to the file is performed,
	 * during an operation where this change needs to be reflected before the
	 * operation ends, then an explicit refresh is necessary. Note that
	 * classpath markers are NOT created.
	 * 
	 * @param monitor a progress monitor for reporting operation progress
	 * @exception JavaModelException if the classpath could not be updated.
	 * Reasons include: <ul> <li> This Java element does not exist
	 * (ELEMENT_DOES_NOT_EXIST)</li> <li> Two or more entries specify source
	 * roots with the same or overlapping paths (NAME_COLLISION) <li> A entry of
	 * kind <code>CPE_PROJECT</code> refers to this project (INVALID_PATH)
	 * <li>This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li> <li>The
	 * output location path refers to a location not contained in this project (<code>PATH_OUTSIDE_PROJECT</code>)
	 * <li>The output location path is not an absolute path (<code>RELATIVE_PATH</code>)
	 * <li>The output location path is nested inside a package fragment root of
	 * this project (<code>INVALID_PATH</code>) <li> The classpath is being
	 * modified during resource change event notification (CORE_EXCEPTION) </ul>
	 */
	protected void forceLoadpathReload(IProgressMonitor monitor) throws RubyModelException {

		if (monitor != null && monitor.isCanceled())
			return;

		// check if any actual difference
		boolean wasSuccessful = false; // flag recording if .loadpath file
										// change got reflected
		try {
			// force to (re)read the property file
			ILoadpathEntry[] fileEntries = readLoadpathFile(false/*
																	 * don't
																	 * create
																	 * markers
																	 */, false/*
																								 * don't
																								 * log
																								 * problems
																								 */);
			if (fileEntries == null) {
				return; // could not read, ignore
			}
			RubyModelManager.PerProjectInfo info = getPerProjectInfo();
			if (info.rawLoadpath != null) { // if there is an in-memory
											// classpath
				if (isLoadpathEqualsTo(info.rawLoadpath, info.outputLocation, fileEntries)) {
					wasSuccessful = true;
					return;
				}
			}

			ILoadpathEntry[] oldResolvedLoadpath = info.resolvedLoadpath;
			setRawLoadpath(fileEntries, SetLoadpathOperation.DO_NOT_SET_OUTPUT, monitor, !ResourcesPlugin.getWorkspace().isTreeLocked(), // canChangeResource
					oldResolvedLoadpath != null ? oldResolvedLoadpath : getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/*
																																 * don't
																																 * generateMarkerOnError
																																 */, false/*
																																										 * don't
																																										 * returnResolutionInProgress
																																										 */), true, // needValidation
					false); // no need to save

			// if reach that far, the classpath file change got absorbed
			wasSuccessful = true;
		} catch (RuntimeException e) {
			// setRawClasspath might fire a delta, and a listener may throw an
			// exception
			if (this.project.isAccessible()) {
				Util.log(e, "Could not set loadpath for " + getPath()); //$NON-NLS-1$
			}
			throw e; // rethrow
		} catch (RubyModelException e) { // CP failed validation
			if (!ResourcesPlugin.getWorkspace().isTreeLocked()) {
				if (this.project.isAccessible()) {
					if (e.getRubyModelStatus().getException() instanceof CoreException) {
						// happens if the .loadpath could not be written to disk
						createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_couldNotWriteClasspathFile, new String[] { getElementName(), e.getMessage() })));
					} else {
						createLoadpathProblemMarker(new RubyModelStatus(IRubyModelStatusConstants.INVALID_LOADPATH_FILE_FORMAT, Messages.bind(Messages.classpath_invalidClasspathInClasspathFile, new String[] { getElementName(), e.getMessage() })));
					}
				}
			}
			throw e; // rethrow
		} finally {
			if (!wasSuccessful) {
				try {
					this.getPerProjectInfo().updateLoadpathInformation(RubyProject.INVALID_LOADPATH);
					updateSourceFolderRoots();
				} catch (RubyModelException e) {
					// ignore
				}
			}
		}
	}

	public void updateLoadpathMarkers(Map preferredClasspaths, Map preferredOutputs) {
		this.flushLoadpathProblemMarkers(false/* cycle */, true/* format */);
		this.flushLoadpathProblemMarkers(false/* cycle */, false/* format */);

		ILoadpathEntry[] classpath = this.readLoadpathFile(true/* marker */, false/* log */);

		// remember invalid path so as to avoid reupdating it again later on
		if (preferredClasspaths != null) {
			preferredClasspaths.put(this, classpath == null ? INVALID_LOADPATH : classpath);
		}
		if (preferredOutputs != null) {
			preferredOutputs.put(this, null);
		}

		// force classpath marker refresh
		if (classpath != null) {
			for (int i = 0; i < classpath.length; i++) {
				IRubyModelStatus status = LoadpathEntry.validateLoadpathEntry(this, classpath[i], false/*
																										 * src
																										 * attach
																										 */, true /*
																															 * recurse
																															 * in
																															 * container
																															 */);
				if (!status.isOK()) {
					if (status.getCode() == IRubyModelStatusConstants.INVALID_CLASSPATH && ((LoadpathEntry) classpath[i]).isOptional())
						continue; // ignore this entry
					this.createLoadpathProblemMarker(status);
				}
			}
			IRubyModelStatus status = LoadpathEntry.validateLoadpath(this, classpath, null);
			if (!status.isOK())
				this.createLoadpathProblemMarker(status);
		}
	}

	/**
	 * Reads and decode an XML loadpath string
	 */
	public ILoadpathEntry[] decodeLoadpath(String xmlClasspath, boolean createMarker, boolean logProblems) {
		return decodeLoadpath(xmlClasspath, createMarker, logProblems, null/*not interested in unknown elements*/);
	}

	public ISourceFolderRoot findSourceFolderRoot(IPath path) throws RubyModelException {
		return findSourceFolderRoot0(RubyProject.canonicalizedPath(path));
	}
	
	/*
	 * no path canonicalization 
	 */
	public ISourceFolderRoot findSourceFolderRoot0(IPath path)
		throws RubyModelException {

		ISourceFolderRoot[] allRoots = this.getAllSourceFolderRoots();
		if (!path.isAbsolute()) {
			throw new IllegalArgumentException(Messages.path_mustBeAbsolute); 
		}
		for (int i= 0; i < allRoots.length; i++) {
			ISourceFolderRoot classpathRoot= allRoots[i];
			if (classpathRoot.getPath().equals(path)) {
				return classpathRoot;
			}
		}
		return null;
	}
	
	/**
	 * @see IRubyProject
	 */
	public ISourceFolderRoot[] findSourceFolderRoots(ILoadpathEntry entry) {
		try {
			ILoadpathEntry[] classpath = this.getRawLoadpath();
			for (int i = 0, length = classpath.length; i < length; i++) {
				if (classpath[i].equals(entry)) { // entry may need to be resolved
					return 
						computeSourceFolderRoots(
							getResolvedLoadpath(new ILoadpathEntry[] {entry}, null, true, false, null/*no reverse map*/), 
							false, // don't retrieve exported roots
							null); /*no reverse map*/
				}
			}
		} catch (RubyModelException e) {
			// project doesn't exist: return an empty array
		}
		return new ISourceFolderRoot[] {};
	}
	
	/**
	 * @see IRubyProject
	 */
	public ISourceFolderRoot[] getAllSourceFolderRoots()
		throws RubyModelException {

		return getAllSourceFolderRoots(null /*no reverse map*/);
	}
	
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
			case JEM_SOURCEFOLDERROOT:
				String rootPath = ISourceFolderRoot.DEFAULT_PACKAGEROOT_PATH;
				token = null;
				while (memento.hasMoreTokens()) {
					token = memento.nextToken();
					char firstChar = token.charAt(0);
					if (firstChar != JEM_SOURCE_FOLDER && firstChar != JEM_COUNT) {
						rootPath += token;
					} else {
						break;
					}
				}
				IPath path = new Path(rootPath);
				RubyElement root;
				if(path.isAbsolute()) {
					root = (RubyElement) getPackageFragmentRoot0(path);
				} else
					root = (RubyElement)getSourceFolderRoot(path);
				if (token != null && token.charAt(0) == JEM_SOURCE_FOLDER) {
					return root.getHandleFromMemento(token, memento, owner);
				} else {
					return root.getHandleFromMemento(memento, owner);
				}
		}
		return null;
	}

	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	protected char getHandleMementoDelimiter() {
		return JEM_RUBYPROJECT;
	}
	
	/**
	 * @see IRubyProject
	 */
	public ITypeHierarchy newTypeHierarchy(
		IRegion region,
		IProgressMonitor monitor)
		throws RubyModelException {			
		return newTypeHierarchy(region, DefaultWorkingCopyOwner.PRIMARY, monitor);
	}
	
	/**
	 * @see IJavaProject
	 */
	public ITypeHierarchy newTypeHierarchy(
		IRegion region,
		WorkingCopyOwner owner,
		IProgressMonitor monitor)
		throws RubyModelException {

		if (region == null) {
			throw new IllegalArgumentException(Messages.hierarchy_nullRegion);
		}
		IRubyScript[] workingCopies = RubyModelManager.getRubyModelManager().getWorkingCopies(owner, true/*add primary working copies*/);
		CreateTypeHierarchyOperation op =
			new CreateTypeHierarchyOperation(region, workingCopies, null, true);
		op.runOperation(monitor);
		return op.getResult();
	}
	
	/*
	 * @see IRubyProject
	 */
	public boolean isOnLoadpath(IResource resource) {
		IPath exactPath = resource.getFullPath();
		IPath path = exactPath;
		
		// ensure that folders are only excluded if all of their children are excluded
		boolean isFolderPath = resource.getType() == IResource.FOLDER;
		
		ILoadpathEntry[] classpath;
		try {
			classpath = this.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
		} catch(RubyModelException e){
			return false; // not a Ruby project
		}
		for (int i = 0; i < classpath.length; i++) {
			ILoadpathEntry entry = classpath[i];
			IPath entryPath = entry.getPath();
			if (entryPath.equals(exactPath)) { // source folder roots must match exactly entry pathes (no exclusion there)
				return true;
			}
			if (entryPath.isPrefixOf(path) 
					&& !Util.isExcluded(path, ((LoadpathEntry)entry).fullInclusionPatternChars(), ((LoadpathEntry)entry).fullExclusionPatternChars(), isFolderPath)) {
				return true;
			}
		}
		return false;
	}
}

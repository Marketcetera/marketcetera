package org.rubypeople.rdt.internal.core.search.indexing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;
import org.rubypeople.rdt.internal.compiler.util.SimpleLookupTable;
import org.rubypeople.rdt.internal.compiler.util.SimpleSet;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.SourceElementParser;
import org.rubypeople.rdt.internal.core.index.DiskIndex;
import org.rubypeople.rdt.internal.core.index.Index;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;
import org.rubypeople.rdt.internal.core.search.PatternSearchJob;
import org.rubypeople.rdt.internal.core.search.processing.IJob;
import org.rubypeople.rdt.internal.core.search.processing.JobManager;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class IndexManager extends JobManager {

	public SimpleLookupTable indexLocations = new SimpleLookupTable();
	/*
	 * key = an IPath, value = an Index
	 */
	private Map indexes = new HashMap(5);

	/* need to save ? */
	private boolean needToSave = false;
	private static final CRC32 checksumCalculator = new CRC32();
	private IPath rubyPluginLocation = null;

	/* can only replace a current state if its less than the new one */
	// key = indexLocation path, value = index state integer
	private SimpleLookupTable indexStates = null;
	private File savedIndexNamesFile = new File(getSavedIndexesDirectory(), "savedIndexNames.txt"); //$NON-NLS-1$
	public static Integer SAVED_STATE = new Integer(0);
	public static Integer UPDATING_STATE = new Integer(1);
	public static Integer UNKNOWN_STATE = new Integer(2);
	public static Integer REBUILDING_STATE = new Integer(3);
	
	private IPath getRubyPluginWorkingLocation() {
		if (this.rubyPluginLocation != null) return this.rubyPluginLocation;

		IPath stateLocation = RubyCore.getPlugin().getStateLocation();
		return this.rubyPluginLocation = stateLocation;
	}
	private File getSavedIndexesDirectory() {
		return new File(getRubyPluginWorkingLocation().toOSString());
	}
	
	public synchronized void jobWasCancelled(IPath containerPath) {
		IPath indexLocation = computeIndexLocation(containerPath);
		Index index = getIndex(indexLocation);
		if (index != null) {
			index.monitor = null;
			this.indexes.remove(indexLocation);
		}
		updateIndexState(indexLocation, UNKNOWN_STATE);
	}
	
	public IPath computeIndexLocation(IPath containerPath) {
		IPath indexLocation = (IPath) this.indexLocations.get(containerPath);
		if (indexLocation == null) {
			String pathString = containerPath.toOSString();
			checksumCalculator.reset();
			checksumCalculator.update(pathString.getBytes());
			String fileName = Long.toString(checksumCalculator.getValue()) + ".index"; //$NON-NLS-1$
			if (VERBOSE)
				Util.verbose("-> index name for " + pathString + " is " + fileName); //$NON-NLS-1$ //$NON-NLS-2$
			// to share the indexLocation between the indexLocations and indexStates tables, get the key from the indexStates table
			indexLocation = (IPath) getIndexStates().getKey(getRubyPluginWorkingLocation().append(fileName));
			this.indexLocations.put(containerPath, indexLocation);
		}
		return indexLocation;
	}
	
	public synchronized Index getIndex(IPath indexLocation) {
		return (Index) this.indexes.get(indexLocation); // is null if unknown, call if the containerPath must be computed
	}
	
	private SimpleLookupTable getIndexStates() {
		if (this.indexStates != null) return this.indexStates;

		this.indexStates = new SimpleLookupTable();
		IPath indexesDirectoryPath = getRubyPluginWorkingLocation();
		char[][] savedNames = readIndexState(indexesDirectoryPath.toOSString());
		if (savedNames != null) {
			for (int i = 1, l = savedNames.length; i < l; i++) { // first name is saved signature, see readIndexState()
				char[] savedName = savedNames[i];
				if (savedName.length > 0) {
					IPath indexLocation = indexesDirectoryPath.append(new String(savedName)); // shares indexesDirectoryPath's segments
					if (VERBOSE)
						Util.verbose("Reading saved index file " + indexLocation); //$NON-NLS-1$
					this.indexStates.put(indexLocation, SAVED_STATE);
				}
			}
		} else {
			deleteIndexFiles();
		}
		return this.indexStates;
	}
	
	public void deleteIndexFiles() {
		this.savedIndexNamesFile.delete(); // forget saved indexes & delete each index file
		deleteIndexFiles(null);
	}
	private void deleteIndexFiles(SimpleSet pathsToKeep) {
		File[] indexesFiles = getSavedIndexesDirectory().listFiles();
		if (indexesFiles == null) return;

		for (int i = 0, l = indexesFiles.length; i < l; i++) {
			String fileName = indexesFiles[i].getAbsolutePath();
			if (pathsToKeep != null && pathsToKeep.includes(fileName)) continue;
			String suffix = ".index"; //$NON-NLS-1$
			if (fileName.regionMatches(true, fileName.length() - suffix.length(), suffix, 0, suffix.length())) {
				if (VERBOSE)
					Util.verbose("Deleting index file " + indexesFiles[i]); //$NON-NLS-1$
				indexesFiles[i].delete();
			}
		}
	}
	
	private synchronized void updateIndexState(IPath indexLocation, Integer indexState) {
		if (indexLocation.isEmpty())
			throw new IllegalArgumentException();

		getIndexStates(); // ensure the states are initialized
		if (indexState != null) {
			if (indexState.equals(indexStates.get(indexLocation))) return; // not changed
			indexStates.put(indexLocation, indexState);
		} else {
			if (!indexStates.containsKey(indexLocation)) return; // did not exist anyway
			indexStates.removeKey(indexLocation);
		}

		writeSavedIndexNamesFile();

		if (VERBOSE) {
			String state = "?"; //$NON-NLS-1$
			if (indexState == SAVED_STATE) state = "SAVED"; //$NON-NLS-1$
			else if (indexState == UPDATING_STATE) state = "UPDATING"; //$NON-NLS-1$
			else if (indexState == UNKNOWN_STATE) state = "UNKNOWN"; //$NON-NLS-1$
			else if (indexState == REBUILDING_STATE) state = "REBUILDING"; //$NON-NLS-1$
			Util.verbose("-> index state updated to: " + state + " for: "+indexLocation); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	private void writeSavedIndexNamesFile() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(savedIndexNamesFile));
			writer.write(DiskIndex.SIGNATURE);
			writer.write('+');
			writer.write(getRubyPluginWorkingLocation().toOSString());
			writer.write('\n');
			Object[] keys = indexStates.keyTable;
			Object[] states = indexStates.valueTable;
			for (int i = 0, l = states.length; i < l; i++) {
				IPath key = (IPath) keys[i];
				if (key != null && !key.isEmpty() && states[i] == SAVED_STATE) {
					writer.write(key.lastSegment());
					writer.write('\n');
				}
			}
		} catch (IOException ignored) {
			if (VERBOSE)
				Util.verbose("Failed to write saved index file names", System.err); //$NON-NLS-1$
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	private char[][] readIndexState(String dirOSString) {
		try {
			char[] savedIndexNames = org.rubypeople.rdt.core.util.Util.getFileCharContent(savedIndexNamesFile, null);
			if (savedIndexNames.length > 0) {
				char[][] names = CharOperation.splitOn('\n', savedIndexNames);
				if (names.length > 1) {
					// First line is DiskIndex signature + saved plugin working location (see writeSavedIndexNamesFile())
					String savedSignature = DiskIndex.SIGNATURE + "+" + dirOSString; //$NON-NLS-1$
					if (savedSignature.equals(new String(names[0])))
						return names;
				}
			}
		} catch (IOException ignored) {
			if (VERBOSE)
				Util.verbose("Failed to read saved index file names"); //$NON-NLS-1$
		}
		return null;
	}
	
	@Override
	public String processName() {
		return Messages.process_name;
	}
	
	public synchronized void aboutToUpdateIndex(IPath containerPath, Integer newIndexState) {
		// newIndexState is either UPDATING_STATE or REBUILDING_STATE
		// must tag the index as inconsistent, in case we exit before the update job is started
		IPath indexLocation = computeIndexLocation(containerPath);
		Object state = getIndexStates().get(indexLocation);
		Integer currentIndexState = state == null ? UNKNOWN_STATE : (Integer) state;
		if (currentIndexState.equals(REBUILDING_STATE)) return; // already rebuilding the index

		int compare = newIndexState.compareTo(currentIndexState);
		if (compare > 0) {
			// so UPDATING_STATE replaces SAVED_STATE and REBUILDING_STATE replaces everything
			updateIndexState(indexLocation, newIndexState);
		} else if (compare < 0 && this.indexes.get(indexLocation) == null) {
			// if already cached index then there is nothing more to do
			rebuildIndex(indexLocation, containerPath);
		}
	}
	
	private void rebuildIndex(IPath indexLocation, IPath containerPath) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) return;
		Object target = RubyModel.getTarget(workspace.getRoot(), containerPath, true);
		if (target == null) return;

		if (VERBOSE)
			Util.verbose("-> request to rebuild index: "+indexLocation+" path: "+containerPath); //$NON-NLS-1$ //$NON-NLS-2$

		updateIndexState(indexLocation, REBUILDING_STATE);
		IndexRequest request = null;
		if (target instanceof IProject) {
			IProject p = (IProject) target;
			if (RubyProject.hasRubyNature(p))
				request = new IndexAllProject(p, this);
		} else if (target instanceof File) {
			request = new AddExternalFolderToIndex(containerPath, this);
		}
		if (request != null)
			request(request);
	}
	
	public void saveIndex(Index index) throws IOException {
		// must have permission to write from the write monitor
		if (index.hasChanged()) {
			if (VERBOSE)
				Util.verbose("-> saving index " + index.getIndexFile()); //$NON-NLS-1$
			index.save();
		}
		synchronized (this) {
			IPath containerPath = new Path(index.containerPath);
			if (this.jobEnd > this.jobStart) {
				for (int i = this.jobEnd; i > this.jobStart; i--) { // skip the current job
					IJob job = this.awaitingJobs[i];
					if (job instanceof IndexRequest)
						if (((IndexRequest) job).containerPath.equals(containerPath)) return;
				}
			}
			IPath indexLocation = computeIndexLocation(containerPath);
			updateIndexState(indexLocation, SAVED_STATE);
		}
	}
	
	public synchronized Index getIndexForUpdate(IPath containerPath, boolean reuseExistingFile, boolean createIfMissing) {
		IPath indexLocation = computeIndexLocation(containerPath);
		if (getIndexStates().get(indexLocation) == REBUILDING_STATE)
			return getIndex(containerPath, indexLocation, reuseExistingFile, createIfMissing);

		return null; // abort the job since the index has been removed from the REBUILDING_STATE
	}
	
	/**
	 * Returns the index for a given project, according to the following algorithm:
	 * - if index is already in memory: answers this one back
	 * - if (reuseExistingFile) then read it and return this index and record it in memory
	 * - if (createIfMissing) then create a new empty index and record it in memory
	 * 
	 * Warning: Does not check whether index is consistent (not being used)
	 */
	public synchronized Index getIndex(IPath containerPath, IPath indexLocation, boolean reuseExistingFile, boolean createIfMissing) {
		// Path is already canonical per construction
		Index index = getIndex(indexLocation);
		if (index == null) {
			Object state = getIndexStates().get(indexLocation);
			Integer currentIndexState = state == null ? UNKNOWN_STATE : (Integer) state;
			if (currentIndexState == UNKNOWN_STATE) {
				// should only be reachable for query jobs
				// IF you put an index in the cache, then AddJarFileToIndex fails because it thinks there is nothing to do
				rebuildIndex(indexLocation, containerPath);
				return null;
			}

			// index isn't cached, consider reusing an existing index file
			String containerPathString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
			String indexLocationString = indexLocation.toOSString();
			if (reuseExistingFile) {
				File indexFile = new File(indexLocationString);
				if (indexFile.exists()) { // check before creating index so as to avoid creating a new empty index if file is missing
					try {
						index = new Index(indexLocationString, containerPathString, true /*reuse index file*/);
						this.indexes.put(indexLocation, index);
						return index;
					} catch (IOException e) {
						// failed to read the existing file or its no longer compatible
						if (currentIndexState != REBUILDING_STATE) { // rebuild index if existing file is corrupt, unless the index is already being rebuilt
							if (VERBOSE)
								Util.verbose("-> cannot reuse existing index: "+indexLocationString+" path: "+containerPathString); //$NON-NLS-1$ //$NON-NLS-2$
							rebuildIndex(indexLocation, containerPath);
							return null;
						} 
						/*index = null;*/ // will fall thru to createIfMissing & create a empty index for the rebuild all job to populate
					}
				}
				if (currentIndexState == SAVED_STATE) { // rebuild index if existing file is missing
					rebuildIndex(indexLocation, containerPath);
					return null;
				}
			} 
			// index wasn't found on disk, consider creating an empty new one
			if (createIfMissing) {
				try {
					if (VERBOSE)
						Util.verbose("-> create empty index: "+indexLocationString+" path: "+containerPathString); //$NON-NLS-1$ //$NON-NLS-2$
					index = new Index(indexLocationString, containerPathString, false /*do not reuse index file*/);
					this.indexes.put(indexLocation, index);
					return index;
				} catch (IOException e) {
					if (VERBOSE)
						Util.verbose("-> unable to create empty index: "+indexLocationString+" path: "+containerPathString); //$NON-NLS-1$ //$NON-NLS-2$
					// The file could not be created. Possible reason: the project has been deleted.
					return null;
				}
			}
		}
		//System.out.println(" index name: " + path.toOSString() + " <----> " + index.getIndexFile().getName());	
		return index;
	}
	
	/**
	 * Removes the index for a given path. 
	 * This is a no-op if the index did not exist.
	 */
	public synchronized void removeIndex(IPath containerPath) {
		if (VERBOSE)
			Util.verbose("removing index " + containerPath); //$NON-NLS-1$
		IPath indexLocation = computeIndexLocation(containerPath);
		Index index = getIndex(indexLocation);
		File indexFile = null;
		if (index != null) {
			index.monitor = null;
			indexFile = index.getIndexFile();
		}
		if (indexFile == null)
			indexFile = new File(indexLocation.toOSString()); // index is not cached yet, but still want to delete the file
		if (indexFile.exists())
			indexFile.delete();
		this.indexes.remove(indexLocation);
		updateIndexState(indexLocation, null);
	}
	
	/**
	 * Trigger removal of a resource to an index
	 * Note: the actual operation is performed in background
	 */
	public void remove(String containerRelativePath, IPath indexedContainer){
		request(new RemoveFromIndex(containerRelativePath, indexedContainer, this));
	}
	
	/**
	 * Returns the index for a given project, according to the following algorithm:
	 * - if index is already in memory: answers this one back
	 * - if (reuseExistingFile) then read it and return this index and record it in memory
	 * - if (createIfMissing) then create a new empty index and record it in memory
	 * 
	 * Warning: Does not check whether index is consistent (not being used)
	 */
	public synchronized Index getIndex(IPath containerPath, boolean reuseExistingFile, boolean createIfMissing) {
		IPath indexLocation = computeIndexLocation(containerPath);
		return getIndex(containerPath, indexLocation, reuseExistingFile, createIfMissing);
	}
	
	/**
	 * Trigger addition of a resource to an index
	 * Note: the actual operation is performed in background
	 */
	public void addSource(IFile resource, IPath containerPath, SourceElementParser parser) {
		if (RubyCore.getPlugin() == null) return;	
		SearchParticipant participant = BasicSearchEngine.getDefaultSearchParticipant();
		SearchDocument document = participant.getDocument(resource.getFullPath().toString());
		((InternalSearchDocument) document).parser = parser;
		IPath indexLocation = computeIndexLocation(containerPath);
		scheduleDocumentIndexing(document, containerPath, indexLocation, participant);
	}
	
	public void scheduleDocumentIndexing(final SearchDocument searchDocument, IPath container, final IPath indexLocation, final SearchParticipant searchParticipant) {
		request(new IndexRequest(container, this) {
			public boolean execute(IProgressMonitor progressMonitor) {
				if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;
				
				/* ensure no concurrent write access to index */
				Index index = getIndex(this.containerPath, indexLocation, true, /*reuse index file*/ true /*create if none*/);
				if (index == null) return true;
				ReadWriteMonitor monitor = index.monitor;
				if (monitor == null) return true; // index got deleted since acquired
				
				try {
					monitor.enterWrite(); // ask permission to write
					indexDocument(searchDocument, searchParticipant, index, indexLocation);
				} finally {
					monitor.exitWrite(); // free write lock
				}
				return true;
			}
			public String toString() {
				return "indexing " + searchDocument.getPath(); //$NON-NLS-1$
			}
		});
	}
	
	public void indexDocument(SearchDocument searchDocument, SearchParticipant searchParticipant, Index index, IPath indexLocation) {
		try {
			((InternalSearchDocument) searchDocument).index = index;
			searchParticipant.indexDocument(searchDocument, indexLocation);
		} finally {
			((InternalSearchDocument) searchDocument).index = null;
		}
	}
	
	/*
	 * Removes unused indexes from disk.
	 */
	public void cleanUpIndexes() {
		SimpleSet knownPaths = new SimpleSet();
		IRubySearchScope scope = BasicSearchEngine.createWorkspaceScope();
		PatternSearchJob job = new PatternSearchJob(null, BasicSearchEngine.getDefaultSearchParticipant(), scope, null);
		Index[] selectedIndexes = job.getIndexes(null);
		for (int i = 0, l = selectedIndexes.length; i < l; i++) {
			String path = selectedIndexes[i].getIndexFile().getAbsolutePath();
			knownPaths.add(path);
		}

		if (this.indexStates != null) {
			Object[] keys = this.indexStates.keyTable;
			IPath[] locations = new IPath[this.indexStates.elementSize];
			int count = 0;
			for (int i = 0, l = keys.length; i < l; i++) {
				IPath key = (IPath) keys[i];
				if (key != null && !knownPaths.includes(key.toOSString()))
					locations[count++] = key;
			}
			if (count > 0)
				removeIndexesState(locations);
		}
		deleteIndexFiles(knownPaths);
	}
	
	private synchronized void removeIndexesState(IPath[] locations) {
		getIndexStates(); // ensure the states are initialized
		int length = locations.length;
		boolean changed = false;
		for (int i=0; i<length; i++) {
			if (locations[i] == null) continue;
			if ((indexStates.removeKey(locations[i]) != null)) {
				changed = true;
				if (VERBOSE) {
					Util.verbose("-> index state updated to: ? for: "+locations[i]); //$NON-NLS-1$
				}
			}
		}
		if (!changed) return;

		writeSavedIndexNamesFile();
	}
	public SourceElementParser getSourceElementParser(IRubyProject project, ISourceElementRequestor requestor) {
		// TODO take into account the project?
		return new SourceElementParser(requestor);
	}
	public void indexLibrary(IPath path, IProject project) {
//		 requestingProject is no longer used to cancel jobs but leave it here just in case
		if (RubyCore.getPlugin() == null) return;

		Object target = RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), path, true);
		IndexRequest request = null;
		if (target instanceof java.io.File) {
			if (((java.io.File) target).isDirectory()) {
				request = new AddExternalFolderToIndex(path, this);
			} else {
				return;
			}
		} else {
			return;
		}

		// check if the same request is not already in the queue
		if (!isJobWaiting(request))
			this.request(request);
	}
	/**
	 * Trigger addition of the entire content of a project
	 * Note: the actual operation is performed in background 
	 */
	public void indexAll(IProject project) {
		if (RubyCore.getPlugin() == null) return;

		// Also request indexing of binaries on the classpath
		// determine the new children
		try {
			RubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			RubyProject javaProject = (RubyProject) model.getRubyProject(project);	
			// only consider immediate libraries - each project will do the same
			// NOTE: force to resolve CP variables before calling indexer - 19303, so that initializers
			// will be run in the current thread.
			ILoadpathEntry[] entries = javaProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);	
			for (int i = 0; i < entries.length; i++) {
				ILoadpathEntry entry= entries[i];
				if (entry.getEntryKind() == ILoadpathEntry.CPE_LIBRARY)
					this.indexLibrary(entry.getPath(), project);
			}
		} catch(RubyModelException e){ // cannot retrieve classpath info
		}

		// check if the same request is not already in the queue
		IndexRequest request = new IndexAllProject(project, this);
		if (!isJobWaiting(request))
			this.request(request);
	}
	
	/**
	 * Removes all indexes whose paths start with (or are equal to) the given path. 
	 */
	public synchronized void removeIndexFamily(IPath path) {
		// only finds cached index files... shutdown removes all non-cached index files
		ArrayList toRemove = null;
		Object[] containerPaths = this.indexLocations.keyTable;
		for (int i = 0, length = containerPaths.length; i < length; i++) {
			IPath containerPath = (IPath) containerPaths[i];
			if (containerPath == null) continue;
			if (path.isPrefixOf(containerPath)) {
				if (toRemove == null)
					toRemove = new ArrayList();
				toRemove.add(containerPath);
			}
		}
		if (toRemove != null)
			for (int i = 0, length = toRemove.size(); i < length; i++)
				this.removeIndex((IPath) toRemove.get(i));
	}
	
	/**
	 * Recreates the index for a given path, keeping the same read-write monitor.
	 * Returns the new empty index or null if it didn't exist before.
	 * Warning: Does not check whether index is consistent (not being used)
	 */
	public synchronized Index recreateIndex(IPath containerPath) {
		// only called to over write an existing cached index...
		String containerPathString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
		try {
			// Path is already canonical
			IPath indexLocation = computeIndexLocation(containerPath);
			
			Index index = (Index) this.indexes.get(indexLocation);
			ReadWriteMonitor monitor = index == null ? null : index.monitor;

			if (VERBOSE)
				Util.verbose("-> recreating index: "+indexLocation+" for path: "+containerPathString); //$NON-NLS-1$ //$NON-NLS-2$
			index = new Index(indexLocation.toString(), containerPathString, false /*reuse index file*/);
			this.indexes.put(indexLocation, index);
			index.monitor = monitor;
			return index;
		} catch (IOException e) {
			// The file could not be created. Possible reason: the project has been deleted.
			if (VERBOSE) {
				Util.verbose("-> failed to recreate index for path: "+containerPathString); //$NON-NLS-1$
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * Remove the content of the given source folder from the index.
	 */
	public void removeSourceFolderFromIndex(RubyProject javaProject, IPath sourceFolder, char[][] inclusionPatterns, char[][] exclusionPatterns) {
		IProject project = javaProject.getProject();
		if (this.jobEnd > this.jobStart) {
			// skip it if a job to index the project is already in the queue
			IndexRequest request = new IndexAllProject(project, this);
			if (isJobWaiting(request)) return;
		}

		this.request(new RemoveFolderFromIndex(sourceFolder, inclusionPatterns, exclusionPatterns, project, this));	
	}
	
	/**
	 * Index the content of the given source folder.
	 */
	public void indexSourceFolder(RubyProject javaProject, IPath sourceFolder, char[][] inclusionPatterns, char[][] exclusionPatterns) {
		IProject project = javaProject.getProject();
		if (this.jobEnd > this.jobStart) {
			// skip it if a job to index the project is already in the queue
			IndexRequest request = new IndexAllProject(project, this);
			if (isJobWaiting(request)) return;
		}
		this.request(new AddFolderToIndex(sourceFolder, project, inclusionPatterns, exclusionPatterns, this));
	}
	
	@Override
	public synchronized void reset() {
		super.reset();
		if (this.indexes != null) {
			this.indexes = new HashMap(5);
			this.indexStates = null;
		}
		this.indexLocations = new SimpleLookupTable();
		this.rubyPluginLocation = null;
	}
}
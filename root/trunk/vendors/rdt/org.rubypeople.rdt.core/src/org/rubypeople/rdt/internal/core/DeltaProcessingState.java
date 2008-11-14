package org.rubypeople.rdt.internal.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Util;

public class DeltaProcessingState implements IResourceChangeListener {

    /*
     * Collection of listeners for Ruby element deltas
     */
    public IElementChangedListener[] elementChangedListeners = new IElementChangedListener[5];
    public int[] elementChangedListenerMasks = new int[5];
    public int elementChangedListenerCount = 0;

    /*
     * Collection of pre Ruby resource change listeners
     */
    public IResourceChangeListener[] preResourceChangeListeners = new IResourceChangeListener[1];
    public int[] preResourceChangeEventMasks = new int[1];
    public int preResourceChangeListenerCount = 0;

    /*
     * The delta processor for the current thread.
     */
    private ThreadLocal deltaProcessors = new ThreadLocal();
    
	/* A table from IPath (from a classpath entry) to RootInfo */
	public HashMap roots = new HashMap();
	
	/* A table from IPath (from a classpath entry) to ArrayList of RootInfo
	 * Used when an IPath corresponds to more than one root */
	public HashMap otherRoots = new HashMap();
	
	/* A table from IPath (from a classpath entry) to RootInfo
	 * from the last time the delta processor was invoked. */
	public HashMap oldRoots = new HashMap();
	
	/* A table from IPath (from a classpath entry) to ArrayList of RootInfo
	 * from the last time the delta processor was invoked.
	 * Used when an IPath corresponds to more than one root */
	public HashMap oldOtherRoots = new HashMap();

    /* Whether the roots tables should be recomputed */
	public boolean rootsAreStale = true;

	/* Threads that are currently running initializeRoots() */
	private Set initializingThreads = Collections.synchronizedSet(new HashSet());	
	
    /* A table from IRubyProject to IRubyProject[] (the list of direct dependent of the key) */
    public HashMap projectDependencies = new HashMap();
    
	public Hashtable externalTimeStamps;
	
	public HashMap projectUpdates = new HashMap();
	/**
	 * Workaround for bug 15168 circular errors not reported  
	 * This is a cache of the projects before any project addition/deletion has started.
	 */
	private HashSet rubyProjectNamesCache;

    /*
     * Need to clone defensively the listener information, in case some listener
     * is reacting to some notification iteration by adding/changing/removing
     * any of the other (for example, if it deregisters itself).
     */
    public void addElementChangedListener(IElementChangedListener listener, int eventMask) {
        for (int i = 0; i < this.elementChangedListenerCount; i++) {
            if (this.elementChangedListeners[i].equals(listener)) {

                // only clone the masks, since we could be in the middle of
                // notifications and one listener decide to change
                // any event mask of another listeners (yet not notified).
                int cloneLength = this.elementChangedListenerMasks.length;
                System.arraycopy(this.elementChangedListenerMasks, 0,
                        this.elementChangedListenerMasks = new int[cloneLength], 0, cloneLength);
                this.elementChangedListenerMasks[i] = eventMask; // could be
                // different
                return;
            }
        }
        // may need to grow, no need to clone, since iterators will have cached
        // original arrays and max boundary and we only add to the end.
        int length;
        if ((length = this.elementChangedListeners.length) == this.elementChangedListenerCount) {
            System.arraycopy(this.elementChangedListeners, 0,
                    this.elementChangedListeners = new IElementChangedListener[length * 2], 0,
                    length);
            System.arraycopy(this.elementChangedListenerMasks, 0,
                    this.elementChangedListenerMasks = new int[length * 2], 0, length);
        }
        this.elementChangedListeners[this.elementChangedListenerCount] = listener;
        this.elementChangedListenerMasks[this.elementChangedListenerCount] = eventMask;
        this.elementChangedListenerCount++;
    }

    public void removeElementChangedListener(IElementChangedListener listener) {

        for (int i = 0; i < this.elementChangedListenerCount; i++) {

            if (this.elementChangedListeners[i].equals(listener)) {

                // need to clone defensively since we might be in the middle of
                // listener notifications (#fire)
                int length = this.elementChangedListeners.length;
                IElementChangedListener[] newListeners = new IElementChangedListener[length];
                System.arraycopy(this.elementChangedListeners, 0, newListeners, 0, i);
                int[] newMasks = new int[length];
                System.arraycopy(this.elementChangedListenerMasks, 0, newMasks, 0, i);

                // copy trailing listeners
                int trailingLength = this.elementChangedListenerCount - i - 1;
                if (trailingLength > 0) {
                    System.arraycopy(this.elementChangedListeners, i + 1, newListeners, i,
                            trailingLength);
                    System.arraycopy(this.elementChangedListenerMasks, i + 1, newMasks, i,
                            trailingLength);
                }

                // update manager listener state (#fire need to iterate over
                // original listeners through a local variable to hold onto
                // the original ones)
                this.elementChangedListeners = newListeners;
                this.elementChangedListenerMasks = newMasks;
                this.elementChangedListenerCount--;
                return;
            }
        }
    }

    public DeltaProcessor getDeltaProcessor() {
        DeltaProcessor deltaProcessor = (DeltaProcessor) this.deltaProcessors.get();
        if (deltaProcessor != null) return deltaProcessor;
        deltaProcessor = new DeltaProcessor(this, RubyModelManager.getRubyModelManager());
        this.deltaProcessors.set(deltaProcessor);
        return deltaProcessor;
    }
    
    public void addPreResourceChangedListener(IResourceChangeListener listener, int eventMask) {
		for (int i = 0; i < this.preResourceChangeListenerCount; i++){
			if (this.preResourceChangeListeners[i].equals(listener)) {
				this.preResourceChangeEventMasks[i] |= eventMask;
				return;
			}
		}
		// may need to grow, no need to clone, since iterators will have cached original arrays and max boundary and we only add to the end.
		int length;
		if ((length = this.preResourceChangeListeners.length) == this.preResourceChangeListenerCount) {
			System.arraycopy(this.preResourceChangeListeners, 0, this.preResourceChangeListeners = new IResourceChangeListener[length*2], 0, length);
			System.arraycopy(this.preResourceChangeEventMasks, 0, this.preResourceChangeEventMasks = new int[length*2], 0, length);
		}
		this.preResourceChangeListeners[this.preResourceChangeListenerCount] = listener;
		this.preResourceChangeEventMasks[this.preResourceChangeListenerCount] = eventMask;
		this.preResourceChangeListenerCount++;
	}
    
public void removePreResourceChangedListener(IResourceChangeListener listener) {
		
		for (int i = 0; i < this.preResourceChangeListenerCount; i++){
			
			if (this.preResourceChangeListeners[i].equals(listener)){
				
				// need to clone defensively since we might be in the middle of listener notifications (#fire)
				int length = this.preResourceChangeListeners.length;
				IResourceChangeListener[] newListeners = new IResourceChangeListener[length];
				int[] newEventMasks = new int[length];
				System.arraycopy(this.preResourceChangeListeners, 0, newListeners, 0, i);
				System.arraycopy(this.preResourceChangeEventMasks, 0, newEventMasks, 0, i);
				
				// copy trailing listeners
				int trailingLength = this.preResourceChangeListenerCount - i - 1;
				if (trailingLength > 0) {
					System.arraycopy(this.preResourceChangeListeners, i+1, newListeners, i, trailingLength);
					System.arraycopy(this.preResourceChangeEventMasks, i+1, newEventMasks, i, trailingLength);
				}
				
				// update manager listener state (#fire need to iterate over original listeners through a local variable to hold onto
				// the original ones)
				this.preResourceChangeListeners = newListeners;
				this.preResourceChangeEventMasks = newEventMasks;
				this.preResourceChangeListenerCount--;
				return;
			}
		}
	}

    public void resourceChanged(final IResourceChangeEvent event) {
        for (int i = 0; i < this.preResourceChangeListenerCount; i++) {
            // wrap callbacks with Safe runnable for subsequent listeners to be
            // called when some are causing grief
            final IResourceChangeListener listener = this.preResourceChangeListeners[i];
            if ((this.preResourceChangeEventMasks[i] & event.getType()) != 0)
            	SafeRunner.run(new ISafeRunnable() {

                    public void handleException(Throwable exception) {
                        Util
                                .log(exception,
                                        "Exception occurred in listener of pre Ruby resource change notification"); //$NON-NLS-1$
                    }

                    public void run() throws Exception {
                        listener.resourceChanged(event);
                    }
                });
        }
        try {
            getDeltaProcessor().resourceChanged(event);
        } finally {
            // TODO (jerome) see 47631, may want to get rid of following so as
            // to reuse delta processor ?
            if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
                this.deltaProcessors.set(null);
            }
        }

    }

    public void initializeRoots() {
//    	 recompute root infos only if necessary
		HashMap newRoots = null;
		HashMap newOtherRoots = null;

		HashMap newProjectDependencies = null;
		if (this.rootsAreStale) {
			Thread currentThread = Thread.currentThread();
			boolean addedCurrentThread = false;			
			try {
				// if reentering initialization (through a container initializer for example) no need to compute roots again
				// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=47213
				if (!this.initializingThreads.add(currentThread)) return;
				addedCurrentThread = true;
				
				// all classpaths in the workspace are going to be resolved
				// ensure that containers are initialized in one batch
				RubyModelManager.getRubyModelManager().batchContainerInitializations = true;

				newRoots = new HashMap();
				newOtherRoots = new HashMap();
				newProjectDependencies = new HashMap();
		
				IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
				IRubyProject[] projects;
				try {
					projects = model.getRubyProjects();
				} catch (RubyModelException e) {
					// nothing can be done
					return;
				}
				for (int i = 0, length = projects.length; i < length; i++) {
					RubyProject project = (RubyProject) projects[i];
					ILoadpathEntry[] loadpath;
					try {
						loadpath = project.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
					} catch (RubyModelException e) {
						// continue with next project
						continue;
					}
					for (int j= 0, loadpathLength = loadpath.length; j < loadpathLength; j++) {
						ILoadpathEntry entry = loadpath[j];
						if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
							IRubyProject key = model.getRubyProject(entry.getPath().segment(0)); // TODO (jerome) reuse handle
							IRubyProject[] dependents = (IRubyProject[]) newProjectDependencies.get(key);
							if (dependents == null) {
								dependents = new IRubyProject[] {project};
							} else {
								int dependentsLength = dependents.length;
								System.arraycopy(dependents, 0, dependents = new IRubyProject[dependentsLength+1], 0, dependentsLength);
								dependents[dependentsLength] = project;
							}
							newProjectDependencies.put(key, dependents);
							continue;
						}
						
						// root path
						IPath path = entry.getPath();
						if (newRoots.get(path) == null) {
							newRoots.put(path, new DeltaProcessor.RootInfo(project, path, ((LoadpathEntry)entry).fullInclusionPatternChars(), ((LoadpathEntry)entry).fullExclusionPatternChars(), entry.getEntryKind()));
						} else {
							ArrayList rootList = (ArrayList)newOtherRoots.get(path);
							if (rootList == null) {
								rootList = new ArrayList();
								newOtherRoots.put(path, rootList);
							}
							rootList.add(new DeltaProcessor.RootInfo(project, path, ((LoadpathEntry)entry).fullInclusionPatternChars(), ((LoadpathEntry)entry).fullExclusionPatternChars(), entry.getEntryKind()));
						}						
					}
				}
			} finally {
				if (addedCurrentThread) {
					this.initializingThreads.remove(currentThread);
				}
			}
		}
		synchronized(this) {
			this.oldRoots = this.roots;
			this.oldOtherRoots = this.otherRoots;			
			if (this.rootsAreStale && newRoots != null) { // double check again
				this.roots = newRoots;
				this.otherRoots = newOtherRoots;
				this.projectDependencies = newProjectDependencies;
				this.rootsAreStale = false;
			}
		}    
    }

    public Hashtable getExternalLibTimeStamps() {
		if (this.externalTimeStamps == null) {
			Hashtable timeStamps = new Hashtable();
			File timestampsFile = getTimeStampsFile();
			DataInputStream in = null;
			try {
				in = new DataInputStream(new BufferedInputStream(new FileInputStream(timestampsFile)));
				int size = in.readInt();
				while (size-- > 0) {
					String key = in.readUTF();
					long timestamp = in.readLong();
					timeStamps.put(Path.fromPortableString(key), new Long(timestamp));
				}
			} catch (IOException e) {
				if (timestampsFile.exists())
					Util.log(e, "Unable to read external time stamps"); //$NON-NLS-1$
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// nothing we can do: ignore
					}
				}
			}
			this.externalTimeStamps = timeStamps;
		}
		return this.externalTimeStamps;
	}
    
	private File getTimeStampsFile() {
		return RubyCore.getPlugin().getStateLocation().append("externalLibsTimeStamps").toFile(); //$NON-NLS-1$
	}

	public void saveExternalLibTimeStamps() throws CoreException {
		if (this.externalTimeStamps == null) return;
		File timestamps = getTimeStampsFile();
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(timestamps)));
			out.writeInt(this.externalTimeStamps.size());
			Iterator keys = this.externalTimeStamps.keySet().iterator();
			while (keys.hasNext()) {
				IPath key = (IPath) keys.next();
				out.writeUTF(key.toPortableString());
				Long timestamp = (Long) this.externalTimeStamps.get(key);
				out.writeLong(timestamp.longValue());
			}
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, IStatus.ERROR, "Problems while saving timestamps", e); //$NON-NLS-1$
			throw new CoreException(status);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// nothing we can do: ignore
				}
			}
		}
	}

	public IRubyProject findRubyProject(String name) {
		if (getOldRubyProjecNames().contains(name))
			return RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(name);
		return null;
	}
	
	/*
	 * Workaround for bug 15168 circular errors not reported 
	 * Returns the list of java projects before resource delta processing
	 * has started.
	 */
	public synchronized HashSet getOldRubyProjecNames() {
		if (this.rubyProjectNamesCache == null) {
			HashSet result = new HashSet();
			IRubyProject[] projects;
			try {
				projects = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProjects();
			} catch (RubyModelException e) {
				return this.rubyProjectNamesCache;
			}
			for (int i = 0, length = projects.length; i < length; i++) {
				IRubyProject project = projects[i];
				result.add(project.getElementName());
			}
			return this.rubyProjectNamesCache = result;
		}
		return this.rubyProjectNamesCache;
	}
	
	
	public synchronized void resetOldRubyProjectNames() {
		this.rubyProjectNamesCache = null;
	}
	
	public synchronized ProjectUpdateInfo[] removeAllProjectUpdates() {
	    int length = this.projectUpdates.size();
	    if (length == 0) return null;
	    ProjectUpdateInfo[]  updates = new ProjectUpdateInfo[length];
	    this.projectUpdates.values().toArray(updates);
	    this.projectUpdates.clear();
	    return updates;
	}

	public void updateProjectReferences(RubyProject project, ILoadpathEntry[] oldResolvedPath, ILoadpathEntry[] newResolvedPath, ILoadpathEntry[] newRawPath, boolean canChangeResources) throws RubyModelException {
		ProjectUpdateInfo info;
		synchronized (this) {
			info = (ProjectUpdateInfo) (canChangeResources ? this.projectUpdates.remove(project) /*remove possibly awaiting one*/ : this.projectUpdates.get(project));
			if (info == null) {
				info = new ProjectUpdateInfo();
				info.project = project;
				info.oldResolvedPath = oldResolvedPath;
				if (!canChangeResources) {
					this.projectUpdates.put(project, info);
				}
		    } // else refresh new loadpath information
		    info.newResolvedPath = newResolvedPath;
		    info.newRawPath = newRawPath;
		}

	    if (canChangeResources) {
	        info.updateProjectReferencesIfNecessary();
	    } // else project references will be updated on next PRE_BUILD notification
	}
	
	public static class ProjectUpdateInfo {
		RubyProject project;
		ILoadpathEntry[] oldResolvedPath;
		ILoadpathEntry[] newResolvedPath;
		ILoadpathEntry[] newRawPath;
		
		/**
		 * Update projects references so that the build order is consistent with the classpath
		 */
		public void updateProjectReferencesIfNecessary() throws RubyModelException {
			
			String[] oldRequired = this.oldResolvedPath == null ? CharOperation.NO_STRINGS : this.project.projectPrerequisites(this.oldResolvedPath);
	
			if (this.newResolvedPath == null) {
				if (this.newRawPath == null)
					this.newRawPath = this.project.getRawLoadpath(true/*create markers*/, false/*don't log problems*/);
				this.newResolvedPath = 
					this.project.getResolvedLoadpath(
						this.newRawPath, 
						null/*no output*/, 
						true/*ignore unresolved entry*/, 
						true/*generate marker on error*/, 
						null/*no reverse map*/);
			}
			String[] newRequired = this.project.projectPrerequisites(this.newResolvedPath);
			try {
				IProject projectResource = this.project.getProject();
				IProjectDescription description = projectResource.getDescription();
				 
				IProject[] projectReferences = description.getDynamicReferences();
				
				HashSet oldReferences = new HashSet(projectReferences.length);
				for (int i = 0; i < projectReferences.length; i++){
					String projectName = projectReferences[i].getName();
					oldReferences.add(projectName);
				}
				HashSet newReferences = (HashSet)oldReferences.clone();
		
				for (int i = 0; i < oldRequired.length; i++){
					String projectName = oldRequired[i];
					newReferences.remove(projectName);
				}
				for (int i = 0; i < newRequired.length; i++){
					String projectName = newRequired[i];
					newReferences.add(projectName);
				}
		
				Iterator iter;
				int newSize = newReferences.size();
				
				checkIdentity: {
					if (oldReferences.size() == newSize){
						iter = newReferences.iterator();
						while (iter.hasNext()){
							if (!oldReferences.contains(iter.next())){
								break checkIdentity;
							}
						}
						return;
					}
				}
				String[] requiredProjectNames = new String[newSize];
				int index = 0;
				iter = newReferences.iterator();
				while (iter.hasNext()){
					requiredProjectNames[index++] = (String)iter.next();
				}
				Util.sort(requiredProjectNames); // ensure that if changed, the order is consistent
				
				IProject[] requiredProjectArray = new IProject[newSize];
				IWorkspaceRoot wksRoot = projectResource.getWorkspace().getRoot();
				for (int i = 0; i < newSize; i++){
					requiredProjectArray[i] = wksRoot.getProject(requiredProjectNames[i]);
				}
				description.setDynamicReferences(requiredProjectArray);
				projectResource.setDescription(description, null);
		
			} catch(CoreException e){
//				if (!ExternalRubyProject.EXTERNAL_PROJECT_NAME.equals(this.project.getElementName()))
					throw new RubyModelException(e);
			}
		}
	}

	
	/*
	 * Update the roots that are affected by the addition or the removal of the given container resource.
	 */
	public synchronized void updateRoots(IPath containerPath, IResourceDelta containerDelta, DeltaProcessor deltaProcessor) {
		Map updatedRoots;
		Map otherUpdatedRoots;
		if (containerDelta.getKind() == IResourceDelta.REMOVED) {
			updatedRoots = this.oldRoots;
			otherUpdatedRoots = this.oldOtherRoots;
		} else {
			updatedRoots = this.roots;
			otherUpdatedRoots = this.otherRoots;
		}
		Iterator iterator = updatedRoots.keySet().iterator();
		while (iterator.hasNext()) {
			IPath path = (IPath)iterator.next();
			if (containerPath.isPrefixOf(path) && !containerPath.equals(path)) {
				IResourceDelta rootDelta = containerDelta.findMember(path.removeFirstSegments(1));
				if (rootDelta == null) continue;
				DeltaProcessor.RootInfo rootInfo = (DeltaProcessor.RootInfo)updatedRoots.get(path);
	
				if (!rootInfo.project.getPath().isPrefixOf(path)) { // only consider roots that are not included in the container
					deltaProcessor.updateCurrentDeltaAndIndex(rootDelta, IRubyElement.SOURCE_FOLDER_ROOT, rootInfo);
				}
				
				ArrayList rootList = (ArrayList)otherUpdatedRoots.get(path);
				if (rootList != null) {
					Iterator otherProjects = rootList.iterator();
					while (otherProjects.hasNext()) {
						rootInfo = (DeltaProcessor.RootInfo)otherProjects.next();
						if (!rootInfo.project.getPath().isPrefixOf(path)) { // only consider roots that are not included in the container
							deltaProcessor.updateCurrentDeltaAndIndex(rootDelta, IRubyElement.SOURCE_FOLDER_ROOT, rootInfo);
						}
					}
				}
			}
		}
	}
}

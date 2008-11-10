package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.SafeRunner;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.builder.RubyBuilder;
import org.rubypeople.rdt.internal.core.hierarchy.TypeHierarchy;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Util;

public class DeltaProcessor {

	static class RootInfo {
		char[][] inclusionPatterns;
		char[][] exclusionPatterns;
		RubyProject project;
		IPath rootPath;
		int entryKind;
		ISourceFolderRoot root;
		RootInfo(RubyProject project, IPath rootPath, char[][] inclusionPatterns, char[][] exclusionPatterns, int entryKind) {
			this.project = project;
			this.rootPath = rootPath;
			this.inclusionPatterns = inclusionPatterns;
			this.exclusionPatterns = exclusionPatterns;
			this.entryKind = entryKind;
		}
		ISourceFolderRoot getSourceFolderRoot(IResource resource) {
			if (this.root == null) {
				if (resource != null) {
					this.root = this.project.getSourceFolderRoot(resource);
				} else {
					Object target = RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.rootPath, false/*don't check existence*/);
					if (target instanceof IResource) {
						this.root = this.project.getSourceFolderRoot((IResource)target);
					} else {
						this.root = this.project.getSourceFolderRoot(this.rootPath.toOSString());
					}
				}
			}
			return this.root;
		}
		boolean isRootOfProject(IPath path) {
			return this.rootPath.equals(path) && this.project.getProject().getFullPath().isPrefixOf(path);
		}
		public String toString() {
			StringBuffer buffer = new StringBuffer("project="); //$NON-NLS-1$
			if (this.project == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				buffer.append(this.project.getElementName());
			}
			buffer.append("\npath="); //$NON-NLS-1$
			if (this.rootPath == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				buffer.append(this.rootPath.toString());
			}
			buffer.append("\nincluding="); //$NON-NLS-1$
			if (this.inclusionPatterns == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				for (int i = 0, length = this.inclusionPatterns.length; i < length; i++) {
					buffer.append(new String(this.inclusionPatterns[i]));
					if (i < length-1) {
						buffer.append("|"); //$NON-NLS-1$
					}
				}
			}
			buffer.append("\nexcluding="); //$NON-NLS-1$
			if (this.exclusionPatterns == null) {
				buffer.append("null"); //$NON-NLS-1$
			} else {
				for (int i = 0, length = this.exclusionPatterns.length; i < length; i++) {
					buffer.append(new String(this.exclusionPatterns[i]));
					if (i < length-1) {
						buffer.append("|"); //$NON-NLS-1$
					}
				}
			}
			return buffer.toString();
		}
	}
	
	private final static String EXTERNAL_JAR_ADDED = "external jar added"; //$NON-NLS-1$
	private final static String EXTERNAL_JAR_CHANGED = "external jar changed"; //$NON-NLS-1$
	private final static String EXTERNAL_JAR_REMOVED = "external jar removed"; //$NON-NLS-1$
	private final static String EXTERNAL_JAR_UNCHANGED = "external jar unchanged"; //$NON-NLS-1$
	private final static String INTERNAL_JAR_IGNORE = "internal jar ignore"; //$NON-NLS-1$
	
    public static final int DEFAULT_CHANGE_EVENT = 0; // must not collide with
    private final static int NON_RUBY_RESOURCE = -1;
    // ElementChangedEvent
    // event masks
    public static boolean DEBUG;
    public static boolean VERBOSE = false;
    public static boolean PERF = false;

    /*
     * Used to update the RubyModel for <code>IRubyElementDelta</code>s.
     */
    private final ModelUpdater modelUpdater = new ModelUpdater();

    /* A set of IRubyProject whose caches need to be reset */
    private HashSet<IRubyProject> projectCachesToReset = new HashSet<IRubyProject>();

	/* A table from IRubyProject to an array of ISourceFolderRoot.
	 * This table contains the src folder roots of the project that are being deleted.
	 */
	public Map<IRubyProject, ISourceFolderRoot[]> removedRoots;
    
    /*
     * A list of IRubyElement used as a scope for external archives refresh
     * during POST_CHANGE. This is null if no refresh is needed.
     */
    private HashSet<IRubyElement> refreshedElements;

    private DeltaProcessingState state;
    private RubyModelManager manager;

    /*
     * Turns delta firing on/off. By default it is on.
     */
    private boolean isFiring = true;

    /*
     * Queue of deltas created explicily by the Ruby Model that have yet to be
     * fired.
     */
    public ArrayList<IRubyElementDelta> rubyModelDeltas = new ArrayList<IRubyElementDelta>();

    /*
     * Queue of reconcile deltas on working copies that have yet to be fired.
     * This is a table from IWorkingCopy to IRubyElementDelta
     */
    public HashMap<IRubyScript, IRubyElementDelta> reconcileDeltas = new HashMap<IRubyScript, IRubyElementDelta>();

    /*
     * The ruby element that was last created (see createElement(IResource)).
     * This is used as a stack of ruby elements (using getParent() to pop it,
     * and using the various get*(...) to push it.
     */
    private Openable currentElement;
    
	/* A set of IRubyProject whose source folder roots need to be refreshed */
	private HashSet<IRubyProject> rootsToRefresh = new HashSet<IRubyProject>();
	
    /*
     * The <code>RubyElementDelta</code> corresponding to the <code>IResourceDelta</code>
     * being translated.
     */
    private RubyElementDelta currentDelta;
   
    /*
     * Type of event that should be processed no matter what the real event type
     * is.
     */
    public int overridenEventType = -1;
    
	private SourceElementParser sourceElementParserCache;

    public DeltaProcessor(DeltaProcessingState state, RubyModelManager manager) {
        this.state = state;
        this.manager = manager;
    }

    public void registerRubyModelDelta(IRubyElementDelta delta) {
        this.rubyModelDeltas.add(delta);
    }

    public void updateRubyModel(IRubyElementDelta customDelta) {
        if (customDelta == null) {
            for (int i = 0, length = this.rubyModelDeltas.size(); i < length; i++) {
                IRubyElementDelta delta = this.rubyModelDeltas.get(i);
                this.modelUpdater.processRubyDelta(delta);
            }
        } else {
            this.modelUpdater.processRubyDelta(customDelta);
        }

    }

    /*
     * Fire Java Model delta, flushing them after the fact after post_change
     * notification. If the firing mode has been turned off, this has no effect.
     */
    public void fire(IRubyElementDelta customDelta, int eventType) {
        if (!this.isFiring) return;

        if (DEBUG) {
            System.out
                    .println("-----------------------------------------------------------------------------------------------------------------------");//$NON-NLS-1$
        }

        IRubyElementDelta deltaToNotify;
        if (customDelta == null) {
            deltaToNotify = this.mergeDeltas(this.rubyModelDeltas);
        } else {
            deltaToNotify = customDelta;
        }

        // Refresh internal scopes
        // TODO Notify Search scopes of deltas
        // if (deltaToNotify != null) {
        // Iterator scopes = this.manager.searchScopes.keySet().iterator();
        // while (scopes.hasNext()) {
        // AbstractSearchScope scope = (AbstractSearchScope) scopes.next();
        // scope.processDelta(deltaToNotify);
        // }
        // RubyWorkspaceScope workspaceScope = this.manager.workspaceScope;
        // if (workspaceScope != null)
        // workspaceScope.processDelta(deltaToNotify);
        // }

        // Notification

        // Important: if any listener reacts to notification by updating the
        // listeners list or mask, these lists will
        // be duplicated, so it is necessary to remember original lists in a
        // variable (since field values may change under us)
        IElementChangedListener[] listeners = this.state.elementChangedListeners;
        int[] listenerMask = this.state.elementChangedListenerMasks;
        int listenerCount = this.state.elementChangedListenerCount;

        switch (eventType) {
        case DEFAULT_CHANGE_EVENT:
            firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
            fireReconcileDelta(listeners, listenerMask, listenerCount);
            break;
        case ElementChangedEvent.POST_CHANGE:
            firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
            fireReconcileDelta(listeners, listenerMask, listenerCount);
            break;
        }
    }

    /*
     * Merges all awaiting deltas.
     */
    private IRubyElementDelta mergeDeltas(Collection deltas) {
        if (deltas.size() == 0) return null;
        if (deltas.size() == 1) return (IRubyElementDelta) deltas.iterator().next();

        if (VERBOSE) {
            System.out
                    .println("MERGING " + deltas.size() + " DELTAS [" + Thread.currentThread() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        Iterator iterator = deltas.iterator();
        RubyElementDelta rootDelta = new RubyElementDelta(this.manager.rubyModel);
        boolean insertedTree = false;
        while (iterator.hasNext()) {
            RubyElementDelta delta = (RubyElementDelta) iterator.next();
            if (VERBOSE) {
                System.out.println(delta.toString());
            }
            IRubyElement element = delta.getElement();
            if (this.manager.rubyModel.equals(element)) {
                IRubyElementDelta[] children = delta.getAffectedChildren();
                for (int j = 0; j < children.length; j++) {
                    RubyElementDelta projectDelta = (RubyElementDelta) children[j];
                    rootDelta.insertDeltaTree(projectDelta.getElement(), projectDelta);
                    insertedTree = true;
                }
                IResourceDelta[] resourceDeltas = delta.getResourceDeltas();
                if (resourceDeltas != null) {
                    for (int i = 0, length = resourceDeltas.length; i < length; i++) {
                        rootDelta.addResourceDelta(resourceDeltas[i]);
                        insertedTree = true;
                    }
                }
            } else {
                rootDelta.insertDeltaTree(element, delta);
                insertedTree = true;
            }
        }
        if (insertedTree) return rootDelta;
        return null;
    }

    private void firePostChangeDelta(IRubyElementDelta deltaToNotify,
            IElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {

        // post change deltas
        if (DEBUG) {
            System.out.println("FIRING POST_CHANGE Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
            System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
        }
        if (deltaToNotify != null) {
            // flush now so as to keep listener reactions to post their own
            // deltas for subsequent iteration
            this.flush();

            notifyListeners(deltaToNotify, ElementChangedEvent.POST_CHANGE, listeners,
                    listenerMask, listenerCount);
        }
    }

    private void fireReconcileDelta(IElementChangedListener[] listeners, int[] listenerMask,
            int listenerCount) {

        IRubyElementDelta deltaToNotify = mergeDeltas(this.reconcileDeltas.values());
        if (DEBUG) {
            System.out.println("FIRING POST_RECONCILE Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
            System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
        }
        if (deltaToNotify != null) {
            // flush now so as to keep listener reactions to post their own
            // deltas for subsequent iteration
            this.reconcileDeltas = new HashMap<IRubyScript, IRubyElementDelta>();

            notifyListeners(deltaToNotify, ElementChangedEvent.POST_RECONCILE, listeners,
                    listenerMask, listenerCount);
        }
    }

    /*
     * Flushes all deltas without firing them.
     */
    public void flush() {
        this.rubyModelDeltas = new ArrayList<IRubyElementDelta>();
    }

    private void notifyListeners(IRubyElementDelta deltaToNotify, int eventType,
            IElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {
        final ElementChangedEvent extraEvent = new ElementChangedEvent(deltaToNotify, eventType);
        for (int i = 0; i < listenerCount; i++) {
            if ((listenerMask[i] & eventType) != 0) {
                final IElementChangedListener listener = listeners[i];
                long start = -1;
                if (VERBOSE) {
                    System.out.print("Listener #" + (i + 1) + "=" + listener.toString());//$NON-NLS-1$//$NON-NLS-2$
                    start = System.currentTimeMillis();
                }
                // wrap callbacks with Safe runnable for subsequent listeners to
                // be called when some are causing grief
                SafeRunner.run(new ISafeRunnable() {

                    public void handleException(Throwable exception) {
                        Util
                                .log(exception,
                                        "Exception occurred in listener of Java element change notification"); //$NON-NLS-1$
                    }

                    public void run() throws Exception {
                        PerformanceStats stats = null;
                        if (PERF) {
                            stats = PerformanceStats.getStats(RubyModelManager.DELTA_LISTENER_PERF,
                                    listener);
                            stats.startRun();
                        }
                        listener.elementChanged(extraEvent);
                        if (PERF) {
                            stats.endRun();
                        }
                    }
                });
                if (VERBOSE) {
                    System.out.println(" -> " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    /*
     * Notification that some resource changes have happened on the platform,
     * and that the Ruby Model should update any required internal structures
     * such that its elements remain consistent. Translates <code>IResourceDeltas</code>
     * into <code>IRubyElementDeltas</code>.
     * 
     * @see IResourceDelta
     * @see IResource
     */
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getSource() instanceof IWorkspace) {
            int eventType = this.overridenEventType == -1 ? event.getType()
                    : this.overridenEventType;
            IResource resource = event.getResource();
            IResourceDelta delta = event.getDelta();

            switch (eventType) {
            case IResourceChangeEvent.PRE_DELETE:
                try {
                    if (resource.getType() == IResource.PROJECT
                            && ((IProject) resource).hasNature(RubyCore.NATURE_ID)) {

                        deleting((IProject) resource);
                    }
                } catch (CoreException e) {
                    // project doesn't exist or is not open: ignore
                }
                return;

            case IResourceChangeEvent.POST_CHANGE:
                if (isAffectedBy(delta)) { // avoid populating for SYNC or
                    // MARKER deltas
                    try {
                        try {
                            stopDeltas();
                            checkProjectsBeingAddedOrRemoved(delta);
                            if (this.refreshedElements != null) {
								createExternalArchiveDelta(null);
							}
                            IRubyElementDelta translatedDelta = processResourceDelta(delta);
                            if (translatedDelta != null) {
                                registerRubyModelDelta(translatedDelta);
                            }
                        } finally {
                            startDeltas();
                        }
                        IElementChangedListener[] listeners;
						int listenerCount;
						synchronized (this.state) {
							listeners = this.state.elementChangedListeners;
							listenerCount = this.state.elementChangedListenerCount;
						}
						notifyTypeHierarchies(listeners, listenerCount);
						fire(null, ElementChangedEvent.POST_CHANGE);
                    } finally {
						// workaround for bug 15168 circular errors not reported 
						this.state.resetOldRubyProjectNames();
						this.removedRoots = null;
                    }
                }
                return;
                
            case IResourceChangeEvent.PRE_BUILD :
			    DeltaProcessingState.ProjectUpdateInfo[] updates = this.state.removeAllProjectUpdates();
				if (updates != null) {
				    for (int i = 0, length = updates.length; i < length; i++) {
				        try {
					        updates[i].updateProjectReferencesIfNecessary();
				        } catch(RubyModelException e) {
				            // do nothing
				        }
				    }
				}
				// this.processPostChange = false;
				if(isAffectedBy(delta)) { // avoid populating for SYNC or MARKER deltas
					updateLoadpathMarkers(delta, updates);
					RubyBuilder.buildStarting();
				}
				// does not fire any deltas
				return;
				
            case IResourceChangeEvent.POST_BUILD :
				RubyBuilder.buildFinished();
				return;
            }
        }
    }
    
    private void notifyTypeHierarchies(IElementChangedListener[] listeners, int listenerCount) {
		for (int i= 0; i < listenerCount; i++) {
			final IElementChangedListener listener = listeners[i];
			if (!(listener instanceof TypeHierarchy)) continue;

			// wrap callbacks with Safe runnable for subsequent listeners to be called when some are causing grief
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					Util.log(exception, "Exception occurred in listener of Ruby element change notification"); //$NON-NLS-1$
				}
				public void run() throws Exception {
					TypeHierarchy typeHierarchy = (TypeHierarchy)listener;
					if (typeHierarchy.hasFineGrainChanges()) {
						// case of changes in primary working copies
						typeHierarchy.needsRefresh = true;
						typeHierarchy.fireChange();
					}
				}
			});
		}
	}

    /*
	 * Update the .loadpath format, missing entries and cycle markers for the projects affected by the given delta.
	 */
	private void updateLoadpathMarkers(IResourceDelta delta, DeltaProcessingState.ProjectUpdateInfo[] updates) {
		
		Map<RubyProject, ILoadpathEntry[]> preferredClasspaths = new HashMap<RubyProject, ILoadpathEntry[]>(5);
		Map preferredOutputs = new HashMap(5);
		HashSet<IPath> affectedProjects = new HashSet<IPath>(5);
		
		// read .loadpath files that have changed, and create markers if format is wrong or if an entry cannot be found
		RubyModel.flushExternalFileCache();
		updateLoadpathMarkers(delta, affectedProjects, preferredClasspaths, preferredOutputs); 
	
		// update .loadpath format markers for affected projects (dependent projects 
		// or projects that reference a library in one of the projects that have changed)
		if (!affectedProjects.isEmpty()) {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] projects = workspaceRoot.getProjects();
			int length = projects.length;
			for (int i = 0; i < length; i++){
				IProject project = projects[i];
				RubyProject rubyProject = (RubyProject)RubyCore.create(project);
				if (preferredClasspaths.get(rubyProject) == null) { // not already updated
					try {
						IPath projectPath = project.getFullPath();
						ILoadpathEntry[] classpath = rubyProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/); // allowed to reuse model cache
						for (int j = 0, cpLength = classpath.length; j < cpLength; j++) {
							ILoadpathEntry entry = classpath[j];
							switch (entry.getEntryKind()) {
								case ILoadpathEntry.CPE_PROJECT:
									if (affectedProjects.contains(entry.getPath())) {
										rubyProject.updateLoadpathMarkers(null, null);
									}
									break;
								case ILoadpathEntry.CPE_LIBRARY:
									IPath entryPath = entry.getPath();
									IPath libProjectPath = entryPath.removeLastSegments(entryPath.segmentCount()-1);
									if (!libProjectPath.equals(projectPath) // if library contained in another project
											&& affectedProjects.contains(libProjectPath)) {
										rubyProject.updateLoadpathMarkers(null, null);
									}
									break;
							}
						}
					} catch(RubyModelException e) {
							// project no longer exists
					}
				}
			}
		}
		if (!affectedProjects.isEmpty() || updates != null) {
			// update all cycle markers since the given delta may have affected cycles
			if (updates != null) {
				for (int i = 0, length = updates.length; i < length; i++) {
					DeltaProcessingState.ProjectUpdateInfo info = updates[i];
					if (!preferredClasspaths.containsKey(info.project))
						preferredClasspaths.put(info.project, info.newResolvedPath);
				}
			}
			try {
				RubyProject.updateAllCycleMarkers(preferredClasspaths);
			} catch (RubyModelException e) {
				// project no longer exist
			}
		}
	}
    
	/*
	 * Check whether .classpath files are affected by the given delta.
	 * Creates/removes problem markers if needed.
	 * Remember the affected projects in the given set.
	 */
	private void updateLoadpathMarkers(IResourceDelta delta, HashSet<IPath> affectedProjects, Map preferredClasspaths, Map preferredOutputs) {
		IResource resource = delta.getResource();
		boolean processChildren = false;

		switch (resource.getType()) {
	
			case IResource.ROOT :
				if (delta.getKind() == IResourceDelta.CHANGED) {
					processChildren = true;
				}
				break;
			case IResource.PROJECT :
				IProject project = (IProject)resource;
				int kind = delta.getKind();
				boolean isRubyProject = RubyProject.hasRubyNature(project);
				switch (kind) {
					case IResourceDelta.ADDED:
						processChildren = isRubyProject;
						affectedProjects.add(project.getFullPath());
						break;
					case IResourceDelta.CHANGED:
						processChildren = isRubyProject;
						if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
							// project opened or closed: remember  project and its dependents
							affectedProjects.add(project.getFullPath());
							if (isRubyProject) {
								RubyProject rubyProject = (RubyProject)RubyCore.create(project);
								rubyProject.updateLoadpathMarkers(preferredClasspaths, preferredOutputs); // in case .loadpath got modified while closed
							}
						} else if ((delta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
							boolean wasRubyProject = this.state.findRubyProject(project.getName()) != null;
							if (wasRubyProject && !isRubyProject) {
								// project no longer has Ruby nature, discard Ruby related obsolete markers
								affectedProjects.add(project.getFullPath());
								// flush loadpath markers
								RubyProject javaProject = (RubyProject)RubyCore.create(project);
								javaProject.
									flushLoadpathProblemMarkers(
										true, // flush cycle markers
										true  //flush loadpath format markers
									);
									
								// remove problems and tasks created  by the builder
								RubyBuilder.removeProblemsAndTasksFor(project);
							}
						} else if (isRubyProject) {
							// check if all entries exist
							try {
								RubyProject javaProject = (RubyProject)RubyCore.create(project);
								javaProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, 	true/*generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
							} catch (RubyModelException e) {
								// project doesn't exist: ignore
							}
						}
						break;
					case IResourceDelta.REMOVED:
						affectedProjects.add(project.getFullPath());
						break;
				}
				break;
			case IResource.FILE :
				/* check loadpath file change */
				IFile file = (IFile) resource;
				if (file.getName().equals(RubyProject.LOADPATH_FILENAME)) {
					affectedProjects.add(file.getProject().getFullPath());
					RubyProject rubyProject = (RubyProject)RubyCore.create(file.getProject());
					rubyProject.updateLoadpathMarkers(preferredClasspaths, preferredOutputs);
					break;
				}
//				/* check custom preference file change */
//				if (file.getName().equals(JavaProject.PREF_FILENAME)) {
//					reconcilePreferenceFileUpdate(delta, file, project);
//					break;
//				}
				break;
		}
		if (processChildren) {
			IResourceDelta[] children = delta.getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				updateLoadpathMarkers(children[i], affectedProjects, preferredClasspaths, preferredOutputs);
			}
		}
	}
	
    /*
     * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code>
     * into the corresponding set of <code>IRubyElementDelta</code>, rooted
     * in the relevant <code>RubyModel</code>s.
     */
    private IRubyElementDelta processResourceDelta(IResourceDelta changes) {

        try {
            IRubyModel model = this.manager.getRubyModel();
            if (!model.isOpen()) {
                // force opening of ruby model so that ruby element delta are
                // reported
                try {
                    model.open(null);
                } catch (RubyModelException e) {
                    if (VERBOSE) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            this.state.initializeRoots();
            this.currentElement = null;

            // get the workspace delta, and start processing there.
            IResourceDelta[] deltas = changes.getAffectedChildren();
            for (int i = 0; i < deltas.length; i++) {
                IResourceDelta delta = deltas[i];
                IResource res = delta.getResource();

                // find out the element type
                RootInfo rootInfo = null;
                int elementType;
                IProject proj = (IProject) res;
                boolean wasRubyProject = this.state.findRubyProject(proj.getName()) != null;
                boolean isRubyProject = RubyProject.hasRubyNature(proj);
                if (!wasRubyProject && !isRubyProject) {
                    elementType = NON_RUBY_RESOURCE;
                } else {
                	rootInfo = this.enclosingRootInfo(res.getFullPath(), delta.getKind());
					if (rootInfo != null && rootInfo.isRootOfProject(res.getFullPath())) {
						elementType = IRubyElement.SOURCE_FOLDER_ROOT;
					} else {
						elementType = IRubyElement.RUBY_PROJECT; 
					}
                }
                              
                // traverse delta
                this.traverseDelta(delta, elementType, rootInfo);

                if (elementType == NON_RUBY_RESOURCE
                        || (wasRubyProject != isRubyProject && (delta.getKind()) == IResourceDelta.CHANGED)) { // project
                    // has
                    // changed
                    // nature
                    // (description
                    // or
                    // open/closed)
                    try {
                        // add child as non ruby resource
                        nonRubyResourcesChanged((RubyModel) model, delta);
                    } catch (RubyModelException e) {
                        // ruby model could not be opened
                    }
                }

            }
            resetProjectCaches();

            return this.currentDelta;
        } finally {
            this.currentDelta = null;
            this.rootsToRefresh.clear();
            this.projectCachesToReset.clear();
        }
    }
    
	/*
	 * Finds the root info this path is included in.
	 * Returns null if not found.
	 */
	private RootInfo enclosingRootInfo(IPath path, int kind) {
		while (path != null && path.segmentCount() > 0) {
			RootInfo rootInfo =  this.rootInfo(path, kind);
			if (rootInfo != null) return rootInfo;
			path = path.removeLastSegments(1);
		}
		return null;
	}
	
	/*
	 * Returns the root info for the given path. Look in the old roots table if kind is REMOVED.
	 */
	private RootInfo rootInfo(IPath path, int kind) {
		if (kind == IResourceDelta.REMOVED) {
			return (RootInfo)this.state.oldRoots.get(path);
		}
		return (RootInfo)this.state.roots.get(path);
	}
    
	/* 
	 * Refresh source folder roots of projects that were affected
	 */
	private void refreshSourceFolderRoots() {
		Iterator iterator = this.rootsToRefresh.iterator();
		while (iterator.hasNext()) {
			RubyProject project = (RubyProject)iterator.next();
			project.updateSourceFolderRoots();
		}
	}

    private RubyElementDelta currentDelta() {
        if (this.currentDelta == null) {
            this.currentDelta = new RubyElementDelta(this.manager.getRubyModel());
        }
        return this.currentDelta;
    }

    /*
     * Traverse the set of projects which have changed namespace, and reset
     * their caches and their dependents
     */
    private void resetProjectCaches() {
        Iterator iterator = this.projectCachesToReset.iterator();
        HashMap projectDepencies = this.state.projectDependencies;
        HashSet<IRubyProject> affectedDependents = new HashSet<IRubyProject>();
        while (iterator.hasNext()) {
            RubyProject project = (RubyProject) iterator.next();
            project.resetCaches();
            addDependentProjects(project, projectDepencies, affectedDependents);
        }
        // reset caches of dependent projects
        iterator = affectedDependents.iterator();
        while (iterator.hasNext()) {
            RubyProject project = (RubyProject) iterator.next();
            project.resetCaches();
        }
    }

    /*
     * Adds the dependents of the given project to the list of the projects to
     * update.
     */
    private void addDependentProjects(IRubyProject project, HashMap projectDependencies,
            HashSet<IRubyProject> result) {
        IRubyProject[] dependents = (IRubyProject[]) projectDependencies.get(project);
        if (dependents == null) return;
        for (int i = 0, length = dependents.length; i < length; i++) {
            IRubyProject dependent = dependents[i];
            if (result.contains(dependent)) continue; // no need to go further
            // as the project is
            // already known
            result.add(dependent);
            addDependentProjects(dependent, projectDependencies, result);
        }
    }

    /*
     * Generic processing for elements with changed contents:<ul> <li>The
     * element is closed such that any subsequent accesses will re-open the
     * element reflecting its new structure. <li>An entry is made in the delta
     * reporting a content change (K_CHANGE with F_CONTENT flag set). </ul>
     */
    private void nonRubyResourcesChanged(Openable element, IResourceDelta delta)
            throws RubyModelException {

		// reset non-ruby resources if element was open
		if (element.isOpen()) {
			RubyElementInfo info = (RubyElementInfo)element.getElementInfo();
			switch (element.getElementType()) {
				case IRubyElement.RUBY_MODEL :
					((RubyModelInfo) info).nonRubyResources = null;
					currentDelta().addResourceDelta(delta);
					return;
				case IRubyElement.RUBY_PROJECT :
					((RubyProjectElementInfo) info).setNonRubyResources(null);
	
					// if a package fragment root is the project, clear it too
					RubyProject project = (RubyProject) element;
					SourceFolderRoot projectRoot =
						(SourceFolderRoot) project.getSourceFolderRoot(project.getProject());
					if (projectRoot.isOpen()) {
						((SourceFolderRootInfo) projectRoot.getElementInfo()).setNonRubyResources(
							null);
					}
					break;
				case IRubyElement.SOURCE_FOLDER :
					 ((SourceFolderInfo) info).setNonRubyResources(null);
					break;
				case IRubyElement.SOURCE_FOLDER_ROOT :
					 ((SourceFolderRootInfo) info).setNonRubyResources(null);
			}
		}

		RubyElementDelta current = currentDelta();
		RubyElementDelta elementDelta = current.find(element);
		if (elementDelta == null) {
			// don't use find after creating the delta as it can be null (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63434)
			elementDelta = current.changed(element, IRubyElementDelta.F_CONTENT);
		}
		elementDelta.addResourceDelta(delta);
    }

    /*
     * Turns the firing mode to off. That is, deltas that are/have been
     * registered will not be fired until deltas are started again.
     */
    private void stopDeltas() {
        this.isFiring = false;
    }

    /*
     * Turns the firing mode to on. That is, deltas that are/have been
     * registered will be fired.
     */
    private void startDeltas() {
        this.isFiring = true;
    }

    /*
     * Note that the project is about to be deleted.
     */
    private void deleting(IProject project) {
		
		try {
			// discard indexing jobs that belong to this project so that the project can be 
			// deleted without interferences from the index manager
			// FIXME Uncomment when we have a more sophisticated index manager
//			this.manager.indexManager.discardJobs(project.getName());

			RubyProject rubyProject = (RubyProject)RubyCore.create(project);
			
			// remember roots of this project
			if (this.removedRoots == null) {
				this.removedRoots = new HashMap<IRubyProject, ISourceFolderRoot[]>();
			}
			if (rubyProject.isOpen()) {
				this.removedRoots.put(rubyProject, rubyProject.getSourceFolderRoots());
			} else {
				// compute roots without opening project
				this.removedRoots.put(
					rubyProject, 
					rubyProject.computeSourceFolderRoots(
						rubyProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/), 
						false,
						null /*no reverse map*/));
			}
			
			rubyProject.close();

			// workaround for bug 15168 circular errors not reported
			this.state.getOldRubyProjecNames(); // foce list to be computed
			
			this.removeFromParentInfo(rubyProject);

			// remove preferences from per project info
			this.manager.resetProjectPreferences(rubyProject);	
		} catch (RubyModelException e) {
			// java project doesn't exist: ignore
		}
    }

    /*
     * Removes the given element from its parents cache of children. If the
     * element does not have a parent, or the parent is not currently open, this
     * has no effect.
     */
    private void removeFromParentInfo(Openable child) {

        Openable parent = (Openable) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                RubyElementInfo info = (RubyElementInfo) parent.getElementInfo();
                info.removeChild(child);
            } catch (RubyModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /*
     * Returns whether a given delta contains some information relevant to the
     * JavaModel, in particular it will not consider SYNC or MARKER only deltas.
     */
    private boolean isAffectedBy(IResourceDelta rootDelta) {
        // if (rootDelta == null) System.out.println("NULL DELTA");
        // long start = System.currentTimeMillis();
        if (rootDelta != null) {
            // use local exception to quickly escape from delta traversal
            class FoundRelevantDeltaException extends RuntimeException {

                private static final long serialVersionUID = 7137113252936111022L; // backward
                // compatible
                // only the class name is used (to differenciate from other
                // RuntimeExceptions)
            }
            try {
                rootDelta.accept(new IResourceDeltaVisitor() {

                    public boolean visit(IResourceDelta delta) /*
                                                                 * throws
                                                                 * CoreException
                                                                 */{
                        switch (delta.getKind()) {
                        case IResourceDelta.ADDED:
                        case IResourceDelta.REMOVED:
                            throw new FoundRelevantDeltaException();
                        case IResourceDelta.CHANGED:
                            // if any flag is set but SYNC or MARKER, this delta
                            // should be considered
                            if (delta.getAffectedChildren().length == 0 // only
                                    // check
                                    // leaf
                                    // delta
                                    // nodes
                                    && (delta.getFlags() & ~(IResourceDelta.SYNC | IResourceDelta.MARKERS)) != 0) { throw new FoundRelevantDeltaException(); }
                        }
                        return true;
                    }
                });
            } catch (FoundRelevantDeltaException e) {
                // System.out.println("RELEVANT DELTA detected in: "+
                // (System.currentTimeMillis() - start));
                return true;
            } catch (CoreException e) { // ignore delta if not able to traverse
            }
        }
        // System.out.println("IGNORE SYNC DELTA took: "+
        // (System.currentTimeMillis() - start));
        return false;
    }

    /*
     * Process the given delta and look for projects being added, opened, closed
     * or with a java nature being added or removed. Note that projects being
     * deleted are checked in deleting(IProject). In all cases, add the
     * project's dependents to the list of projects to update so that the
     * classpath related markers can be updated.
     */
    private void checkProjectsBeingAddedOrRemoved(IResourceDelta delta) {
        IResource resource = delta.getResource();
        boolean processChildren = false;

        switch (resource.getType()) {
        case IResource.ROOT:
//        	 workaround for bug 15168 circular errors not reported 
			this.state.getOldRubyProjecNames(); // force list to be computed
            processChildren = true;
            break;
        case IResource.PROJECT:
            // NB: No need to check project's nature as if the project is not a
            // ruby project:
            // - if the project is added or changed this is a noop for
            // projectsBeingDeleted
            // - if the project is closed, it has already lost its ruby nature
            IProject project = (IProject) resource;
            RubyProject rubyProject = (RubyProject) RubyCore.create(project);
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
				this.manager.batchContainerInitializations = true;
				
				// remember project and its dependents
				this.addToRootsToRefreshWithDependents(rubyProject);
				
                // workaround for bug 15168 circular errors not reported
                if (RubyProject.hasRubyNature(project)) {
                    this.addToParentInfo(rubyProject);
					// ensure project references are updated (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=121569)
					try {
						this.state.updateProjectReferences(
							rubyProject, 
							null/*no old loadpath*/, 
							null/*compute new resolved loadpath later*/, 
							null/*read raw loadpath later*/, 
							false/*cannot change resources*/);
					} catch (RubyModelException e1) {
						// project always exists
					}					
				}
				
				this.state.rootsAreStale = true; 
				break;

            case IResourceDelta.CHANGED:
                if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
					this.manager.batchContainerInitializations = true;
					
					// project opened or closed: remember  project and its dependents
					this.addToRootsToRefreshWithDependents(rubyProject);
					
                    // workaround for bug 15168 circular errors not reported
                    if (project.isOpen()) {
                        if (RubyProject.hasRubyNature(project)) {
                            this.addToParentInfo(rubyProject);
                        }
                    } else {
                        try {
                            rubyProject.close();
                        } catch (RubyModelException e) {
                            // ruby project doesn't exist: ignore
                        }
                        this.removeFromParentInfo(rubyProject);
                        this.manager.removePerProjectInfo(rubyProject);
                        this.manager.containerRemove(rubyProject);
                    }
                    this.state.rootsAreStale = true;
                } else if ((delta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
                    boolean wasJavaProject = this.state.findRubyProject(project.getName()) != null;
                    boolean isJavaProject = RubyProject.hasRubyNature(project);
                    if (wasJavaProject != isJavaProject) {
                    	this.manager.batchContainerInitializations = true;
						
						// ruby nature added or removed: remember  project and its dependents
						this.addToRootsToRefreshWithDependents(rubyProject);

                        // workaround for bug 15168 circular errors not reported
                        if (isJavaProject) {
                            this.addToParentInfo(rubyProject);
                        } else {
                            // remove classpath cache so that initializeRoots()
                            // will not consider the project has a classpath
                            this.manager.removePerProjectInfo((RubyProject) RubyCore
                                    .create(project));
//                          remove container cache for this project
							this.manager.containerRemove(rubyProject);
                            // close project
                            try {
                                rubyProject.close();
                            } catch (RubyModelException e) {
                                // ruby project doesn't exist: ignore
                            }
                            this.removeFromParentInfo(rubyProject);
                        }
                        this.state.rootsAreStale = true;
                    } else {
                        // in case the project was removed then added then
                        // changed (see bug 19799)
                        if (isJavaProject) { // need nature check - 18698
                            this.addToParentInfo(rubyProject);
                            processChildren = true;
                        }
                    }
                } else {
                    // workaround for bug 15168 circular errors not reported
                    // in case the project was removed then added then changed
                    if (RubyProject.hasRubyNature(project)) { // need nature
                        // check - 18698
                        this.addToParentInfo(rubyProject);
                        processChildren = true;
                    }
                }
                break;

            case IResourceDelta.REMOVED:
				this.manager.batchContainerInitializations = true;
				
				// remove classpath cache so that initializeRoots() will not consider the project has a classpath
				this.manager.removePerProjectInfo(rubyProject);
				// remove container cache for this project
				this.manager.containerRemove(rubyProject);
				
				this.state.rootsAreStale = true;
				break;
            }

            // in all cases, refresh the external jars for this project
            addForRefresh(rubyProject);

            break;
        case IResource.FILE:
        	IFile file = (IFile) resource;
			/* loadpath file change */
			if (file.getName().equals(RubyProject.LOADPATH_FILENAME)) {
				this.manager.batchContainerInitializations = true;
				reconcileLoadpathFileUpdate(delta, (RubyProject)RubyCore.create(file.getProject()));
				this.state.rootsAreStale = true;
			}
			break;
        }
        if (processChildren) {
            IResourceDelta[] children = delta.getAffectedChildren();
            for (int i = 0; i < children.length; i++) {
                checkProjectsBeingAddedOrRemoved(children[i]);
            }
        }
    }
    
	/*
	 * Adds the given project and its dependents to the list of the roots to refresh.
	 */
	private void addToRootsToRefreshWithDependents(IRubyProject rubyProject) {
		this.rootsToRefresh.add(rubyProject);
		this.addDependentProjects(rubyProject, this.state.projectDependencies, this.rootsToRefresh);
	}

    /*
     * Adds the given element to the list of elements used as a scope for
     * external jars refresh.
     */
    public void addForRefresh(IRubyElement element) {
        if (this.refreshedElements == null) {
            this.refreshedElements = new HashSet<IRubyElement>();
        }
        this.refreshedElements.add(element);
    }

    /*
     * Adds the given child handle to its parent's cache of children.
     */
    private void addToParentInfo(Openable child) {
        Openable parent = (Openable) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                RubyElementInfo info = (RubyElementInfo) parent.getElementInfo();
                info.addChild(child);
            } catch (RubyModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /*
     * Converts an <code>IResourceDelta</code> and its children into the
     * corresponding <code>IRubyElementDelta</code>s.
     */
    private void traverseDelta(IResourceDelta delta, int elementType, RootInfo rootInfo) {

        IResource res = delta.getResource();
        
		// set stack of elements
		if (this.currentElement == null && rootInfo != null) {
			this.currentElement = rootInfo.project;
		}

        // process current delta
        boolean processChildren = true;
        if (res instanceof IProject) {
            processChildren = updateCurrentDeltaAndIndex(delta, 
            		elementType == IRubyElement.SOURCE_FOLDER_ROOT ? 
					IRubyElement.RUBY_PROJECT : // case of prj=src, 
						elementType, rootInfo);
        } else if (rootInfo != null) {
			processChildren = this.updateCurrentDeltaAndIndex(delta, elementType, rootInfo);
		} else {
            // not yet inside a package fragment root
            processChildren = true;
        }

        // process children if needed
        if (processChildren) {
            IResourceDelta[] children = delta.getAffectedChildren();
            boolean oneChildOnLoadpath = false;
            int length = children.length;
            IResourceDelta[] orphanChildren = null;
            Openable parent = null;
            boolean isValidParent = true;
            
            
			for (int i = 0; i < length; i++) {
				IResourceDelta child = children[i];
				IResource childRes = child.getResource();
				
				// find out whether the child is a source folder root of the current project
				IPath childPath = childRes.getFullPath();
				int childKind = child.getKind();
				RootInfo childRootInfo = this.rootInfo(childPath, childKind);
				if (childRootInfo != null && !childRootInfo.isRootOfProject(childPath)) {
					// package fragment root of another project (dealt with later)
					childRootInfo = null;
				}
				
				// compute child type
				int childType = 
					this.elementType(
						childRes, 
						childKind,
						elementType, 
						rootInfo == null ? childRootInfo : rootInfo
					);
						
				// is childRes in the output folder and is it filtered out ?
				boolean isResFilteredFromOutput = false;

				boolean isNestedRoot = rootInfo != null && childRootInfo != null;
				if (!isResFilteredFromOutput 
						&& !isNestedRoot) { // do not treat as non-ruby rsc if nested root

					this.traverseDelta(child, childType, rootInfo == null ? childRootInfo : rootInfo); // traverse delta for child in the same project

					if (childType == NON_RUBY_RESOURCE) {
						if (rootInfo != null) { // if inside a source folder root
							if (!isValidParent) continue; 
							if (parent == null) {
								// find the parent of the non-ruby resource to attach to
								if (this.currentElement == null
										|| !rootInfo.project.equals(this.currentElement.getRubyProject())) { // note if currentElement is the IRubyModel, getJavaProject() is null
									// force the currentProject to be used
									this.currentElement = rootInfo.project;
								}
								if (elementType == IRubyElement.RUBY_PROJECT
									|| (elementType == IRubyElement.SOURCE_FOLDER_ROOT 
										&& res instanceof IProject)) { 
									// NB: attach non-ruby resource to project (not to its package fragment root)
									parent = rootInfo.project;
								} else {
									parent = this.createElement(res, elementType, rootInfo);
								}
								if (parent == null) {
									isValidParent = false;
									continue;
								}
							}
							// add child as non ruby resource
							try {
								nonRubyResourcesChanged(parent, child);
							} catch (RubyModelException e) {
								// ignore
							}
						} else {
							// the non-ruby resource (or its parent folder) will be attached to the ruby project
							if (orphanChildren == null) orphanChildren = new IResourceDelta[length];
							orphanChildren[i] = child;
						}
					} else {
						oneChildOnLoadpath = true;
					}
				} else {
					oneChildOnLoadpath = true; // to avoid reporting child delta as non-ruby resource delta
				}
								
				// if child is a nested root 
				// or if it is not a package fragment root of the current project
				// but it is a package fragment root of another project, traverse delta too
				if (isNestedRoot 
						|| (childRootInfo == null && (childRootInfo = this.rootInfo(childPath, childKind)) != null)) {
					this.traverseDelta(child, IRubyElement.SOURCE_FOLDER_ROOT, childRootInfo); // binary output of childRootInfo.project cannot be this root
				}
	
				// if the child is a package fragment root of one or several other projects
				ArrayList rootList;
				if ((rootList = this.otherRootsInfo(childPath, childKind)) != null) {
					Iterator iterator = rootList.iterator();
					while (iterator.hasNext()) {
						childRootInfo = (RootInfo) iterator.next();
						this.traverseDelta(child, IRubyElement.SOURCE_FOLDER_ROOT, childRootInfo); // binary output of childRootInfo.project cannot be this root
					}
				}
			}            
            if (orphanChildren != null && (oneChildOnLoadpath // orphan
                    // children are
                    // siblings of a
                    // package
                    // fragment root
                    || res instanceof IProject)) { // non-ruby resource
                // directly under a project

                // attach orphan children
                IProject rscProject = res.getProject();
                RubyProject adoptiveProject = (RubyProject) RubyCore.create(rscProject);
                if (adoptiveProject != null && RubyProject.hasRubyNature(rscProject)) { // delta
                    // iff
                    // Ruby
                    // project
                    // (18698)
                    for (int i = 0; i < length; i++) {
                        if (orphanChildren[i] != null) {
                            try {
                                nonRubyResourcesChanged(adoptiveProject, orphanChildren[i]);
                            } catch (RubyModelException e) {
                                // ignore
                            }
                        }
                    }
                }
            } // else resource delta will be added by parent
        } // else resource delta will be added by parent
    }

	/*
	 * Returns the other root infos for the given path. Look in the old other roots table if kind is REMOVED.
	 */
	private ArrayList otherRootsInfo(IPath path, int kind) {
		if (kind == IResourceDelta.REMOVED) {
			return (ArrayList)this.state.oldOtherRoots.get(path);
		}
		return (ArrayList)this.state.otherRoots.get(path);
	}	

    /*
     * Closes the given element, which removes it from the cache of open
     * elements.
     */
    private void close(Openable element) {
        try {
            element.close();
        } catch (RubyModelException e) {
            // do nothing
        }
    }

    /*
     * Creates the openables corresponding to this resource. Returns null if
     * none was found.
     */
    private Openable createElement(IResource resource, int elementType, RootInfo rootInfo) {
        if (resource == null) return null;

        IPath path = resource.getFullPath();
        IRubyElement element = null;
        switch (elementType) {

        case IRubyElement.RUBY_PROJECT:

            // note that non-ruby resources rooted at the project level will
            // also enter this code with
            // an elementType PROJECT (see #elementType(...)).
            if (resource instanceof IProject) {

                this.popUntilPrefixOf(path);

                if (this.currentElement != null
                        && this.currentElement.getElementType() == IRubyElement.RUBY_PROJECT
                        && ((IRubyProject) this.currentElement).getProject().equals(resource)) { return this.currentElement; }
                
                if  (rootInfo != null && rootInfo.project.getProject().equals(resource)){
					element = rootInfo.project;
					break;
				}
                
                IProject proj = (IProject) resource;
                if (RubyProject.hasRubyNature(proj)) {
                    element = RubyCore.create(proj);
                } else {
                    // java project may have been been closed or removed (look
                    // for
                    // element amongst old ruby project s list).
                    element = this.manager.getRubyModel().findRubyProject(proj);
                }
            }
            break;
        case IRubyElement.SOURCE_FOLDER_ROOT:
			element = rootInfo == null ? RubyCore.create(resource) : rootInfo.getSourceFolderRoot(resource);
			break;
		case IRubyElement.SOURCE_FOLDER:
			if (rootInfo != null) {
				if (rootInfo.project.contains(resource)) {
					SourceFolderRoot root = (SourceFolderRoot) rootInfo.getSourceFolderRoot(null);
					// create package handle
					IPath pkgPath = path.removeFirstSegments(rootInfo.rootPath.segmentCount());
					String[] pkgName = pkgPath.segments();
					element = root.getSourceFolder(pkgName);
				}
			} else {
				// find the element that encloses the resource
				this.popUntilPrefixOf(path);
			
				if (this.currentElement == null) {
					element = RubyCore.create(resource);
				} else {
					// find the root
					SourceFolderRoot root = this.currentElement.getSourceFolderRoot();
					if (root == null) {
						element =  RubyCore.create(resource);
					} else if (((RubyProject)root.getRubyProject()).contains(resource)) {
						// create package handle
						IPath pkgPath = path.removeFirstSegments(root.getPath().segmentCount());
						String[] pkgName = pkgPath.segments();
						element = root.getSourceFolder(pkgName);
					}
				}
			}
			break;
        case IRubyElement.SCRIPT:
            // find the element that encloses the resource
            this.popUntilPrefixOf(path);
            element = RubyCore.create(resource);
            break;
        }
        if (element == null) return null;
        this.currentElement = (Openable) element;
        return this.currentElement;
    }

    private void popUntilPrefixOf(IPath path) {
        while (this.currentElement != null) {
            IPath currentElementPath = null;
            IResource currentElementResource = this.currentElement.getResource();
            if (currentElementResource != null) {
                currentElementPath = currentElementResource.getFullPath();
            }

            if (currentElementPath != null) {
                if (currentElementPath.isPrefixOf(path)) { return; }
            }
            this.currentElement = (Openable) this.currentElement.getParent();
        }
    }

    /*
     * Processing for an element that has been added:<ul> <li>If the element
     * is a project, do nothing, and do not process children, as when a project
     * is created it does not yet have any natures - specifically a java nature.
     * <li>If the elemet is not a project, process it as added (see <code>basicElementAdded</code>.
     * </ul> Delta argument could be null if processing an external JAR change
     */
    private void elementAdded(Openable element, IResourceDelta delta, RootInfo rootInfo) {
        int elementType = element.getElementType();

        if (elementType == IRubyElement.RUBY_PROJECT) {
            // project add is handled by RubyProject.configure() because
            // when a project is created, it does not yet have a ruby nature
            if (delta != null && RubyProject.hasRubyNature((IProject) delta.getResource())) {
                addToParentInfo(element);
                if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
                    Openable movedFromElement = (Openable) element.getRubyModel().getRubyProject(
                            delta.getMovedFromPath().lastSegment());
                    currentDelta().movedTo(element, movedFromElement);
                } else {
                    currentDelta().added(element);
                }
                this.state.updateRoots(element.getPath(), delta, this);
				
                // refresh pkg fragment roots and caches of the project (and its
                // dependents)
                this.rootsToRefresh.add((IRubyProject)element);
                this.projectCachesToReset.add((IRubyProject)element);
            }
        } else {
            if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) {
                // regular element addition
                if (isPrimaryWorkingCopy(element, elementType)) {
                    // filter out changes to primary compilation unit in working
                    // copy mode
                    // just report a change to the resource (see
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
                    currentDelta().changed(element, IRubyElementDelta.F_PRIMARY_RESOURCE);
                } else {
                    addToParentInfo(element);

                    // Force the element to be closed as it might have been
                    // opened
                    // before the resource modification came in and it might
                    // have a new child
                    // For example, in an IWorkspaceRunnable:
                    // 1. create a package fragment p using a java model
                    // operation
                    // 2. open package p
                    // 3. add file X.java in folder p
                    // When the resource delta comes in, only the addition of p
                    // is notified,
                    // but the package p is already opened, thus its children
                    // are not recomputed
                    // and it appears empty.
                    close(element);

                    currentDelta().added(element);
                }
            } else {
                // element is moved
                addToParentInfo(element);
                close(element);

                IPath movedFromPath = delta.getMovedFromPath();
                IResource res = delta.getResource();
                IResource movedFromRes;
                if (res instanceof IFile) {
                    movedFromRes = res.getWorkspace().getRoot().getFile(movedFromPath);
                } else {
                    movedFromRes = res.getWorkspace().getRoot().getFolder(movedFromPath);
                }

                // find the element type of the moved from element
                RootInfo movedFromInfo = this.enclosingRootInfo(movedFromPath, IResourceDelta.REMOVED);
                int movedFromType = this.elementType(movedFromRes, IResourceDelta.REMOVED, element
                        .getParent().getElementType(), movedFromInfo);

                // reset current element as it might be inside a nested root
                // (popUntilPrefixOf() may use the outer root)
                this.currentElement = null;

                // create the moved from element
                Openable movedFromElement = elementType != IRubyElement.RUBY_PROJECT
                        && movedFromType == IRubyElement.RUBY_PROJECT ? null : // outside
                                                                            // loadpath
                        this.createElement(movedFromRes, movedFromType, rootInfo);
                if (movedFromElement == null) {
                    // moved from outside classpath
                    currentDelta().added(element);
                } else {
                    currentDelta().movedTo(element, movedFromElement);
                }
            }
            
            switch (elementType) {
			case IRubyElement.SOURCE_FOLDER_ROOT :
				// when a root is added, and is on the loadpath, the project must be updated
				RubyProject project = (RubyProject) element.getRubyProject();

				// refresh src folder roots and caches of the project (and its dependents)
				this.rootsToRefresh.add(project);
				this.projectCachesToReset.add(project);
				
				break;
			case IRubyElement.SOURCE_FOLDER :
				// reset project's source folder cache 
				project = (RubyProject) element.getRubyProject();
				this.projectCachesToReset.add(project);						

				break;
            }
        }
    }

    /*
     * Returns whether the given element is a primary compilation unit in
     * working copy mode.
     */
    private boolean isPrimaryWorkingCopy(IRubyElement element, int elementType) {
        if (elementType == IRubyElement.SCRIPT) {
            RubyScript cu = (RubyScript) element;
            return cu.isPrimary() && cu.isWorkingCopy();
        }
        return false;
    }

    /*
     * Generic processing for a removed element:<ul> <li>Close the element,
     * removing its structure from the cache <li>Remove the element from its
     * parent's cache of children <li>Add a REMOVED entry in the delta </ul>
     * Delta argument could be null if processing an external JAR change
     */
    private void elementRemoved(Openable element, IResourceDelta delta, RootInfo rootInfo) {

        int elementType = element.getElementType();
        if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
            // regular element removal
            if (isPrimaryWorkingCopy(element, elementType)) {
                // filter out changes to primary compilation unit in working
                // copy mode
                // just report a change to the resource (see
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
                currentDelta().changed(element, IRubyElementDelta.F_PRIMARY_RESOURCE);
            } else {
                close(element);
                removeFromParentInfo(element);
                currentDelta().removed(element);
            }
        } else {
            // element is moved
            close(element);
            removeFromParentInfo(element);
            IPath movedToPath = delta.getMovedToPath();
            IResource res = delta.getResource();
            IResource movedToRes;
            switch (res.getType()) {
            case IResource.PROJECT:
                movedToRes = res.getWorkspace().getRoot().getProject(movedToPath.lastSegment());
                break;
            case IResource.FOLDER:
                movedToRes = res.getWorkspace().getRoot().getFolder(movedToPath);
                break;
            case IResource.FILE:
                movedToRes = res.getWorkspace().getRoot().getFile(movedToPath);
                break;
            default:
                return;
            }

            // find the element type of the moved from element
			RootInfo movedToInfo = this.enclosingRootInfo(movedToPath, IResourceDelta.ADDED);
            int movedToType = this.elementType(movedToRes, IResourceDelta.ADDED, element
                    .getParent().getElementType(), movedToInfo);

            // reset current element as it might be inside a nested root
            // (popUntilPrefixOf() may use the outer root)
            this.currentElement = null;

            // create the moved To element
            Openable movedToElement = elementType != IRubyElement.RUBY_PROJECT
                    && movedToType == IRubyElement.RUBY_PROJECT ? null : // outside
                                                                    // loadpath
                    this.createElement(movedToRes, movedToType, rootInfo);
            if (movedToElement == null) {
                // moved outside classpath
                currentDelta().removed(element);
            } else {
                currentDelta().movedFrom(element, movedToElement);
            }
        }

        switch (elementType) {
		case IRubyElement.RUBY_MODEL :
//			this.manager.indexManager.reset();
			break;
		case IRubyElement.RUBY_PROJECT :
			this.state.updateRoots(element.getPath(), delta, this);

			// refresh pkg fragment roots and caches of the project (and its dependents)
			this.rootsToRefresh.add((IRubyProject)element);
			this.projectCachesToReset.add((IRubyProject)element);

			break;
		case IRubyElement.SOURCE_FOLDER_ROOT :
			RubyProject project = (RubyProject) element.getRubyProject();

			// refresh src folder roots and caches of the project (and its dependents)
			this.rootsToRefresh.add(project);
			this.projectCachesToReset.add(project);				

			break;
		case IRubyElement.SOURCE_FOLDER :
			// reset sourc folder cache
			project = (RubyProject) element.getRubyProject();
			this.projectCachesToReset.add(project);

			break;
        }
    }

    /*
     * Generic processing for elements with changed contents:<ul> <li>The
     * element is closed such that any subsequent accesses will re-open the
     * element reflecting its new structure. <li>An entry is made in the delta
     * reporting a content change (K_CHANGE with F_CONTENT flag set). </ul>
     * Delta argument could be null if processing an external JAR change
     */
    private void contentChanged(Openable element) {

        boolean isPrimary = false;
        boolean isPrimaryWorkingCopy = false;
        if (element.getElementType() == IRubyElement.SCRIPT) {
            RubyScript cu = (RubyScript) element;
            isPrimary = cu.isPrimary();
            isPrimaryWorkingCopy = isPrimary && cu.isWorkingCopy();
        }
        if (isPrimaryWorkingCopy) {
            // filter out changes to primary compilation unit in working copy
            // mode
            // just report a change to the resource (see
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
            currentDelta().changed(element, IRubyElementDelta.F_PRIMARY_RESOURCE);
        } else {
            close(element);
            int flags = IRubyElementDelta.F_CONTENT;
            if (isPrimary) {
                flags |= IRubyElementDelta.F_PRIMARY_RESOURCE;
            }
            currentDelta().changed(element, flags);
        }
    }

    /*
     * Returns the type of the ruby element the given delta matches to. Returns
     * NON_RUBY_RESOURCE if unknown (e.g. a non-ruby resource or excluded .rb
     * file)
     */
	private int elementType(IResource res, int kind, int parentType, RootInfo rootInfo) {
		switch (parentType) {
			case IRubyElement.RUBY_MODEL:
				// case of a movedTo or movedFrom project (other cases are handled in processResourceDelta(...)
				return IRubyElement.RUBY_PROJECT;
			
			case NON_RUBY_RESOURCE:
			case IRubyElement.RUBY_PROJECT:
				if (rootInfo == null) {
					rootInfo = this.enclosingRootInfo(res.getFullPath(), kind);
				}
				if (rootInfo != null && rootInfo.isRootOfProject(res.getFullPath())) {
					return IRubyElement.SOURCE_FOLDER_ROOT;
				} 
				// not yet in a source folder root or root of another project
				// or source folder to be included (see below)
				// -> let it go through

			case IRubyElement.SOURCE_FOLDER_ROOT:
			case IRubyElement.SOURCE_FOLDER:
				if (rootInfo == null) {
					rootInfo = this.enclosingRootInfo(res.getFullPath(), kind);
				}
				if (rootInfo == null) {
					return NON_RUBY_RESOURCE;
				}
				if (Util.isExcluded(res, rootInfo.inclusionPatterns, rootInfo.exclusionPatterns)) {
					return NON_RUBY_RESOURCE;
				}
				if (res.getType() == IResource.FOLDER) {
					if (parentType == NON_RUBY_RESOURCE && !Util.isExcluded(res.getParent(), rootInfo.inclusionPatterns, rootInfo.exclusionPatterns))
						// parent is a non-Ruby resource because it doesn't have a valid package name (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=130982)
						return NON_RUBY_RESOURCE;
//					if (Util.isValidFolderNameForPackage(res.getName())) {
						return IRubyElement.SOURCE_FOLDER;
//					}
//					return NON_RUBY_RESOURCE;
				}
				String fileName = res.getName();
				if (Util.isValidRubyScriptName(fileName)) {
					return IRubyElement.SCRIPT;
				} else if (this.rootInfo(res.getFullPath(), kind) != null) {
					// case of proj=src=bin and resource is a jar file on the classpath
					return IRubyElement.SOURCE_FOLDER_ROOT;
				} else {
					return NON_RUBY_RESOURCE;
				}
				
			default:
				return NON_RUBY_RESOURCE;
		}
	}

	/*
	 * Answer a combination of the lastModified stamp and the size.
	 * Used for detecting external JAR changes
	 */
	public static long getTimeStamp(File file) {
		return file.lastModified() + file.length();
	}
	
	/*
	 * Update the RubyModel according to a .loadpath file change. The file can have changed as a result of a previous
	 * call to RubyProject#setRawLoadpath or as a result of some user update (through repository)
	 */
	private void reconcileLoadpathFileUpdate(IResourceDelta delta, RubyProject project) {

		switch (delta.getKind()) {
			case IResourceDelta.REMOVED : // recreate one based on in-memory classpath
				try {
					RubyModelManager.PerProjectInfo info = project.getPerProjectInfo();
					if (info.rawLoadpath != null) { // if there is an in-memory classpath
						project.saveLoadpath(info.rawLoadpath, info.outputLocation);
					}
				} catch (RubyModelException e) {
					if (project.getProject().isAccessible()) {
						Util.log(e, "Could not save loadpath for "+ project.getPath());
					}
				}
				break;
			case IResourceDelta.CHANGED :
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.CONTENT) == 0  // only consider content change
					&& (flags & IResourceDelta.ENCODING) == 0 // and encoding change
					&& (flags & IResourceDelta.MOVED_FROM) == 0) {// and also move and overide scenario (see http://dev.eclipse.org/bugs/show_bug.cgi?id=21420)
					break;
				}
			// fall through
			case IResourceDelta.ADDED :
				try {
					project.forceLoadpathReload(null);
				} catch (RuntimeException e) {
					if (VERBOSE) {
						e.printStackTrace();
					}
				} catch (RubyModelException e) {	
					if (VERBOSE) {
						e.printStackTrace();
					}
				}
		}
	}
	
	private void updateIndex(Openable element, IResourceDelta delta) {
		
		IndexManager indexManager = this.manager.getIndexManager();
		if (indexManager == null)
			return;
	
		switch (element.getElementType()) {
			case IRubyElement.RUBY_PROJECT :
				switch (delta.getKind()) {
					case IResourceDelta.ADDED :
						indexManager.indexAll(element.getRubyProject().getProject());
						break;
					case IResourceDelta.REMOVED :
						indexManager.removeIndexFamily(element.getRubyProject().getProject().getFullPath());
						// NB: Discarding index jobs belonging to this project was done during PRE_DELETE
						break;
					// NB: Update of index if project is opened, closed, or its java nature is added or removed
					//     is done in updateCurrentDeltaAndIndex
				}
				break;
			case IRubyElement.SOURCE_FOLDER_ROOT :
				if (element instanceof ExternalSourceFolderRoot) {
					ExternalSourceFolderRoot root = (ExternalSourceFolderRoot)element;
					// index jar file only once (if the root is in its declaring project)
					IPath jarPath = root.getPath();
					switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							// index the new jar
							indexManager.indexLibrary(jarPath, root.getRubyProject().getProject());
							break;
						case IResourceDelta.CHANGED:
							// first remove the index so that it is forced to be re-indexed
							indexManager.removeIndex(jarPath);
							// then index the jar
							indexManager.indexLibrary(jarPath, root.getRubyProject().getProject());
							break;
						case IResourceDelta.REMOVED:
							// the jar was physically removed: remove the index
							indexManager.discardJobs(jarPath.toString());
							indexManager.removeIndex(jarPath);
							break;
					}
					break;
				}
				int kind = delta.getKind();
				if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
					SourceFolderRoot root = (SourceFolderRoot)element;
					this.updateRootIndex(root, CharOperation.NO_STRINGS, delta);
					break;
				}
				// don't break as packages of the package fragment root can be indexed below
			case IRubyElement.SOURCE_FOLDER :
				switch (delta.getKind()) {
					case IResourceDelta.ADDED:
					case IResourceDelta.REMOVED:
						ISourceFolder pkg = null;
						if (element instanceof ISourceFolderRoot) {
							SourceFolderRoot root = (SourceFolderRoot)element;
							pkg = root.getSourceFolder(CharOperation.NO_STRINGS);
						} else {
							pkg = (ISourceFolder)element;
						}
						RootInfo rootInfo = rootInfo(pkg.getParent().getPath(), delta.getKind());
						boolean isSource = 
							rootInfo == null // if null, defaults to source
							|| rootInfo.entryKind == ILoadpathEntry.CPE_SOURCE;
						IResourceDelta[] children = delta.getAffectedChildren();
						for (int i = 0, length = children.length; i < length; i++) {
							IResourceDelta child = children[i];
							IResource resource = child.getResource();
							// TODO (philippe) Why do this? Every child is added anyway as the delta is walked
							if (resource instanceof IFile) {
								String name = resource.getName();
								if (isSource) {
									if (org.rubypeople.rdt.internal.core.util.Util.isRubyOrERBLikeFileName(name)) {
										Openable cu = (Openable)pkg.getRubyScript(name);
										this.updateIndex(cu, child);
									}
								}
							}
						}
						break;
				}
				break;
			case IRubyElement.SCRIPT :
				IFile file = (IFile) delta.getResource();
				switch (delta.getKind()) {
					case IResourceDelta.CHANGED :
						// no need to index if the content has not changed
						int flags = delta.getFlags();
						if ((flags & IResourceDelta.CONTENT) == 0 && (flags & IResourceDelta.ENCODING) == 0)
							break;
					case IResourceDelta.ADDED :
						indexManager.addSource(file, file.getProject().getFullPath(), getSourceElementParser(element));
						// Clean file from secondary types cache but do not update indexing secondary type cache as it will be updated through indexing itself
						this.manager.secondaryTypesRemoving(file, false);
						break;
					case IResourceDelta.REMOVED :
						indexManager.remove(Util.relativePath(file.getFullPath(), 1/*remove project segment*/), file.getProject().getFullPath());
						// Clean file from secondary types cache and update indexing secondary type cache as indexing cannot remove secondary types from cache
						this.manager.secondaryTypesRemoving(file, true);
						break;
				}
		}
	}

	private SourceElementParser getSourceElementParser(Openable element) {
		if (this.sourceElementParserCache == null)
			this.sourceElementParserCache = this.manager.getIndexManager().getSourceElementParser(element.getRubyProject(), null/*requestor will be set by indexer*/);
		return this.sourceElementParserCache;
	}
	
	/*
	 * Updates the index of the given root (assuming it's an addition or a removal).
	 * This is done recusively, pkg being the current package.
	 */
	private void updateRootIndex(SourceFolderRoot root, String[] pkgName, IResourceDelta delta) {
		Openable pkg = root.getSourceFolder(pkgName);
		this.updateIndex(pkg, delta);
		IResourceDelta[] children = delta.getAffectedChildren();
		for (int i = 0, length = children.length; i < length; i++) {
			IResourceDelta child = children[i];
			IResource resource = child.getResource();
			if (resource instanceof IFolder) {
				String[] subpkgName = Util.arrayConcat(pkgName, resource.getName());
				this.updateRootIndex(root, subpkgName, child);
			}
		}
	}
	
	/*
	 * Update the current delta (ie. add/remove/change the given element) and update the correponding index.
	 * Returns whether the children of the given delta must be processed.
	 * @throws a RubyModelException if the delta doesn't correspond to a ruby element of the given type.
	 */
	public boolean updateCurrentDeltaAndIndex(IResourceDelta delta, int elementType, RootInfo rootInfo) {
		Openable element;
		switch (delta.getKind()) {
			case IResourceDelta.ADDED :
				IResource deltaRes = delta.getResource();
				element = createElement(deltaRes, elementType, rootInfo);
				if (element == null) {
					// resource might be containing shared roots (see bug 19058)
					this.state.updateRoots(deltaRes.getFullPath(), delta, this);
					return rootInfo != null && rootInfo.inclusionPatterns != null;
				}
				updateIndex(element, delta);
				elementAdded(element, delta, rootInfo);
				return elementType == IRubyElement.SOURCE_FOLDER;
			case IResourceDelta.REMOVED :
				deltaRes = delta.getResource();
				element = createElement(deltaRes, elementType, rootInfo);
				if (element == null) {
					// resource might be containing shared roots (see bug 19058)
					this.state.updateRoots(deltaRes.getFullPath(), delta, this);
					return rootInfo != null && rootInfo.inclusionPatterns != null;
				}
				updateIndex(element, delta);
				elementRemoved(element, delta, rootInfo);
	
				if (deltaRes.getType() == IResource.PROJECT){			
					// reset the corresponding project built state, since cannot reuse if added back
					if (RubyBuilder.DEBUG)
						System.out.println("Clearing last state for removed project : " + deltaRes); //$NON-NLS-1$
					this.manager.setLastBuiltState((IProject)deltaRes, null /*no state*/);
					
					// clean up previous session containers (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=89850)
					this.manager.previousSessionContainers.remove(element);
				}
				return elementType == IRubyElement.SOURCE_FOLDER;
			case IResourceDelta.CHANGED :
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.CONTENT) != 0 || (flags & IResourceDelta.ENCODING) != 0) {
					// content or encoding has changed
					element = createElement(delta.getResource(), elementType, rootInfo);
					if (element == null) return false;
					updateIndex(element, delta);
					contentChanged(element);
				} else if (elementType == IRubyElement.RUBY_PROJECT) {
					if ((flags & IResourceDelta.OPEN) != 0) {
						// project has been opened or closed
						IProject res = (IProject)delta.getResource();
						element = createElement(res, elementType, rootInfo);
						if (element == null) {
							// resource might be containing shared roots (see bug 19058)
							this.state.updateRoots(res.getFullPath(), delta, this);
							return false;
						}
						if (res.isOpen()) {
							if (RubyProject.hasRubyNature(res)) {
								addToParentInfo(element);
								currentDelta().opened(element);
								this.state.updateRoots(element.getPath(), delta, this);
								
								// refresh pkg fragment roots and caches of the project (and its dependents)
								this.rootsToRefresh.add((IRubyProject)element);
								this.projectCachesToReset.add((IRubyProject)element);
								
								this.manager.getIndexManager().indexAll(res);
							}
						} else {
							boolean wasJavaProject = this.state.findRubyProject(res.getName()) != null;
							if (wasJavaProject) {
								close(element);
								removeFromParentInfo(element);
								currentDelta().closed(element);
								this.manager.getIndexManager().discardJobs(element.getElementName());
								this.manager.getIndexManager().removeIndexFamily(res.getFullPath());
							}
						}
						return false; // when a project is open/closed don't process children
					}
					if ((flags & IResourceDelta.DESCRIPTION) != 0) {
						IProject res = (IProject)delta.getResource();
						boolean wasJavaProject = this.state.findRubyProject(res.getName()) != null;
						boolean isJavaProject = RubyProject.hasRubyNature(res);
						if (wasJavaProject != isJavaProject) {
							// project's nature has been added or removed
							element = this.createElement(res, elementType, rootInfo);
							if (element == null) return false; // note its resources are still visible as roots to other projects
							if (isJavaProject) {
								elementAdded(element, delta, rootInfo);
								this.manager.getIndexManager().indexAll(res);
							} else {
								elementRemoved(element, delta, rootInfo);
								this.manager.getIndexManager().discardJobs(element.getElementName());
								this.manager.getIndexManager().removeIndexFamily(res.getFullPath());
								// reset the corresponding project built state, since cannot reuse if added back
								if (RubyBuilder.DEBUG)
									System.out.println("Clearing last state for project loosing Java nature: " + res); //$NON-NLS-1$
								this.manager.setLastBuiltState(res, null /*no state*/);
							}
							return false; // when a project's nature is added/removed don't process children
						}
					}
				}
				return true;
		}
		return true;
	}

	/*
	 * Check all external archive (referenced by given roots, projects or model) status and issue a corresponding root delta.
	 * Also triggers index updates
	 */
	public void checkExternalArchiveChanges(IRubyElement[] elementsToRefresh, IProgressMonitor monitor) throws RubyModelException {
		try {
			for (int i = 0, length = elementsToRefresh.length; i < length; i++) {
				this.addForRefresh(elementsToRefresh[i]);
			}
			boolean hasDelta = this.createExternalArchiveDelta(monitor);
			if (monitor != null && monitor.isCanceled()) return; 
			if (hasDelta){
				// force loadpath marker refresh of affected projects
				RubyModel.flushExternalFileCache();
								
				IRubyElementDelta[] projectDeltas = this.currentDelta.getAffectedChildren();
				final int length = projectDeltas.length;
//				final IProject[] projectsToTouch = new IProject[length];
				for (int i = 0; i < length; i++) {
					IRubyElementDelta delta = projectDeltas[i];
					RubyProject rubyProject = (RubyProject)delta.getElement();
					rubyProject.getResolvedLoadpath(
						true/*ignoreUnresolvedEntry*/, 
						true/*generateMarkerOnError*/, 
						false/*don't returnResolutionInProgress*/);
//					projectsToTouch[i] = rubyProject.getProject();
				}
				
				// no need to rebuild if external folders/files change
//				// touch the projects to force them to be recompiled while taking the workspace lock 
//				// so that there is no concurrency with the Java builder
//				// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=96575
//				IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
//					public void run(IProgressMonitor progressMonitor) throws CoreException {
//						for (int i = 0; i < length; i++) {
//							IProject project = projectsToTouch[i];
//							
//							// touch to force a build of this project
//							if (RubyBuilder.DEBUG)
//								System.out.println("Touching project " + project.getName() + " due to external jar file change"); //$NON-NLS-1$ //$NON-NLS-2$
//							project.touch(progressMonitor);
//						}
//					}
//				};
//				try {
//					ResourcesPlugin.getWorkspace().run(runnable, monitor);
//				} catch (CoreException e) {
//					throw new RubyModelException(e);
//				}
				
				if (this.currentDelta != null) { // if delta has not been fired while creating markers
					this.fire(this.currentDelta, DEFAULT_CHANGE_EVENT);
				}
			}
		} finally {
			this.currentDelta = null;
			if (monitor != null) monitor.done();
		}
	}
	
	/*
	 * Check if external archives have changed and create the corresponding deltas.
	 * Returns whether at least on delta was created.
	 */
	private boolean createExternalArchiveDelta(IProgressMonitor monitor) {
		
		if (this.refreshedElements == null) return false;
			
		HashMap externalArchivesStatus = new HashMap();
		boolean hasDelta = false;
		
		// find JARs to refresh
		HashSet archivePathsToRefresh = new HashSet();
		Iterator iterator = this.refreshedElements.iterator();
		this.refreshedElements = null; // null out early to avoid concurrent modification exception (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63534)
		while (iterator.hasNext()) {
			IRubyElement element = (IRubyElement)iterator.next();
			switch(element.getElementType()){
				case IRubyElement.SOURCE_FOLDER_ROOT :
					archivePathsToRefresh.add(element.getPath());
					break;
				case IRubyElement.RUBY_PROJECT :
					RubyProject javaProject = (RubyProject) element;
					if (!RubyProject.hasRubyNature(javaProject.getProject())) {
						// project is not accessible or has lost its Ruby nature
						break;
					}
					ILoadpathEntry[] classpath;
					try {
						classpath = javaProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
						for (int j = 0, cpLength = classpath.length; j < cpLength; j++){
							if (classpath[j].getEntryKind() == ILoadpathEntry.CPE_LIBRARY){
								archivePathsToRefresh.add(classpath[j].getPath());
							}
						}
					} catch (RubyModelException e) {
						// project doesn't exist -> ignore
					}
					break;
				case IRubyElement.RUBY_MODEL :
					Iterator projectNames = this.state.getOldRubyProjecNames().iterator();
					while (projectNames.hasNext()) {
						String projectName = (String) projectNames.next();
						IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
						if (!RubyProject.hasRubyNature(project)) {
							// project is not accessible or has lost its Ruby nature
							continue;
						}
						javaProject = (RubyProject) RubyCore.create(project);
						try {
							classpath = javaProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
						} catch (RubyModelException e2) {
							// project doesn't exist -> ignore
							continue;
						}
						for (int k = 0, cpLength = classpath.length; k < cpLength; k++){
							if (classpath[k].getEntryKind() == ILoadpathEntry.CPE_LIBRARY){
								archivePathsToRefresh.add(classpath[k].getPath());
							}
						}
					}
					break;
			}
		}
		
		// perform refresh
		Iterator projectNames = this.state.getOldRubyProjecNames().iterator();
		IWorkspaceRoot wksRoot = ResourcesPlugin.getWorkspace().getRoot();
		while (projectNames.hasNext()) {
			
			if (monitor != null && monitor.isCanceled()) break; 
			
			String projectName = (String) projectNames.next();
			IProject project = wksRoot.getProject(projectName);
			if (!RubyProject.hasRubyNature(project)) {
				// project is not accessible or has lost its Ruby nature
				continue;
			}
			RubyProject javaProject = (RubyProject) RubyCore.create(project);
			ILoadpathEntry[] entries;
			try {
				entries = javaProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
			} catch (RubyModelException e1) {
				// project does not exist -> ignore
				continue;
			}
			for (int j = 0; j < entries.length; j++){
				if (entries[j].getEntryKind() == ILoadpathEntry.CPE_LIBRARY) {
					
					IPath entryPath = entries[j].getPath();
					
					if (!archivePathsToRefresh.contains(entryPath)) continue; // not supposed to be refreshed
					
					String status = (String)externalArchivesStatus.get(entryPath); 
					if (status == null){
						
						// compute shared status
						Object targetLibrary = RubyModel.getTarget(wksRoot, entryPath, true);
		
						if (targetLibrary == null){ // missing JAR
							if (this.state.getExternalLibTimeStamps().remove(entryPath) != null){
								externalArchivesStatus.put(entryPath, EXTERNAL_JAR_REMOVED);
								// the jar was physically removed: remove the index
								this.manager.indexManager.removeIndex(entryPath);
							}
		
						} else if (targetLibrary instanceof File){ // external JAR
		
							File externalFile = (File)targetLibrary;
							
							// check timestamp to figure if JAR has changed in some way
							Long oldTimestamp =(Long) this.state.getExternalLibTimeStamps().get(entryPath);
							long newTimeStamp = getTimeStamp(externalFile);
							if (oldTimestamp != null){
		
								if (newTimeStamp == 0){ // file doesn't exist
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_REMOVED);
									this.state.getExternalLibTimeStamps().remove(entryPath);
									// remove the index
									this.manager.indexManager.removeIndex(entryPath);
		
								} else if (oldTimestamp.longValue() != newTimeStamp){
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_CHANGED);
									this.state.getExternalLibTimeStamps().put(entryPath, new Long(newTimeStamp));
									// first remove the index so that it is forced to be re-indexed
									this.manager.indexManager.removeIndex(entryPath);
									// then index the jar
									this.manager.indexManager.indexLibrary(entryPath, project.getProject());
								} else {
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_UNCHANGED);
								}
							} else {
								if (newTimeStamp == 0){ // jar still doesn't exist
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_UNCHANGED);
								} else {
									externalArchivesStatus.put(entryPath, EXTERNAL_JAR_ADDED);
									this.state.getExternalLibTimeStamps().put(entryPath, new Long(newTimeStamp));
									// index the new jar
									this.manager.indexManager.indexLibrary(entryPath, project.getProject());
								}
							}
						} else { // internal JAR
							externalArchivesStatus.put(entryPath, INTERNAL_JAR_IGNORE);
						}
					}
					// according to computed status, generate a delta
					status = (String)externalArchivesStatus.get(entryPath); 
					if (status != null){
						if (status == EXTERNAL_JAR_ADDED){
							SourceFolderRoot root = (SourceFolderRoot) javaProject.getSourceFolderRoot(entryPath.toString());
							if (VERBOSE){
								System.out.println("- External JAR ADDED, affecting root: "+root.getElementName()); //$NON-NLS-1$
							} 
							elementAdded(root, null, null);
							hasDelta = true;
						} else if (status == EXTERNAL_JAR_CHANGED) {
							SourceFolderRoot root = (SourceFolderRoot) javaProject.getSourceFolderRoot(entryPath.toString());
							if (VERBOSE){
								System.out.println("- External JAR CHANGED, affecting root: "+root.getElementName()); //$NON-NLS-1$
							}
							contentChanged(root);
							hasDelta = true;
						} else if (status == EXTERNAL_JAR_REMOVED) {
							SourceFolderRoot root = (SourceFolderRoot) javaProject.getSourceFolderRoot(entryPath.toString());
							if (VERBOSE){
								System.out.println("- External JAR REMOVED, affecting root: "+root.getElementName()); //$NON-NLS-1$
							}
							elementRemoved(root, null, null);
							hasDelta = true;
						}
					}
				}
			}
		}
		return hasDelta;
	}
}

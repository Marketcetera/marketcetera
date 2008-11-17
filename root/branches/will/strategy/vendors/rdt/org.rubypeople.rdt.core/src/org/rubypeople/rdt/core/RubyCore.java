/*
 * Author: 
 *
 * Copyright (c) 2003-2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.core.search.TypeNameRequestor;
import org.rubypeople.rdt.internal.core.BatchOperation;
import org.rubypeople.rdt.internal.core.DefaultWorkingCopyOwner;
import org.rubypeople.rdt.internal.core.LoadpathAttribute;
import org.rubypeople.rdt.internal.core.LoadpathEntry;
import org.rubypeople.rdt.internal.core.Region;
import org.rubypeople.rdt.internal.core.RubyCorePreferenceInitializer;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.SetLoadpathOperation;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class RubyCore extends Plugin {

    private static RubyCore RUBY_CORE_PLUGIN = null;

    public final static String PLUGIN_ID = "org.rubypeople.rdt.core";//$NON-NLS-1$
    public final static String NATURE_ID = PLUGIN_ID + ".rubynature";//$NON-NLS-1$

    /**
     * New Preferences API
     * 
     * @since 0.6.0
     */
    public static final IEclipsePreferences[] preferencesLookup = new IEclipsePreferences[2];
    static final int PREF_INSTANCE = 0;
    static final int PREF_DEFAULT = 1;

    /**
     * Default task tag
     * 
     * @since 0.6.0
     */
    public static final String DEFAULT_TASK_TAGS = "TODO,FIXME,XXX,OPTIMIZE"; //$NON-NLS-1$

    /**
     * The identifier for the Ruby builder (value
     * <code>"org.rubypeople.rdt.core.rubybuilder"</code>).
     */
    public static final String BUILDER_ID = PLUGIN_ID + ".rubybuilder"; //$NON-NLS-1$

    /**
     * Default task priority
     * 
     * @since 0.6.0
     */
    public static final String DEFAULT_TASK_PRIORITIES = "NORMAL,HIGH,NORMAL,NORMAL"; //$NON-NLS-1$
    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITIES = PLUGIN_ID + ".compiler.taskPriorities"; //$NON-NLS-1$
    /**
     * Possible configurable option value for COMPILER_TASK_PRIORITIES.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITY_HIGH = "HIGH"; //$NON-NLS-1$
    /**
     * Possible configurable option value for COMPILER_TASK_PRIORITIES.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITY_LOW = "LOW"; //$NON-NLS-1$
    /**
     * Possible configurable option value for COMPILER_TASK_PRIORITIES.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITY_NORMAL = "NORMAL"; //$NON-NLS-1$

    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_TAGS = PLUGIN_ID + ".compiler.taskTags"; //$NON-NLS-1$
    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String COMPILER_TASK_CASE_SENSITIVE = PLUGIN_ID
            + ".compiler.taskCaseSensitive"; //$NON-NLS-1$	
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String ENABLED = "enabled"; //$NON-NLS-1$
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String DISABLED = "disabled"; //$NON-NLS-1$

    /**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions()
	 */
	public static final String ERROR = "error"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions()
	 */
	public static final String WARNING = "warning"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions()
	 */
	public static final String IGNORE = "ignore"; //$NON-NLS-1$
    
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String TAB = "tab"; //$NON-NLS-1$
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String SPACE = "space"; //$NON-NLS-1$

    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String CORE_ENCODING = PLUGIN_ID + ".encoding"; //$NON-NLS-1$

    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.8.0
     */
    public static final String INSERT = "insert"; //$NON-NLS-1$
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.8.0
     */
    public static final String DO_NOT_INSERT = "do not insert"; //$NON-NLS-1$

	/**
	 * Value of the content-type for Ruby source files. Use this value to retrieve the Ruby content type
	 * from the content type manager, and to add new Ruby-like extensions to this content type.
	 * 
	 * @see org.eclipse.core.runtime.content.IContentTypeManager#getContentType(String)
	 * @see #getRubyLikeExtensions()
	 * @since 0.8.0
	 */
	public static final String RUBY_SOURCE_CONTENT_TYPE = RubyCore.PLUGIN_ID+".rubySource" ; //$NON-NLS-1$

	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_EMPTY_STATEMENT = PLUGIN_ID + ".compiler.problem.emptyStatement"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_CONSTANT_REASSIGNMENT = PLUGIN_ID + ".compiler.problem.constantReassignment"; //$NON-NLS-1$

	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_UNREACHABLE_CODE = PLUGIN_ID + ".compiler.problem.unreachableCode"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_REDEFINITION_CORE_CLASS_METHOD = PLUGIN_ID + ".compiler.problem.redefinition.core.class.method"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 1.0.0
	 */
	public static final String COMPILER_PB_RUBY_19_WHEN_STATEMENTS = PLUGIN_ID + ".compiler.problem.ruby19WhenStatements"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 1.0.0
	 */
	public static final String COMPILER_PB_RUBY_19_HASH_COMMA_SYTNAX = PLUGIN_ID + ".compiler.problem.ruby19HashCommaSyntax"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String CODEASSIST_CAMEL_CASE_MATCH = PLUGIN_ID + ".codeComplete.camelCaseMatch"; //$NON-NLS-1$

	// FIXME Rename to CORE_INCOMPLETE_LOADPATH
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String CORE_INCOMPLETE_CLASSPATH = PLUGIN_ID + ".incompleteClasspath"; //$NON-NLS-1$

	// FIXME Rename to CORE_INCOMPATIBLE_RUBY_VERSION
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 3.0
	 */
	public static final String CORE_INCOMPATIBLE_JDK_LEVEL = PLUGIN_ID + ".incompatibleJDKLevel"; //$NON-NLS-1$
	
	// FIXME Rename to CORE_CIRCULAR_LOADPATH
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 2.1
	 */
	public static final String CORE_CIRCULAR_CLASSPATH = PLUGIN_ID + ".circularClasspath"; //$NON-NLS-1$

	/**
	 * Name of the User Library Container id.
	 * @since 1.0.0
	 */
	public static final String USER_LIBRARY_CONTAINER_ID= "org.rubypeople.rdt.USER_LIBRARY"; //$NON-NLS-1$
		
	private static final boolean VERBOSE = false;

	private RubyProjectListener fProjectListener;
	
    public RubyCore() {
        super();
        RUBY_CORE_PLUGIN = this;
    }

    /**
     * Returns the single instance of the Ruby core plug-in runtime class.
     * 
     * @return the single instance of the Ruby core plug-in runtime class
     */
    public static RubyCore getPlugin() {
        return RUBY_CORE_PLUGIN;
    }

    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        fProjectListener = new RubyProjectListener();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(fProjectListener, IResourceChangeEvent.POST_CHANGE);
//        
//        Job job = new Job("Startup RubyModelManager") {
//		
//			@Override
//			protected IStatus run(IProgressMonitor arg0) {
//				try {
					RubyModelManager.getRubyModelManager().startup();
//				} catch (CoreException e) {
//					return e.getStatus();
//				}
//				return Status.OK_STATUS;
//			}
//		
//		};
//        job.setSystem(true);
//        job.setPriority(Job.SHORT);
//        job.schedule();       
    }

    /*
     * (non-Javadoc) Shutdown the RubyCore plug-in. <p> De-registers the
     * RubyModelManager as a resource changed listener and save participant. <p>
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        try {
        	ResourcesPlugin.getWorkspace().removeResourceChangeListener(fProjectListener);
            RubyModelManager.getRubyModelManager().shutdown();
        } finally {
            // ensure we call super.stop as the last thing
            super.stop(context);
        }
    }

    public static void trace(String message) {
        if (getPlugin().isDebugging()) System.out.println(message);
    }

    public static void log(Exception e) {
        String msg = e.getMessage();
        if (msg == null) msg = "";
        log(Status.ERROR, msg, e);
    }

    /**
     * @param string
     */
    public static void log(String string) {
        log(IStatus.INFO, string);
    }

    public static void log(int severity, String string) {
        log(severity, string, null);
    }

    public static void log(int severity, String string, Throwable e) {
        getPlugin().getLog().log(new Status(severity, PLUGIN_ID, IStatus.OK, string, e));
        if (severity == Status.ERROR) {
        	// send stack trace to Trac!
        }
        if (VERBOSE) {
        	System.out.println(string);
        	if (e != null) e.printStackTrace();
        }
    }

    public static String getOSDirectory(Plugin plugin) {
        final Bundle bundle = plugin.getBundle();
        String location = bundle.getLocation();
        int prefixLength = location.indexOf('@');
        if (prefixLength == -1) { throw new RuntimeException(
                "Location of launching bundle does not contain @: " + location); }
        String pluginDir = location.substring(prefixLength + 1);
        File pluginDirFile = new File(pluginDir);
        if (!pluginDirFile.exists()) {
            // pluginDirFile is a relative path, if the working directory is
            // different from
            // the location of the eclipse executable, we try this ...
            String installArea = System.getProperty("osgi.install.area");
            if (installArea.startsWith("file:")) {
                installArea = installArea.substring("file:".length());
            }
            // Path.toOSString() removes a leading slash if on windows, e.g.
            // /D:/Eclipse => D:/Eclipse
            File installFile = new File(new Path(installArea).toOSString());
            pluginDirFile = new File(installFile, pluginDir);
            if (!pluginDirFile.exists())
                throw new RuntimeException("Unable to find (" + pluginDirFile + ") directory for "
                        + plugin.getClass());
        }
        return pluginDirFile.getAbsolutePath() + "/";
    }

    public static IProject[] getRubyProjects() {
        List<IProject> rubyProjectsList = new ArrayList<IProject>();
        IProject[] workspaceProjects = RubyCore.getWorkspace().getRoot().getProjects();

        for (int i = 0; i < workspaceProjects.length; i++) {
            IProject iProject = workspaceProjects[i];
            if (isRubyProject(iProject)) rubyProjectsList.add(iProject);
        }

        IProject[] rubyProjects = new IProject[rubyProjectsList.size()];
        return rubyProjectsList.toArray(rubyProjects);
    }

    public static boolean isRubyProject(IProject aProject) {
        try {
            return aProject.hasNature(RubyCore.NATURE_ID);
        } catch (CoreException e) {
        }
        return false;
    }

    public static IRubyScript create(IFile file) {
    	return RubyModelManager.create(file, null/*unknown ruby project*/);
    }

    public static IRubyProject create(IProject project) {
        if (project == null) { return null; }
        RubyModel rubyModel = RubyModelManager.getRubyModelManager().getRubyModel();
        return rubyModel.getRubyProject(project);
    }

    public static void addRubyNature(IProject project, IProgressMonitor monitor)
            throws CoreException {
        if (!project.hasNature(RubyCore.NATURE_ID)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = RubyCore.NATURE_ID;
            description.setNatureIds(newNatures);
            project.setDescription(description, monitor);
        }
    }

    public static IRubyElement create(IResource resource) {       
    	return RubyModelManager.create(resource, null/*unknown ruby project*/);    	
    }

    /**
     * Returns the Ruby model.
     * 
     * @param root
     *            the given root
     * @return the Ruby model, or <code>null</code> if the root is null
     */
    public static IRubyModel create(IWorkspaceRoot root) {
        if (root == null) { return null; }
        return RubyModelManager.getRubyModelManager().getRubyModel();
    }

    /**
     * Runs the given action as an atomic Java model operation.
     * <p>
     * After running a method that modifies java elements, registered listeners
     * receive after-the-fact notification of what just transpired, in the form
     * of a element changed event. This method allows clients to call a number
     * of methods that modify java elements and only have element changed event
     * notifications reported at the end of the entire batch.
     * </p>
     * <p>
     * If this method is called outside the dynamic scope of another such call,
     * this method runs the action and then reports a single element changed
     * event describing the net effect of all changes done to java elements by
     * the action.
     * </p>
     * <p>
     * If this method is called in the dynamic scope of another such call, this
     * method simply runs the action.
     * </p>
     * <p>
     * The supplied scheduling rule is used to determine whether this operation
     * can be run simultaneously with workspace changes in other threads. See
     * <code>IWorkspace.run(...)</code> for more details.
     * </p>
     * 
     * @param action
     *            the action to perform
     * @param rule
     *            the scheduling rule to use when running this operation, or
     *            <code>null</code> if there are no scheduling restrictions
     *            for this operation.
     * @param monitor
     *            a progress monitor, or <code>null</code> if progress
     *            reporting and cancellation are not desired
     * @exception CoreException
     *                if the operation failed.
     * @since 3.0
     */
    public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor)
            throws CoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (workspace.isTreeLocked()) {
            new BatchOperation(action).run(monitor);
        } else {
            // use IWorkspace.run(...) to ensure that a build will be done in
            // autobuild mode
            workspace.run(new BatchOperation(action), rule, IWorkspace.AVOID_UPDATE, monitor);
        }
    }

    /**
     * Helper method for returning one option value only. Equivalent to
     * <code>(String)JavaCore.getOptions().get(optionName)</code> Note that it
     * may answer <code>null</code> if this option does not exist.
     * <p>
     * For a complete description of the configurable options, see
     * <code>getDefaultOptions</code>.
     * </p>
     * 
     * @param optionName
     *            the name of an option
     * @return the String value of a given option
     * @see RubyCore#getDefaultOptions()
     * @see RubyCorePreferenceInitializer for changing default settings
     */
    public static String getOption(String optionName) {
        return RubyModelManager.getRubyModelManager().getOption(optionName);
    }

    /**
     * Returns the workspace root default charset encoding.
     * 
     * @return the name of the default charset encoding for workspace root.
     * @see IContainer#getDefaultCharset()
     * @see ResourcesPlugin#getEncoding()
     */
    public static String getEncoding() {
        // Verify that workspace is not shutting down (see bug
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=60687)
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (workspace != null) {
            try {
                return workspace.getRoot().getDefaultCharset();
            } catch (CoreException e) {
                // fails silently and return plugin global encoding if core
                // exception occurs
            }
        }
        return ResourcesPlugin.getEncoding();
    }

    /**
     * Returns the table of the current options. Initially, all options have
     * their default values, and this method returns a table that includes all
     * known options.
     * <p>
     * For a complete description of the configurable options, see
     * <code>getDefaultOptions</code>.
     * </p>
     * 
     * @return table of current settings of all options (key type:
     *         <code>String</code>; value type: <code>String</code>)
     * @see #getDefaultOptions()
     * @see RubyCorePreferenceInitializer for changing default settings
     */
    public static Hashtable<String, String> getOptions() {
        return RubyModelManager.getRubyModelManager().getOptions();
    }

    /**
     * Adds the given listener for changes to Java elements. Has no effect if an
     * identical listener is already registered.
     * 
     * This listener will only be notified during the POST_CHANGE resource
     * change notification and any reconcile operation (POST_RECONCILE). For
     * finer control of the notification, use
     * <code>addElementChangedListener(IElementChangedListener,int)</code>,
     * which allows to specify a different eventMask.
     * 
     * @param listener
     *            the listener
     * @see ElementChangedEvent
     */
    public static void addElementChangedListener(IElementChangedListener listener) {
        addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE
                | ElementChangedEvent.POST_RECONCILE);
    }

    /**
     * Adds the given listener for changes to Java elements. Has no effect if an
     * identical listener is already registered. After completion of this
     * method, the given listener will be registered for exactly the specified
     * events. If they were previously registered for other events, they will be
     * deregistered.
     * <p>
     * Once registered, a listener starts receiving notification of changes to
     * java elements in the model. The listener continues to receive
     * notifications until it is replaced or removed.
     * </p>
     * <p>
     * Listeners can listen for several types of event as defined in
     * <code>ElementChangeEvent</code>. Clients are free to register for any
     * number of event types however if they register for more than one, it is
     * their responsibility to ensure they correctly handle the case where the
     * same java element change shows up in multiple notifications. Clients are
     * guaranteed to receive only the events for which they are registered.
     * </p>
     * 
     * @param listener
     *            the listener
     * @param eventMask
     *            the bit-wise OR of all event types of interest to the listener
     * @see IElementChangedListener
     * @see ElementChangedEvent
     * @see #removeElementChangedListener(IElementChangedListener)
     * @since 0.7.0
     */
    public static void addElementChangedListener(IElementChangedListener listener, int eventMask) {
        RubyModelManager.getRubyModelManager().deltaState.addElementChangedListener(listener,
                eventMask);
    }

    /**
     * Removes the given element changed listener. Has no affect if an identical
     * listener is not registered.
     * 
     * @param listener
     *            the listener
     */
    public static void removeElementChangedListener(IElementChangedListener listener) {
        RubyModelManager.getRubyModelManager().deltaState.removeElementChangedListener(listener);
    }

    /**
     * Returns the single instance of the Ruby core plug-in runtime class.
     * Equivalent to <code>(RubyCore) getPlugin()</code>.
     * 
     * @return the single instance of the Ruby core plug-in runtime class
     */
    public static RubyCore getRubyCore() {
        return getPlugin();
    }

    public static boolean isRubyLikeFileName(String name) {
        return Util.isRubyLikeFileName(name);
    }

	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
	 * for all files in the project's source folder identified by the given
	 * absolute workspace-relative path.
	 * <p>
	 * The convenience method is fully equivalent to:
	 * <pre>
	 * newSourceEntry(path, new IPath[] {}, new IPath[] {}, null);
	 * </pre>
	 * </p>
	 * 
	 * @param path the absolute workspace-relative path of a source folder
	 * @return a new source classpath entry
	 * @see #newSourceEntry(IPath, IPath[], IPath[])
	 */
	public static ILoadpathEntry newSourceEntry(IPath path) {
		return newSourceEntry(path, LoadpathEntry.INCLUDE_ALL, LoadpathEntry.EXCLUDE_NONE, LoadpathEntry.NO_EXTRA_ATTRIBUTES);
	}
	
	/**
	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
	 * for the project's source folder identified by the given absolute 
	 * workspace-relative path but excluding all source files with paths
	 * matching any of the given patterns, and associated with a specific output location
	 * (that is, ".class" files are not going to the project default output location). 
	 * <p>
	 * The convenience method is fully equivalent to:
	 * <pre>
	 * newSourceEntry(path, new IPath[] {}, exclusionPatterns);
	 * </pre>
	 * </p>
	 *
	 * @param path the absolute workspace-relative path of a source folder
	 * @param inclusionPatterns the possibly empty list of inclusion patterns
	 *    represented as relative paths
	 * @param exclusionPatterns the possibly empty list of exclusion patterns
	 *    represented as relative paths
	 * @return a new source classpath entry
	 * @since 3.0
	 */
	public static ILoadpathEntry newSourceEntry(IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, ILoadpathAttribute[] extraAttributes) {
		if (path == null) Assert.isTrue(false, "Source path cannot be null"); //$NON-NLS-1$
		if (!path.isAbsolute()) Assert.isTrue(false, "Path for ILoadpathEntry must be absolute"); //$NON-NLS-1$
		if (exclusionPatterns == null) Assert.isTrue(false, "Exclusion pattern set cannot be null"); //$NON-NLS-1$
		if (inclusionPatterns == null) Assert.isTrue(false, "Inclusion pattern set cannot be null"); //$NON-NLS-1$

		return new LoadpathEntry(
			ILoadpathEntry.CPE_SOURCE,
			path,
			inclusionPatterns,
			exclusionPatterns,
			extraAttributes,
			false); 
	}

	public static ILoadpathEntry newLibraryEntry(IPath path, ILoadpathAttribute[] extraAttributes, boolean isExported) {
				
			if (path == null) Assert.isTrue(false, "Library path cannot be null"); //$NON-NLS-1$
			if (!path.isAbsolute()) Assert.isTrue(false, "Path for ILoadpathEntry must be absolute"); //$NON-NLS-1$

			return new LoadpathEntry(
					ILoadpathEntry.CPE_LIBRARY,
				RubyProject.canonicalizedPath(path),
				LoadpathEntry.INCLUDE_ALL, // inclusion patterns
				LoadpathEntry.EXCLUDE_NONE, // exclusion patterns
				extraAttributes,
				isExported);
		
	}

	public static ILoadpathEntry newProjectEntry(IPath path, ILoadpathAttribute[] extraAttributes, boolean isExported) {
		if (!path.isAbsolute()) Assert.isTrue(false, "Path for ILoadpathEntry must be absolute"); //$NON-NLS-1$
		
		return new LoadpathEntry(
			ILoadpathEntry.CPE_PROJECT,
			path,
			LoadpathEntry.INCLUDE_ALL, // inclusion patterns
			LoadpathEntry.EXCLUDE_NONE, // exclusion patterns
			extraAttributes,
			isExported);
	}

	public static ILoadpathEntry newVariableEntry(IPath variablePath, ILoadpathAttribute[] extraAttributes, boolean isExported) {
		if (variablePath == null) Assert.isTrue(false, "Variable path cannot be null"); //$NON-NLS-1$
		if (variablePath.segmentCount() < 1) {
			Assert.isTrue(
				false,
				"Illegal loadpath variable path: \'" + variablePath.makeRelative().toString() + "\', must have at least one segment"); //$NON-NLS-1$//$NON-NLS-2$
		}
	
		return new LoadpathEntry(
			ILoadpathEntry.CPE_VARIABLE,
			variablePath,
			LoadpathEntry.INCLUDE_ALL, // inclusion patterns
			LoadpathEntry.EXCLUDE_NONE, // exclusion patterns	
			extraAttributes,
			isExported);
	}

	public static ILoadpathEntry newContainerEntry(IPath containerPath, ILoadpathAttribute[] extraAttributes,
			boolean isExported) {
		if (containerPath == null) {
			Assert.isTrue(false, "Container path cannot be null"); //$NON-NLS-1$
		} else if (containerPath.segmentCount() < 1) {
			Assert.isTrue(
				false,
				"Illegal loadpath container path: \'" + containerPath.makeRelative().toString() + "\', must have at least one segment (containerID+hints)"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return new LoadpathEntry(
			ILoadpathEntry.CPE_CONTAINER,
			containerPath,
			LoadpathEntry.INCLUDE_ALL, // inclusion patterns
			LoadpathEntry.EXCLUDE_NONE, // exclusion patterns
			extraAttributes,
			isExported);
	}

	public static ILoadpathEntry getResolvedLoadpathEntry(
			ILoadpathEntry entry) {
		if (entry.getEntryKind() != ILoadpathEntry.CPE_VARIABLE)
			return entry;
	
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath resolvedPath = RubyCore.getResolvedVariablePath(entry.getPath());
		if (resolvedPath == null)
			return null;
		
		Object target = RubyModel.getTarget(workspaceRoot, resolvedPath, false);
		if (target == null)
			return null;
	
		// inside the workspace
		if (target instanceof IResource) {
			IResource resolvedResource = (IResource) target;
			if (resolvedResource != null) {
				switch (resolvedResource.getType()) {
					
					case IResource.PROJECT :  
						// internal project
						return RubyCore.newProjectEntry(
								resolvedPath,
								entry.getExtraAttributes(),
								entry.isExported());					
					case IResource.FOLDER : 
						// internal binary folder
						return RubyCore.newLibraryEntry(
								resolvedPath,
								entry.getExtraAttributes(),
								entry.isExported());
				}
			}
		}
		// outside the workspace
		if (target instanceof File) {
			File externalFile = RubyModel.getFolder(target);
			if (externalFile != null) {
				return RubyCore.newLibraryEntry(resolvedPath, entry.getExtraAttributes(), entry.isExported());				
			} else { // external binary folder
				if (resolvedPath.isAbsolute()){
					return RubyCore.newLibraryEntry(resolvedPath, entry.getExtraAttributes(), entry.isExported());
				}
			}
		}
		return null;
	}

	/**
	 * Resolve a variable path (helper method).
	 * 
	 * @param variablePath the given variable path
	 * @return the resolved variable path or <code>null</code> if none
	 */
	public static IPath getResolvedVariablePath(IPath variablePath) {
	
		if (variablePath == null)
			return null;
		int count = variablePath.segmentCount();
		if (count == 0)
			return null;
	
		String variableName = variablePath.segment(0);
		IPath[] resolvedPaths = RubyCore.getLoadpathVariable(variableName);
		if (resolvedPaths == null) {
			log(Status.INFO, "Was unable to resolve path: " + variablePath.toPortableString());
			return null;
		}
	
		// append path suffix
		if (count > 1) {
			for (int i = 0; i < resolvedPaths.length; i++) {
				resolvedPaths[i] = resolvedPaths[i].append(variablePath.removeFirstSegments(1));
			}
		}
				
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath resolvedPath = null;
		Object target = null;
		for (int i = 0; i < resolvedPaths.length; i++) {
			target = RubyModel.getTarget(workspaceRoot, resolvedPaths[i], false);
			if (target instanceof File) {
				File targetFile = (File) target;
				if (!targetFile.exists()) {
					target = null;
				}
			}
			if (target != null) {
				resolvedPath = resolvedPaths[i];
				break;
			}
		}
		if (target == null) {
			log(Status.INFO, "Was unable to resolve path: " + variablePath.toPortableString());
			return null;
		}
		
		return resolvedPath; 
	}
	
	/**
	 * Returns the path held in the given loadpath variable.
	 * Returns <code>null</code> if unable to bind.
	 * <p>
	 * Loadpath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 * Note that loadpath variables can be contributed registered initializers for,
	 * using the extension point "org.rubypeople.rdt.core.loadpathVariableInitializer".
	 * If an initializer is registered for a variable, its persisted value will be ignored:
	 * its initializer will thus get the opportunity to rebind the variable differently on
	 * each session.
	 *
	 * @param variableName the name of the loadpath variable
	 * @return the path, or <code>null</code> if none 
	 * @see #setLoadpathVariable(String, IPath)
	 */
	public static IPath[] getLoadpathVariable(final String variableName) {

		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		IPath[] variablePath = manager.variableGet(variableName);
		if (variablePath == RubyModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS){
		    return manager.getPreviousSessionVariable(variableName);
		}
		
		if (variablePath != null) {
			if (variablePath == RubyModelManager.CP_ENTRY_IGNORE_PATH)
				return null;
			return variablePath;
		}

		// even if persisted value exists, initializer is given priority, only if no initializer is found the persisted value is reused
		final LoadpathVariableInitializer initializer = RubyCore.getLoadpathVariableInitializer(variableName);
		if (initializer != null){
			if (RubyModelManager.CP_RESOLVE_VERBOSE){
				Util.verbose(
					"CPVariable INIT - triggering initialization\n" + //$NON-NLS-1$
					"	variable: " + variableName + '\n' + //$NON-NLS-1$
					"	initializer: " + initializer + '\n' + //$NON-NLS-1$
					"	invocation stack trace:"); //$NON-NLS-1$
				new Exception("<Fake exception>").printStackTrace(System.out); //$NON-NLS-1$
			}
			manager.variablePut(variableName, RubyModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS); // avoid initialization cycles
			boolean ok = false;
			try {
				// let OperationCanceledException go through
				// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=59363)
				initializer.initialize(variableName);
				
				variablePath = manager.variableGet(variableName); // initializer should have performed side-effect
				if (variablePath == RubyModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS) return null; // break cycle (initializer did not init or reentering call)
				if (RubyModelManager.CP_RESOLVE_VERBOSE){
					Util.verbose(
						"CPVariable INIT - after initialization\n" + //$NON-NLS-1$
						"	variable: " + variableName +'\n' + //$NON-NLS-1$
						"	variable path: " + variablePath); //$NON-NLS-1$
				}
				manager.variablesWithInitializer.add(variableName);
				ok = true;
			} catch (RuntimeException e) {
				if (RubyModelManager.CP_RESOLVE_VERBOSE) {
					e.printStackTrace();
				}
				throw e;
			} catch (Error e) {
				if (RubyModelManager.CP_RESOLVE_VERBOSE) {
					e.printStackTrace();
				}
				throw e;
			} finally {
				if (!ok) RubyModelManager.getRubyModelManager().variablePut(variableName, null); // flush cache
			}
		} else {
			if (RubyModelManager.CP_RESOLVE_VERBOSE){
				Util.verbose(
					"CPVariable INIT - no initializer found\n" + //$NON-NLS-1$
					"	variable: " + variableName); //$NON-NLS-1$
			}
		}
		return variablePath;
	}

	/**
	 * Helper method finding the loadpath variable initializer registered for a given loadpath variable name 
	 * or <code>null</code> if none was found while iterating over the contributions to extension point to
	 * the extension point "org.rubypeople.rdt.core.loadpathVariableInitializer".
	 * <p>
 	 * @param variable the given variable
 	 * @return LoadpathVariableInitializer - the registered loadpath variable initializer or <code>null</code> if 
	 * none was found.
	 * @since 0.9.0
 	 */
	public static LoadpathVariableInitializer getLoadpathVariableInitializer(String variable){
		
		Plugin jdtCorePlugin = RubyCore.getPlugin();
		if (jdtCorePlugin == null) return null;

		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(RubyCore.PLUGIN_ID, RubyModelManager.CPVARIABLE_INITIALIZER_EXTPOINT_ID);
		if (extension != null) {
			IExtension[] extensions =  extension.getExtensions();
			for(int i = 0; i < extensions.length; i++){
				IConfigurationElement [] configElements = extensions[i].getConfigurationElements();
				for(int j = 0; j < configElements.length; j++){
					try {
						String varAttribute = configElements[j].getAttribute("variable"); //$NON-NLS-1$
						if (variable.equals(varAttribute)) {
							if (RubyModelManager.CP_RESOLVE_VERBOSE) {
								Util.verbose(
									"CPVariable INIT - found initializer\n" + //$NON-NLS-1$
									"	variable: " + variable + '\n' + //$NON-NLS-1$
									"	class: " + configElements[j].getAttribute("class")); //$NON-NLS-1$ //$NON-NLS-2$
							}						
							Object execExt = configElements[j].createExecutableExtension("class"); //$NON-NLS-1$
							if (execExt instanceof LoadpathVariableInitializer){
								return (LoadpathVariableInitializer)execExt;
							}
						}
					} catch(CoreException e){
						// executable extension could not be created: ignore this initializer
						if (RubyModelManager.CP_RESOLVE_VERBOSE) {
							Util.verbose(
								"CPContainer INIT - failed to instanciate initializer\n" + //$NON-NLS-1$
								"	variable: " + variable + '\n' + //$NON-NLS-1$
								"	class: " + configElements[j].getAttribute("class"), //$NON-NLS-1$ //$NON-NLS-2$
								System.err); 
							e.printStackTrace();
						}						
					}
				}
			}	
		}
		return null;
	}	
	
	public static ILoadpathContainer getLoadpathContainer(IPath containerPath, IRubyProject project) throws RubyModelException {

		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		ILoadpathContainer container = manager.getLoadpathContainer(containerPath, project);
		if (container == RubyModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS) {
		    return manager.getPreviousSessionContainer(containerPath, project);
		}
		return container;			
	}

	/**
	 * Helper method finding the classpath container initializer registered for a given classpath container ID 
	 * or <code>null</code> if none was found while iterating over the contributions to extension point to
	 * the extension point "org.eclipse.jdt.core.classpathContainerInitializer".
	 * <p>
	 * A containerID is the first segment of any container path, used to identify the registered container initializer.
	 * <p>
	 * @param containerID - a containerID identifying a registered initializer
	 * @return ClasspathContainerInitializer - the registered classpath container initializer or <code>null</code> if 
	 * none was found.
	 * @since 0.9.0
	 */
	public static LoadpathContainerInitializer getLoadpathContainerInitializer(String containerID) {
		HashMap<String, LoadpathContainerInitializer> containerInitializersCache = RubyModelManager.getRubyModelManager().containerInitializersCache;
		LoadpathContainerInitializer initializer = (LoadpathContainerInitializer) containerInitializersCache.get(containerID);
		if (initializer == null) {
			initializer = computeLoadpathContainerInitializer(containerID);
			if (initializer == null)
				return null;
			containerInitializersCache.put(containerID, initializer);
		}
		return initializer;
	}
	
	private static LoadpathContainerInitializer computeLoadpathContainerInitializer(String containerID) {
		Plugin jdtCorePlugin = RubyCore.getPlugin();
		if (jdtCorePlugin == null) return null;

		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(RubyCore.PLUGIN_ID, RubyModelManager.CPCONTAINER_INITIALIZER_EXTPOINT_ID);
		if (extension != null) {
			IExtension[] extensions =  extension.getExtensions();
			for(int i = 0; i < extensions.length; i++){
				IConfigurationElement [] configElements = extensions[i].getConfigurationElements();
				for(int j = 0; j < configElements.length; j++){
					String initializerID = configElements[j].getAttribute("id"); //$NON-NLS-1$
					if (initializerID != null && initializerID.equals(containerID)){
						if (RubyModelManager.CP_RESOLVE_VERBOSE) {
							Util.verbose(
								"CPContainer INIT - found initializer\n" + //$NON-NLS-1$
								"	container ID: " + containerID + '\n' + //$NON-NLS-1$
								"	class: " + configElements[j].getAttribute("class")); //$NON-NLS-1$ //$NON-NLS-2$
						}						
						try {
							Object execExt = configElements[j].createExecutableExtension("class"); //$NON-NLS-1$
							if (execExt instanceof LoadpathContainerInitializer){
								return (LoadpathContainerInitializer)execExt;
							}
						} catch(CoreException e) {
							// executable extension could not be created: ignore this initializer
							if (RubyModelManager.CP_RESOLVE_VERBOSE) {
								Util.verbose(
									"CPContainer INIT - failed to instanciate initializer\n" + //$NON-NLS-1$
									"	container ID: " + containerID + '\n' + //$NON-NLS-1$
									"	class: " + configElements[j].getAttribute("class"), //$NON-NLS-1$ //$NON-NLS-2$
									System.err); 
								e.printStackTrace();
							}						
						}
					}
				}
			}	
		}
		return null;
	}
	
	public static void setLoadpathContainer(final IPath containerPath, IRubyProject[] affectedProjects, ILoadpathContainer[] respectiveContainers, IProgressMonitor monitor) throws RubyModelException {

		if (affectedProjects.length != respectiveContainers.length) Assert.isTrue(false, "Projects and containers collections should have the same size"); //$NON-NLS-1$
	
		if (monitor != null && monitor.isCanceled()) return;
	
		if (RubyModelManager.CP_RESOLVE_VERBOSE){
			Util.verbose(
				"CPContainer SET  - setting container\n" + //$NON-NLS-1$
				"	container path: " + containerPath + '\n' + //$NON-NLS-1$
				"	projects: {" +//$NON-NLS-1$
				org.rubypeople.rdt.core.util.Util.toString(
					affectedProjects, 
					new org.rubypeople.rdt.core.util.Util.Displayable(){ 
						public String displayString(Object o) { return ((IRubyProject) o).getElementName(); }
					}) +
				"}\n	values: {\n"  +//$NON-NLS-1$
				org.rubypeople.rdt.core.util.Util.toString(
					respectiveContainers, 
					new org.rubypeople.rdt.core.util.Util.Displayable(){ 
						public String displayString(Object o) { 
							StringBuffer buffer = new StringBuffer("		"); //$NON-NLS-1$
							if (o == null) {
								buffer.append("<null>"); //$NON-NLS-1$
								return buffer.toString();
							}
							ILoadpathContainer container = (ILoadpathContainer) o;
							buffer.append(container.getDescription());
							buffer.append(" {\n"); //$NON-NLS-1$
							ILoadpathEntry[] entries = container.getLoadpathEntries();
							if (entries != null){
								for (int i = 0; i < entries.length; i++){
									buffer.append(" 			"); //$NON-NLS-1$
									buffer.append(entries[i]); 
									buffer.append('\n'); 
								}
							}
							buffer.append(" 		}"); //$NON-NLS-1$
							return buffer.toString();
						}
					}) +
				"\n	}\n	invocation stack trace:"); //$NON-NLS-1$
				new Exception("<Fake exception>").printStackTrace(System.out); //$NON-NLS-1$
		}
		
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		if (manager.containerPutIfInitializingWithSameEntries(containerPath, affectedProjects, respectiveContainers))
			return;

		final int projectLength = affectedProjects.length;	
		final IRubyProject[] modifiedProjects;
		System.arraycopy(affectedProjects, 0, modifiedProjects = new IRubyProject[projectLength], 0, projectLength);
		final ILoadpathEntry[][] oldResolvedPaths = new ILoadpathEntry[projectLength][];
			
		// filter out unmodified project containers
		int remaining = 0;
		for (int i = 0; i < projectLength; i++){
	
			if (monitor != null && monitor.isCanceled()) return;
	
			RubyProject affectedProject = (RubyProject) affectedProjects[i];
			ILoadpathContainer newContainer = respectiveContainers[i];
			if (newContainer == null) newContainer = RubyModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS; // 30920 - prevent infinite loop
			boolean found = false;
			if (RubyProject.hasRubyNature(affectedProject.getProject())){
				ILoadpathEntry[] rawClasspath = affectedProject.getRawLoadpath();
				for (int j = 0, cpLength = rawClasspath.length; j <cpLength; j++) {
					ILoadpathEntry entry = rawClasspath[j];
					if (entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER && entry.getPath().equals(containerPath)){
						found = true;
						break;
					}
				}
			}
			if (!found){
				modifiedProjects[i] = null; // filter out this project - does not reference the container path, or isnt't yet Java project
				manager.containerPut(affectedProject, containerPath, newContainer);
				continue;
			}
			ILoadpathContainer oldContainer = manager.containerGet(affectedProject, containerPath);
			if (oldContainer == RubyModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS) {
					oldContainer = null;
			}
			if (oldContainer != null && oldContainer.equals(respectiveContainers[i])){
				modifiedProjects[i] = null; // filter out this project - container did not change
				continue;
			}
			remaining++; 
			oldResolvedPaths[i] = affectedProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
			manager.containerPut(affectedProject, containerPath, newContainer);
		}
		
		if (remaining == 0) return;
		
		// trigger model refresh
		try {
			final boolean canChangeResources = !ResourcesPlugin.getWorkspace().isTreeLocked();
			RubyCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor progressMonitor) throws CoreException {
					for(int i = 0; i < projectLength; i++){
		
						if (progressMonitor != null && progressMonitor.isCanceled()) return;
		
						RubyProject affectedProject = (RubyProject)modifiedProjects[i];
						if (affectedProject == null) continue; // was filtered out
						
						if (RubyModelManager.CP_RESOLVE_VERBOSE){
							Util.verbose(
								"CPContainer SET  - updating affected project due to setting container\n" + //$NON-NLS-1$
								"	project: " + affectedProject.getElementName() + '\n' + //$NON-NLS-1$
								"	container path: " + containerPath); //$NON-NLS-1$
						}

						// force a refresh of the affected project (will compute deltas)
						affectedProject.setRawLoadpath(
								affectedProject.getRawLoadpath(),
								SetLoadpathOperation.DO_NOT_SET_OUTPUT,
								progressMonitor,
								canChangeResources,
								oldResolvedPaths[i],
								false, // updating - no need for early validation
								false); // updating - no need to save
					}
				}
			},
			null/*no need to lock anything*/,
			monitor);
		} catch(CoreException e) {
			if (RubyModelManager.CP_RESOLVE_VERBOSE){
				Util.verbose(
					"CPContainer SET  - FAILED DUE TO EXCEPTION\n" + //$NON-NLS-1$
					"	container path: " + containerPath, //$NON-NLS-1$
					System.err);
				e.printStackTrace();
			}
			if (e instanceof RubyModelException) {
				throw (RubyModelException)e;
			} else {
				throw new RubyModelException(e);
			}
		} finally {
			for (int i = 0; i < projectLength; i++) {
				if (respectiveContainers[i] == null) {
					manager.containerPut(affectedProjects[i], containerPath, null); // reset init in progress marker
				}
			}
		}
					
	}

	/**
	 * Sets the value of the given loadpath variable.
	 * The path must not be null.
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * Loadpath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 * Updating a variable with the same value has no effect.
	 *
	 * @param variableName the name of the loadpath variable
	 * @param path the path
	 * @param monitor a monitor to report progress
	 * @throws RubyModelException
	 * @see #getLoadpathVariable(String)
	 */
	public static void setLoadpathVariable(
		String variableName,
		IPath[] path,
		IProgressMonitor monitor)
		throws RubyModelException {

		if (path == null) Assert.isTrue(false, "Variable path cannot be null"); //$NON-NLS-1$
		setLoadpathVariables(new String[]{variableName}, new IPath[][]{ path }, monitor);
	}
	
	/**
	 * Sets the values of all the given loadpath variables at once.
	 * Null paths can be used to request corresponding variable removal.
	 * <p>
	 * A combined Ruby element delta will be notified to describe the corresponding 
	 * loadpath changes resulting from the variables update. This operation is batched, 
	 * and automatically eliminates unnecessary updates (new variable is same as old one). 
	 * This operation acquires a lock on the workspace's root.
	 * <p>
	 * This functionality cannot be used while the workspace is locked, since
	 * it may create/remove some resource markers.
	 * <p>
	 * Loadpath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 * Updating a variable with the same value has no effect.
	 * 
	 * @param variableNames an array of names for the updated loadpath variables
	 * @param paths an array of path updates for the modified loadpath variables (null
	 *       meaning that the corresponding value will be removed
	 * @param monitor a monitor to report progress
	 * @throws RubyModelException
	 * @see #getLoadpathVariable(String)
	 * @since 0.9.0
	 */
	public static void setLoadpathVariables(
		String[] variableNames,
		IPath[][] paths,
		IProgressMonitor monitor)
		throws RubyModelException {

		if (variableNames.length != paths.length)	Assert.isTrue(false, "Variable names and paths collections should have the same size"); //$NON-NLS-1$
		RubyModelManager.getRubyModelManager().updateVariableValues(variableNames, paths, true/*update preferences*/, monitor);
	}

	public static ILoadpathEntry newProjectEntry(IPath fullPath) {
		return newProjectEntry(fullPath, LoadpathEntry.NO_EXTRA_ATTRIBUTES, false);
	}

	public static ILoadpathEntry newVariableEntry(IPath path) {
		return newVariableEntry(path, LoadpathEntry.NO_EXTRA_ATTRIBUTES, false);
	}

	public static ILoadpathEntry newContainerEntry(IPath path) {
		return newContainerEntry(path, LoadpathEntry.NO_EXTRA_ATTRIBUTES, false);
	}

	public static ILoadpathEntry newLibraryEntry(IPath p) {
		return newLibraryEntry(p, LoadpathEntry.NO_EXTRA_ATTRIBUTES, false);
	}

	/**
	 * Creates and returns a new loadpath attribute with the given name and the given value.
	 * 
	 * @return a new loadpath attribute
	 * @since 0.9.0
	 */
	public static ILoadpathAttribute newLoadpathAttribute(String name, String value) {
		return new LoadpathAttribute(name, value);
	}

	public static ILoadpathEntry newLibraryEntry(IPath path, boolean isExported) {
		return newLibraryEntry(path, LoadpathEntry.NO_EXTRA_ATTRIBUTES, isExported);
	}

	public static ILoadpathEntry newVariableEntry(IPath path, boolean isExported) {
		return newVariableEntry(path, LoadpathEntry.NO_EXTRA_ATTRIBUTES, isExported);
	}

	public static ILoadpathEntry newProjectEntry(IPath path, boolean isExported) {
		return newProjectEntry(path, LoadpathEntry.NO_EXTRA_ATTRIBUTES, isExported);
	}

	public static ILoadpathEntry newContainerEntry(IPath path, boolean isExported) {
		return newContainerEntry(path, LoadpathEntry.NO_EXTRA_ATTRIBUTES, isExported);
	}

 	/**
 	 * Returns the Ruby model element corresponding to the given handle identifier
 	 * generated by <code>IRubyElement.getHandleIdentifier()</code>, or
 	 * <code>null</code> if unable to create the associated element.
	 *
	 * @param handleIdentifier the given handle identifier
 	 * @return the Ruby element corresponding to the handle identifier
 	 */
 	public static IRubyElement create(String handleIdentifier) {
 		return create(handleIdentifier, DefaultWorkingCopyOwner.PRIMARY);
 	}
 
 	/**
 	 * Returns the Ruby model element corresponding to the given handle identifier
 	 * generated by <code>IRubyElement.getHandleIdentifier()</code>, or
 	 * <code>null</code> if unable to create the associated element.
 	 * If the returned Ruby element is an <code>ICompilationUnit</code>, its owner
 	 * is the given owner if such a working copy exists, otherwise the compilation unit
 	 * is a primary compilation unit.
 	 *
 	 * @param handleIdentifier the given handle identifier
 	 * @param owner the owner of the returned compilation unit, ignored if the returned
 	 *   element is not a compilation unit
 	 * @return the Ruby element corresponding to the handle identifier
 	 * @since 3.0
 	 */
 	public static IRubyElement create(String handleIdentifier, WorkingCopyOwner owner) {
 		if (handleIdentifier == null) {
 			return null;
		}
		MementoTokenizer memento = new MementoTokenizer(handleIdentifier);
		RubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
 		return model.getHandleFromMemento(memento, owner);
 	}

	/**
	 * Creates and returns a new loadpath entry of kind <code>CPE_SOURCE</code>
	 * for the project's source folder identified by the given absolute 
	 * workspace-relative path but excluding all source files with paths
	 * matching any of the given patterns.
	 * <p>
	 * The convenience method is fully equivalent to:
	 * <pre>
	 * newSourceEntry(path, new IPath[] {}, exclusionPatterns, null);
	 * </pre>
	 * </p>
	 *
	 * @param path the absolute workspace-relative path of a source folder
	 * @param exclusionPatterns the possibly empty list of exclusion patterns
	 *    represented as relative paths
	 * @return a new source loadpath entry
	 * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
	 * @since 2.1
	 */
	public static ILoadpathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns) {
		return newSourceEntry(path, LoadpathEntry.INCLUDE_ALL, exclusionPatterns, LoadpathEntry.NO_EXTRA_ATTRIBUTES); 
	}
	
	/**
	 * Creates and returns a compilation unit element for
	 * the given source file (i.e. a file with one of the {@link RubyCore#getRubyLikeExtensions() 
	 * Java-like extensions}). Returns <code>null</code> if unable
	 * to recognize the compilation unit.
	 * 
	 * @param file the given source file
	 * @return a compilation unit element for the given source file, or <code>null</code> if unable
	 * to recognize the compilation unit
	 */
	public static IRubyScript createRubyScriptFrom(IFile file) {
		return RubyModelManager.createRubyScriptFrom(file, null/*unknown ruby project*/);
	}

	
	private static class RubyProjectListener implements IResourceChangeListener {

		public void resourceChanged(IResourceChangeEvent event) {
			if (event == null)
				return;
			IResourceDelta delta = event.getDelta();
			checkDelta(delta);
		}

		private void checkDelta(IResourceDelta delta) {
			if (delta == null)
				return;
			IResource resource = delta.getResource();
			if (resource instanceof IProject) {
				// if (IResourceDelta.ADDED != delta.getKind()) //
				// FIXME Try to narrow down which deltas we actually
				// check
				// return;
				final IProject project = (IProject) resource;
				if (!RubyProject.hasRubyNature(project)) {
					// check for ruby scripts, and if it has any,
					// add the nature.
					IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
						private boolean added = false;

						public boolean visit(IResourceProxy proxy)
								throws CoreException {
							if (proxy.getType() == IResource.FILE) {
								if (RubyCore
										.isRubyLikeFileName(proxy.getName())) {
									Job job = new Job("Add Ruby Nature") {

										@Override
										protected IStatus run(
												IProgressMonitor monitor) {
											try {
												RubyCore.addRubyNature(project,
														monitor);
											} catch (CoreException e) {
												RubyCore.log(e);
											}
											return Status.OK_STATUS;
										}

									};
									job.schedule(500);
									added = true;
								}
							}
							return !added;
						}

					};
					try {
						project.accept(visitor, IResource.NONE);
					} catch (CoreException e) {
						RubyCore.log(e);
					}
				}
				IResourceDelta[] children = delta.getAffectedChildren();
				for (int i = 0; i < children.length; i++) {
					checkDelta(children[i]);
				}
			}
		}
	}

	/**
	 * Returns a new empty region.
	 * 
	 * @return a new empty region
	 */
	public static IRegion newRegion() {
		return new Region();
	}

	/**
	 * Returns the names of all known loadpath variables.
	 * <p>
	 * Loadpath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 *
	 * @return the list of loadpath variable names
	 * @see #setLoadpathVariable(String, IPath)
	 */
	public static String[] getLoadpathVariableNames() {
		return RubyModelManager.getRubyModelManager().variableNames();
	}
	
	/**
	 * Initializes RubyCore internal structures to allow subsequent operations (such 
	 * as the ones that need a resolved classpath) to run full speed. A client may 
	 * choose to call this method in a background thread early after the workspace 
	 * has started so that the initialization is transparent to the user.
	 * <p>
	 * However calling this method is optional. Services will lazily perform 
	 * initialization when invoked. This is only a way to reduce initialization 
	 * overhead on user actions, if it can be performed before at some 
	 * appropriate moment.
	 * </p><p>
	 * This initialization runs accross all Ruby projects in the workspace. Thus the
	 * workspace root scheduling rule is used during this operation.
	 * </p><p>
	 * This method may return before the initialization is complete. The 
	 * initialization will then continue in a background thread.
	 * </p><p>
	 * This method can be called concurrently.
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if the initialization fails, 
	 * 		the status of the exception indicates the reason of the failure
	 * @since 3.1
	 */
	public static void initializeAfterLoad(IProgressMonitor monitor) throws CoreException {
		try {
			if (monitor != null) monitor.beginTask(Messages.javamodel_initialization, 100);
			// dummy query for waiting until the indexes are ready and classpath containers/variables are initialized
			SearchEngine engine = new SearchEngine();
			IRubySearchScope scope = SearchEngine.createWorkspaceScope(); // initialize all containers and variables
			try {
				engine.searchAllTypeNames(
					null,
					"!@$#!@".toCharArray(), //$NON-NLS-1$
					SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE,
					IRubySearchConstants.CLASS,
					scope, 
					new TypeNameRequestor() {
						public void acceptType(
							boolean isModule,
							char[] packageName,
							char[] simpleTypeName,
							char[][] enclosingTypeNames,
							String path) {
							// no type to accept
						}
					},
					// will not activate index query caches if indexes are not ready, since it would take to long
					// to wait until indexes are fully rebuild
					IRubySearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH,
					monitor == null ? null : new SubProgressMonitor(monitor, 99) // 99% of the time is spent in the dummy search
				); 
			} catch (RubyModelException e) {
				// /search failed: ignore
			} catch (OperationCanceledException e) {
				if (monitor != null && monitor.isCanceled())
					throw e;
				// else indexes were not ready: catch the exception so that jars are still refreshed
			}			
			final RubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			// ensure external jars are refreshed (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=93668)
			try {
				model.refreshExternalArchives(
					null/*refresh all projects*/, 
					monitor == null ? null : new SubProgressMonitor(monitor, 1) // 1% of the time is spent in jar refresh
				);
			} catch (RubyModelException e) {
				// refreshing failed: ignore
			}
		} finally {
			if (monitor != null) monitor.done();
		}
	}
	
	/**
	 * Iterate through system path and try to find matching executable
	 * @param exe
	 * @return IPath to executable if found, null otherwise
	 */
	public static IPath checkSystemPath(String exe) {
		String systemPath = System.getenv("PATH");
		if (systemPath == null) return null;
		String[] paths = systemPath.split(File.pathSeparator);
		return checkDirs(exe, paths);
	}

	/**
	 * Check common bin/exe locations on non-win systems
	 * @param exe
	 * @return
	 */
	public static IPath checkCommonBinLocations(String exe) {
		if (Platform.getOS().equals(Platform.OS_WIN32)) return null;
		String[] paths = new String[] {"/opt/local/bin", "/usr/local/bin", "/usr/sbin", "/usr/bin", "/sbin", "/bin"};
		return checkDirs(exe, paths);
	}

	/**
	 * Iterate through paths and try to find matching executable inside the directory
	 * @param exe
	 * @return IPath to executable if found, null otherwise
	 */
	private static IPath checkDirs(String exe, String[] paths) {
		for (int i = 0; i < paths.length; i++) {
			IPath path = new Path(paths[i]).append(exe);
			if (path.toFile().exists()) return path;
		}
		return null;
	}
}

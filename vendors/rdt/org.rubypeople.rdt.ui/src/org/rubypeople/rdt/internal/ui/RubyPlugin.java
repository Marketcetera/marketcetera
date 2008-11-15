/*
 * Copyright (c) 2003-2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.ui;

import java.io.IOException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ConfigurationElementSorter;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IRubyInformation;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.corext.util.OpenTypeHistory;
import org.rubypeople.rdt.internal.corext.util.TypeFilter;
import org.rubypeople.rdt.internal.formatter.OldCodeFormatter;
import org.rubypeople.rdt.internal.ui.infoviews.RiUtility;
import org.rubypeople.rdt.internal.ui.packageview.PackageExplorerPart;
import org.rubypeople.rdt.internal.ui.preferences.MembersOrderPreferenceCache;
import org.rubypeople.rdt.internal.ui.preferences.MockupPreferenceStore;
import org.rubypeople.rdt.internal.ui.rubyeditor.ASTProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.DocumentAdapter;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyDocumentProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyScriptDocumentProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.WorkingCopyManager;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.internal.ui.text.folding.RubyFoldingStructureProviderRegistry;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.RubyEditorTextHoverDescriptor;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.RubyTemplateAccess;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemMarkerManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.viewsupport.ImageDescriptorRegistry;

/**
 * Ruby Plugin class
 */
public class RubyPlugin extends AbstractUIPlugin
{

	/**
	 * plugin
	 */
	protected static RubyPlugin plugin;

	/**
	 * PLUGIN_ID
	 */
	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui"; //$NON-NLS-1$

	/**
	 * textTools
	 */
	protected RubyTextTools textTools;

	/**
	 * rubyFileMatcher
	 */
	protected RubyFileMatcher rubyFileMatcher;
	private WorkingCopyManager fWorkingCopyManager;
	private RubyDocumentProvider fDocumentProvider;

	/**
	 * pluginProperties
	 */
	protected PropertyResourceBundle pluginProperties;

	/**
	 * The combined preference store.
	 * 
	 * @since 3.0
	 */
	private IPreferenceStore fCombinedPreferenceStore;

	/**
	 * Mockup preference store for firing events and registering listeners on project setting changes. FIXME: Temporary
	 * solution.
	 * 
	 * @since 3.0
	 */
	private MockupPreferenceStore fMockupPreferenceStore;

	private RubyFoldingStructureProviderRegistry fFoldingStructureProviderRegistry;
	private ImageDescriptorRegistry fImageDescriptorRegistry;
	private MembersOrderPreferenceCache fMembersOrderPreferenceCache;
	private RubyScriptDocumentProvider fExternalRubyDocumentProvider;

	private RubyEditorTextHoverDescriptor[] fRubyEditorTextHoverDescriptors;

	/**
	 * Default instance of the appearance type filters.
	 * 
	 * @since 1.0
	 */
	private TypeFilter fTypeFilter;
	private ProblemMarkerManager fProblemMarkerManager;
	private ASTProvider fASTProvider;

	private ILaunchListener launchListener;
	private RubyExplorerTracker fRubyExplorerTracker;

	/**
	 * Ruby plugin default constructor
	 */
	public RubyPlugin()
	{
		super();
		plugin = this;
	}

	/**
	 * Returns the mock-up preference store for firing events and registering listeners on project setting changes.
	 * Temporary solution.
	 * 
	 * @return the mock-up preference store
	 */
	public MockupPreferenceStore getMockupPreferenceStore()
	{
		if (fMockupPreferenceStore == null)
			fMockupPreferenceStore = new MockupPreferenceStore();

		return fMockupPreferenceStore;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);

		context.registerService(IRubyInformation.class.getName(), new RiUtility(), null);
		// initialize textTools before any RubyEditor gets initialized, so that
		// textTools can
		// register for property change events first. If the registration takes
		// place in the
		// wrong order, changes to properties in the preferences page are not
		// immediately updated
		// within the ruby editors.
		// getTextTools();

		// Here's where the magic happens that makes the IRubyScript's contents
		// get re-routed to the IDocument's latest contents
		WorkingCopyOwner.setPrimaryBufferProvider(new WorkingCopyOwner()
		{

			public IBuffer createBuffer(IRubyScript workingCopy)
			{
				IRubyScript original = workingCopy.getPrimary();
				IResource resource = original.getResource();
				if (resource instanceof IFile)
					return new DocumentAdapter(workingCopy, (IFile) resource);
				return DocumentAdapter.NULL;
			}
		});

		ensurePreferenceStoreBackwardsCompatibility();

		// Swap in our process console manager for theirs
		launchListener = new AptanaProcessConsoleManager();
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);

		// Initialize AST provider
		getASTProvider();

		// Removed by Will
//		fRubyExplorerTracker = new RubyExplorerTracker();

//		new InitializeAfterLoadJob().schedule();
	}

	/**
	 * Installs backwards compatibility for the preference store.
	 */
	private void ensurePreferenceStoreBackwardsCompatibility()
	{

		IPreferenceStore store = getPreferenceStore();

		// must add here to guarantee that it is the first in the listener list
		fMembersOrderPreferenceCache = new MembersOrderPreferenceCache();
		fMembersOrderPreferenceCache.install(store);
	}

	/* package */static void initializeAfterLoad(IProgressMonitor monitor)
	{
		OpenTypeHistory.getInstance().checkConsistency(monitor);

		new RubyInstalledDetector().schedule();
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (fWorkingCopyManager != null)
			{
				fWorkingCopyManager.shutdown();
				fWorkingCopyManager = null;
			}

			if (fDocumentProvider != null)
			{
				fDocumentProvider.shutdown();
				fDocumentProvider = null;
			}
			if (textTools != null)
			{
				textTools.dispose();
				textTools = null;
			}

			if (fTypeFilter != null)
			{
				fTypeFilter.dispose();
				fTypeFilter = null;
			}

			if (fMembersOrderPreferenceCache != null)
			{
				fMembersOrderPreferenceCache.dispose();
				fMembersOrderPreferenceCache = null;
			}

			PackageExplorerPart explorer = PackageExplorerPart.getFromActivePerspective();
			if (explorer != null)
			{
				explorer.getSite().getSelectionProvider().removeSelectionChangedListener(fRubyExplorerTracker);
			}

			DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(launchListener);
			launchListener = null;
		}
		finally
		{
			super.stop(context);
		}
	}

	/**
	 * @param string
	 */
	public static void log(String string)
	{
		log(IStatus.OK, string);

	}

	/**
	 * Gets the default plugin
	 * 
	 * @return - ruby plugin
	 */
	public static RubyPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Gets the workspace
	 * 
	 * @return - workspace
	 */
	public static IWorkspace getWorkspace()
	{
		return RubyCore.getWorkspace();
	}

	/**
	 * Gets the active workbench window
	 * 
	 * @return - workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Logs a status object
	 * 
	 * @param status
	 */
	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
		System.out.println(status.getMessage());
		if (status.getException() != null)
			status.getException().printStackTrace();
	}

	/**
	 * Logs a throwable object
	 * 
	 * @param e
	 */
	public static void log(Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RubyUIMessages.RdtUiPlugin_internalErrorOccurred, e));
	}

	/**
	 * Logs a message with throwable
	 * 
	 * @param severity
	 * @param message
	 * @param e
	 */
	public static void log(int severity, String message, Throwable e)
	{
		Status status = new Status(severity, PLUGIN_ID, IStatus.OK, message, e);
		RubyPlugin.log(status);
	}

	/**
	 * Gets the active workbench shell
	 * 
	 * @return - shell
	 */
	public static Shell getActiveWorkbenchShell()
	{
		return getActiveWorkbenchWindow().getShell();
	}

	/**
	 * Gets the ruby text tools
	 * 
	 * @return - ruby text tools
	 */
	public synchronized RubyTextTools getRubyTextTools()
	{
		if (textTools == null)
			textTools = new RubyTextTools(getPreferenceStore(), RubyCore.getPlugin().getPluginPreferences());
		return textTools;
	}

	/**
	 * Gets the code formatter
	 * 
	 * @return - code formatter
	 */
	public OldCodeFormatter getCodeFormatter()
	{
		return new OldCodeFormatter(RubyCore.getOptions());
	}

	/**
	 * Gets the active page
	 * 
	 * @return - page
	 */
	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getActivePage();
	}

	/**
	 * Gets the ruby file matcher
	 * 
	 * @return - ruby file matcher
	 */
	public RubyFileMatcher getRubyFileMatcher()
	{
		// be lazy in Plugin class
		if (rubyFileMatcher == null)
		{
			rubyFileMatcher = new RubyFileMatcher();
		}
		return rubyFileMatcher;
	}

	/**
	 * Gets the selected resource
	 * 
	 * @return - resource
	 */
	public IResource getSelectedResource()
	{
		IWorkbenchPage page = RubyPlugin.getActivePage();
		if (page == null)
		{
			return null;
		}
		// first try: a selection in the navigator or ruby resource view
		ISelection selection = page.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object obj = structuredSelection.getFirstElement();
			if (obj instanceof IResource)
			{
				return (IResource) obj;
			}
		}
		// second try: an editor is selected
		IEditorPart part = page.getActiveEditor();
		if (part == null)
		{
			return null;
		}
		IEditorInput input = part.getEditorInput();
		return (IResource) input.getAdapter(IResource.class);
	}

	/**
	 * True is file is a ruby file
	 * 
	 * @param file
	 * @return - true if ruby file
	 */
	public boolean isRubyFile(IFile file)
	{
		// TODO: this is work in progress. Once we can use the content-type
		// extension point, this method must be removed
		return this.getRubyFileMatcher().hasRubyEditorAssociation(file);
	}

	/**
	 * True if resource is a ruby file
	 * 
	 * @param resource
	 * @return - true if ruby file
	 */
	public boolean isRubyFile(IResource resource)
	{
		if (resource == null || !(resource instanceof IFile))
		{
			return false;
		}
		return isRubyFile((IFile) resource);
	}

	/**
	 * @return - working copy manager
	 */
	public WorkingCopyManager getWorkingCopyManager()
	{
		if (fWorkingCopyManager == null)
		{
			RubyDocumentProvider provider = getRubyDocumentProvider();
			fWorkingCopyManager = new WorkingCopyManager(provider);
		}
		return fWorkingCopyManager;
	}

	/**
	 * Gets the ruby document provider
	 * 
	 * @return - ruby doc provider
	 */
	public synchronized RubyDocumentProvider getRubyDocumentProvider()
	{
		if (fDocumentProvider == null)
			fDocumentProvider = new RubyDocumentProvider();
		return fDocumentProvider;
	}

	/**
	 * Returns the registry of the extensions to the <code>org.rubypeople.rdt.ui.rubyFoldingStructureProvider</code>
	 * extension point.
	 * 
	 * @return the registry of contributed <code>IRubyFoldingStructureProvider</code>
	 * @since 3.0
	 */
	public synchronized RubyFoldingStructureProviderRegistry getFoldingStructureProviderRegistry()
	{
		if (fFoldingStructureProviderRegistry == null)
			fFoldingStructureProviderRegistry = new RubyFoldingStructureProviderRegistry();
		return fFoldingStructureProviderRegistry;
	}

	/**
	 * @return - plugin id
	 */
	public static String getPluginId()
	{
		return PLUGIN_ID;
	}

	/**
	 * @param severity
	 * @param string
	 */
	public static void log(int severity, String string)
	{
		log(new Status(severity, PLUGIN_ID, IStatus.OK, string, null));
	}

	/**
	 * Returns a combined preference store, this store is read-only.
	 * 
	 * @return the combined preference store
	 * @since 3.0
	 */
	public IPreferenceStore getCombinedPreferenceStore()
	{
		if (fCombinedPreferenceStore == null)
		{
			IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
			fCombinedPreferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] { getPreferenceStore(),
					new PreferencesAdapter(RubyCore.getPlugin().getPluginPreferences()), generalTextStore });
		}
		return fCombinedPreferenceStore;
	}

	/**
	 * Logs an error message
	 * 
	 * @param message
	 */
	public static void logErrorMessage(String message)
	{
		log(new Status(IStatus.ERROR, getPluginId(), IRubyStatusConstants.INTERNAL_ERROR, message, null));
	}

	/**
	 * Returns the template store for the java editor templates.
	 * 
	 * @return the template store for the ruby editor templates
	 * @since 3.0
	 */
	public TemplateStore getTemplateStore()
	{
		return RubyTemplateAccess.getDefault().getTemplateStore();
	}

	/**
	 * Returns the template context type registry for the ruby plugin.
	 * 
	 * @return the template context type registry for the ruby plugin
	 * @since 3.0
	 */
	public ContextTypeRegistry getTemplateContextRegistry()
	{
		return RubyTemplateAccess.getDefault().getContextTypeRegistry();
	}

	/**
	 * Is this plugin debugging
	 * 
	 * @return - true if debugging
	 */
	public static boolean isDebug()
	{
		return getDefault().isDebugging();
	}

	/**
	 * Creates the Ruby plugin standard groups in a context menu.
	 * 
	 * @param menu
	 *            the menu manager to be populated
	 */
	public static void createStandardGroups(IMenuManager menu)
	{
		if (!menu.isEmpty())
			return;
		menu.add(new Separator(IContextMenuConstants.GROUP_NEW));
		menu.add(new GroupMarker(IContextMenuConstants.GROUP_GOTO));
		menu.add(new Separator(IContextMenuConstants.GROUP_OPEN));
		menu.add(new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		menu.add(new Separator(ICommonMenuConstants.GROUP_EDIT));
		menu.add(new Separator(IContextMenuConstants.GROUP_REORGANIZE));
		menu.add(new Separator(IContextMenuConstants.GROUP_GENERATE));
		menu.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
		menu.add(new Separator(IContextMenuConstants.GROUP_BUILD));
		menu.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
		menu.add(new Separator(IContextMenuConstants.GROUP_VIEWER_SETUP));
		menu.add(new Separator(IContextMenuConstants.GROUP_PROPERTIES));
	}

	/**
	 * Gets the image descriptor registry
	 * 
	 * @return - image descriptor registry
	 */
	public static ImageDescriptorRegistry getImageDescriptorRegistry()
	{
		return getDefault().internalGetImageDescriptorRegistry();
	}

	private synchronized ImageDescriptorRegistry internalGetImageDescriptorRegistry()
	{
		if (fImageDescriptorRegistry == null)
			fImageDescriptorRegistry = new ImageDescriptorRegistry();
		return fImageDescriptorRegistry;
	}

	/**
	 * Gets members order preference cache
	 * 
	 * @return - cache
	 */
	public synchronized MembersOrderPreferenceCache getMemberOrderPreferenceCache()
	{
		// initialized on startup
		return fMembersOrderPreferenceCache;
	}

	/**
	 * Gets the external document provider
	 * 
	 * @return - ruby script doc provider
	 */
	public synchronized RubyScriptDocumentProvider getExternalDocumentProvider()
	{
		if (fExternalRubyDocumentProvider == null)
			fExternalRubyDocumentProvider = new RubyScriptDocumentProvider();
		return fExternalRubyDocumentProvider;
	}

	/**
	 * Gets the plugin properties
	 * 
	 * @return - property resource bundle
	 */
	public PropertyResourceBundle getPluginProperties()
	{

		if (pluginProperties == null)
		{
			try
			{

				pluginProperties = new PropertyResourceBundle(

				FileLocator.openStream(this.getBundle(),

				new Path("plugin.properties"), false));

			}
			catch (IOException e)
			{
				log(e);
			}
		}
		return pluginProperties;
	}

	/**
	 * Gets the type filter
	 * 
	 * @return - type filter
	 */
	public synchronized TypeFilter getTypeFilter()
	{
		if (fTypeFilter == null)
			fTypeFilter = new TypeFilter();
		return fTypeFilter;
	}

	/**
	 * Returns a section in the Ruby plugin's dialog settings. If the section doesn't exist yet, it is created.
	 * 
	 * @param name
	 *            the name of the section
	 * @return the section of the given name
	 * @since 1.0
	 */
	public IDialogSettings getDialogSettingsSection(String name)
	{
		IDialogSettings dialogSettings = getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(name);
		if (section == null)
		{
			section = dialogSettings.addNewSection(name);
		}
		return section;
	}

	/**
	 * Returns all Ruby editor text hovers contributed to the workbench.
	 * 
	 * @return an array of RubyEditorTextHoverDescriptor
	 * @since 1.0
	 */
	public RubyEditorTextHoverDescriptor[] getRubyEditorTextHoverDescriptors()
	{
		Preferences prefs = getPluginPreferences();
		if (prefs != null && !prefs.getBoolean(PreferenceConstants.HOVERS_ENABLED))
		{
			return new RubyEditorTextHoverDescriptor[0];
		}
		if (fRubyEditorTextHoverDescriptors == null)
		{
			fRubyEditorTextHoverDescriptors = RubyEditorTextHoverDescriptor.getContributedHovers();
			ConfigurationElementSorter sorter = new ConfigurationElementSorter()
			{
				/*
				 * @see org.eclipse.ui.texteditor.ConfigurationElementSorter#getConfigurationElement(java.lang.Object)
				 */
				public IConfigurationElement getConfigurationElement(Object object)
				{
					return ((RubyEditorTextHoverDescriptor) object).getConfigurationElement();
				}
			};
			sorter.sort(fRubyEditorTextHoverDescriptors);

			// Move Best Match hover to front
			for (int i = 0; i < fRubyEditorTextHoverDescriptors.length - 1; i++)
			{
				if (PreferenceConstants.ID_BESTMATCH_HOVER.equals(fRubyEditorTextHoverDescriptors[i].getId()))
				{
					RubyEditorTextHoverDescriptor hoverDescriptor = fRubyEditorTextHoverDescriptors[i];
					for (int j = i; j > 0; j--)
						fRubyEditorTextHoverDescriptors[j] = fRubyEditorTextHoverDescriptors[j - 1];
					fRubyEditorTextHoverDescriptors[0] = hoverDescriptor;
					break;
				}

			}
		}

		return fRubyEditorTextHoverDescriptors;
	}

	/**
	 * Gets the problem marker manager
	 * 
	 * @return - problem marker manager
	 */
	public synchronized ProblemMarkerManager getProblemMarkerManager()
	{
		if (fProblemMarkerManager == null)
			fProblemMarkerManager = new ProblemMarkerManager();
		return fProblemMarkerManager;
	}

	/**
	 * Returns the AST provider.
	 * 
	 * @return the AST provider
	 * @since 3.0
	 */
	public synchronized ASTProvider getASTProvider()
	{
		if (fASTProvider == null)
			fASTProvider = new ASTProvider();

		return fASTProvider;
	}

	/**
	 * Gets the project tracker
	 * 
	 * @return - project trakcer
	 */
	public RubyExplorerTracker getProjectTracker()
	{
		return fRubyExplorerTracker;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path); //$NON-NLS-1$
	}
}
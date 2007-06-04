package org.marketcetera.photon;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.HttpDatabaseIDFactory;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageVisitor;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.photon.preferences.ConnectionsPreferencePage;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.marketcetera.photon.scripting.ScriptChangesAdapter;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.photon.views.IOrderTicket;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.photon.views.OptionOrderTicket;
import org.marketcetera.photon.views.SecondaryIDCreator;
import org.marketcetera.photon.views.StockOrderTicket;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyCore;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Symbol;

/**
 * The main plugin class to be used in the Photon application.
 */
@ClassVersion("$Id$")
public class PhotonPlugin extends AbstractUIPlugin {

	public static final String ID = "org.marketcetera.photon";

	//The shared instance.
	private static PhotonPlugin plugin;

	private FIXMessageHistory fixMessageHistory;

	private Logger mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);

	private Logger marketDataLogger = Logger.getLogger(MARKETDATA_CONSOLE_LOGGER_NAME);

	private ScriptRegistry scriptRegistry;

	private PhotonController photonController;

	private IDFactory idFactory;

	private BundleContext bundleContext;
	
	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger";

	public static final String MARKETDATA_CONSOLE_LOGGER_NAME = "marketdata.console.logger";

	public static final String DEFAULT_PROJECT_NAME = "ActiveScripts";

	private static final String RUBY_NATURE_ID = ".rubynature";

	private ScriptChangesAdapter scriptChangesAdapter;

	private SimpleMessageListenerContainer registryListener;

	private FIXMessageFactory messageFactory;

	private FIXVersion fixVersion;

	private SecondaryIDCreator secondaryIDCreator = new SecondaryIDCreator();

	/**
	 * The constructor.
	 */
	public PhotonPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
		
		String level = getPreferenceStore().getString(PhotonPage.LOG_LEVEL_KEY);
		changeLogLevel(level == null ? PhotonPage.LOG_LEVEL_VALUE_INFO : level);
		
		// This sets the internal broker to use on thread per "listener"?
		// Needed because the version of JRuby we're using doesn't play well
		// with mutliple threads
        System.setProperty("org.apache.activemq.UseDedicatedTaskRunner", "true");

        BSFManager.registerScriptingEngine(ScriptRegistry.RUBY_LANG_STRING,
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });
		initResources();
		initMessageFactory();
		initIDFactory();
		initFIXMessageHistory();
		initScriptRegistry();
		initPhotonController();
	}

	private void initPhotonController() {
		photonController = new PhotonController();
		photonController.setMessageHistory(fixMessageHistory);
		photonController.setMainConsoleLogger(getMainConsoleLogger());
		photonController.setIDFactory(idFactory);
		photonController.setMessageFactory(messageFactory);

	}

	private void initFIXMessageHistory() {
		fixMessageHistory = new FIXMessageHistory(messageFactory);
	}

	private void initScriptRegistry() {
		scriptRegistry = new ScriptRegistry();
		scriptChangesAdapter = new ScriptChangesAdapter();
		scriptChangesAdapter.setRegistry(scriptRegistry);
		
	}



	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		
		if (scriptRegistry != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(scriptChangesAdapter);
			scriptRegistry = null;
		}
		if (registryListener != null)
			registryListener.stop();
	}

	/**
	 * Returns the shared instance.
	 */
	public static PhotonPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	@Override
	public ScopedPreferenceStore getPreferenceStore() {
		return (ScopedPreferenceStore) super.getPreferenceStore();
	}

	private void initResources() throws FIXFieldConverterNotAvailable
	{
		MessageBundleManager.registerCoreMessageBundle();
		MessageBundleManager.registerMessageBundle("photon", "photon_fix_messages");
	}

	private void initIDFactory() throws MalformedURLException, UnknownHostException
	{
		ScopedPreferenceStore preferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		URL url = new URL(
				"http",
				preferenceStore.getString(ConnectionConstants.WEB_APP_HOST_KEY),
				preferenceStore.getInt(ConnectionConstants.WEB_APP_PORT_KEY),
				"/id_repository/get_next_batch"
		);
		idFactory = new HttpDatabaseIDFactory(url);

	}
	
	private void initMessageFactory() throws FIXFieldConverterNotAvailable {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		String versionString = thePreferenceStore.getString(ConnectionsPreferencePage.FIX_VERSION_PREFERENCE);
		fixVersion = FIXVersion.FIX42;
		try {
			fixVersion = FIXVersion.valueOf(versionString);
		} catch (IllegalArgumentException iae) {
			// just use version 4.2
		}
		messageFactory = fixVersion.getMessageFactory();
		FIXDataDictionaryManager.initialize(FIXVersion.FIX44, "FIX44.xml");
		FIXDataDictionaryManager.initialize(fixVersion, fixVersion.getDataDictionaryURL());
	}

	public void startScriptRegistry() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		try {
			scriptChangesAdapter.setInitialRegistryValueString(thePreferenceStore.getString(ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE));
			scriptRegistry.afterPropertiesSet();
			scriptChangesAdapter.afterPropertiesSet();
		} catch (BSFException e) {
			Throwable targetException = e.getTargetException();
			getMainConsoleLogger().error("Exception starting script engine", targetException);
		} catch (Exception e) {
			getMainConsoleLogger().error("Exception starting script engine", e);
		}
		thePreferenceStore.addPropertyChangeListener(scriptChangesAdapter);
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(scriptChangesAdapter, 
				IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);
		
	}

	/**
	 * Accessor for the console logger singleton.  This logger writes
	 * messages into the main console displayed to the user in the application.
	 * @return the main console logger
	 */
	public Logger getMainLogger()
	{
		return mainConsoleLogger;
	}

	public Logger getMarketDataLogger() {
		return marketDataLogger;
	}
	
	public static Logger getMainConsoleLogger()
	{
		return getDefault().getMainLogger();
	}
	                                            
	/**
	 * Accessor for the FIXMessageHistory singleton.
	 * 
	 * @return the FIXMessageHistory singleton
	 */
	public FIXMessageHistory getFIXMessageHistory() {
		return fixMessageHistory;
	}

	public ScriptRegistry getScriptRegistry() {
		return scriptRegistry;
	}
	
	/** 
	 * Accessor for the OrderManager singleton.  The OrderManager is the 
	 * holder of most of the business logic for the application.
	 * @return the order manager singleton
	 */
	public PhotonController getPhotonController()
	{
		return photonController;
	}
	
	/**
	 * Accessor for the IDFactory singleton.
	 * 
	 * @return the IDFactory singleton
	 */
	public IDFactory getIDFactory() {
		return idFactory;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}


	public void ensureDefaultProject(IProgressMonitor monitor){
		monitor.beginTask("Ensure default project", 2);
		

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject newProject = root.getProject(
	                DEFAULT_PROJECT_NAME);
			IProjectDescription description = workspace.newProjectDescription(newProject.getName());

			if (!newProject.exists()) {
					newProject.create(description, new SubProgressMonitor(monitor, 1));
			}
			if (!newProject.isOpen()){
				newProject.open(monitor);
			}
	
			try {
				if (!newProject.hasNature(RUBY_NATURE_ID)){
					try {
						RubyCore.addRubyNature(newProject, new SubProgressMonitor(monitor, 1));
					} catch (Throwable t){
						// RDT possibly not included...
					}
				}
			} catch (CoreException e) {
				if (mainConsoleLogger.isDebugEnabled())
					mainConsoleLogger.debug("Exception trying to determine nature of default project.", e);
			}
		} catch (Throwable t){
			mainConsoleLogger.error("Exception creating default scripting project", t);
		}
			
		monitor.done();
	}
	
	public void changeLogLevel(String levelValue){
		if (PhotonPage.LOG_LEVEL_VALUE_ERROR.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.ERROR);
		} else if (PhotonPage.LOG_LEVEL_VALUE_WARN.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.WARN);
		} else if (PhotonPage.LOG_LEVEL_VALUE_INFO.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.INFO);
		} else if (PhotonPage.LOG_LEVEL_VALUE_DEBUG.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.DEBUG);
		}
		mainConsoleLogger.info("Changed log level to '"+levelValue+"'");
	}

	public FIXMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public FIXDataDictionary getFIXDataDictionary() {
		return FIXDataDictionaryManager.getCurrentFIXDataDictionary();
	}

	public FIXVersion getFIXVersion() {
		return fixVersion;
	}

	public static IViewPart getActiveView(String viewId) {
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.findView(viewId);
			}
		}
		return null;
	}
	
	/**
	 * @return a view of the expectedClass. null if not found or the found view
	 *         is not of the expected class.
	 */
	public static IViewPart getActiveView(String viewId, Class<?> expectedClass) {
		IViewPart viewPart = getActiveView(viewId);
		if (viewPart == null) {
			return null;
		}
		if (expectedClass.isAssignableFrom(viewPart.getClass())) {
			return viewPart;
		}
		return null;
	}
	
	public IOrderTicketController getOrderTicketController(
			String orderTicketViewID) {
		IViewPart viewPart = getActiveView(orderTicketViewID);
		IOrderTicketController controller = null;
		if (viewPart instanceof IOrderTicket) {
			IOrderTicket orderTicket = (IOrderTicket) viewPart;
			controller = orderTicket.getOrderTicketController();
		}
		return controller;
	}
	
	public IOrderTicketController getOrderTicketController(Message orderMessage) {
		String orderTicketViewID = StockOrderTicket.ID;
		if (isOptionOrder(orderMessage)) {
			orderTicketViewID = OptionOrderTicket.ID;
		}
		IOrderTicketController controller = getOrderTicketController(orderTicketViewID);
		return controller;
	}

	// todo: This needs to be replaced with an implementation that will work
	// with any option symbol.
	private boolean isOptionOrder(Message orderMessage) {
		boolean rval = false;
		try {
			StringField symbolField = orderMessage.getField(new Symbol());
			String symbol = symbolField.getValue();
			if (symbol != null && symbol.indexOf("+") >= 0) {
				rval = true;
			}
		} catch (Exception anyException) {
			// Ignore
		}
		return rval;
	}
	
	/**
	 * @return the next secondary ID for use in IWorkbenchPage.showView()
	 */
	public String getNextSecondaryID() {
		return secondaryIDCreator.getNextSecondaryID();
	}

}

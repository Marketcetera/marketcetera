package org.marketcetera.photon;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.HttpDatabaseIDFactory;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.messaging.DirectMessageListenerAdapter;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.photon.messaging.SpringUtils;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.marketcetera.photon.scripting.Classpath;
import org.marketcetera.photon.scripting.ScriptChangesAdapter;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;
import org.rubypeople.rdt.core.RubyCore;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.SessionAwareMessageListener;

import quickfix.Message;

/**
 * The main plugin class to be used in the Photon application.
 */
@ClassVersion("$Id$")
public class PhotonPlugin extends AbstractUIPlugin {

	public static final String ID = "org.marketcetera.photon";

	//The shared instance.
	private static PhotonPlugin plugin;

	private FIXMessageHistory fixMessageHistory;

	private ConnectionFactory internalConnectionFactory;

	private PooledConnectionFactory pooledInternalConnectionFactory;

	private Logger mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);

	private JmsOperations quoteJmsOperations;

	private ActiveMQTopic tradesTopic;

	private ActiveMQTopic quotesTopic;

	private ScriptRegistry scriptRegistry;

	private PhotonController photonController;

	private IDFactory idFactory;

	private BundleContext bundleContext;
	



	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger";

	public static final String DEFAULT_PROJECT_NAME = "ActiveScripts";

	private static final String RUBY_NATURE_ID = ".rubynature";

	private ScriptChangesAdapter scriptChangesAdapter;

	private SimpleMessageListenerContainer registryListener;

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
		
		Preferences preferences = new ConfigurationScope().getNode(PhotonPlugin.ID);
		String level = getPreferenceStore().getString(PhotonPage.LOG_LEVEL_KEY);
		changeLogLevel(level == null ? PhotonPage.LOG_LEVEL_VALUE_INFO : level);
		
		// This sets the internal broker to use on thread per "listener"?
		// Needed because the version of JRuby we're using doesn't play well
		// with mutliple threads
        System.setProperty("org.apache.activemq.UseDedicatedTaskRunner", "true");

        BSFManager.registerScriptingEngine(ScriptRegistry.RUBY_LANG_STRING,
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });
		initResources();
		initIDFactory();
		initInternalConnectionFactory();
		initFIXMessageHistory();
		initMessageListeners();
		initScriptRegistry();
		initPhotonController();
	}

	private void initPhotonController() {
		photonController = new PhotonController();
		photonController.setMessageHistory(fixMessageHistory);
		photonController.setMainConsoleLogger(getMainConsoleLogger());
		photonController.setIDFactory(idFactory);

	}

	private void initFIXMessageHistory() {
		fixMessageHistory = new FIXMessageHistory();
	}

	private void initInternalConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory;
		internalConnectionFactory = activeMQConnectionFactory = new ActiveMQConnectionFactory();
		pooledInternalConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
		activeMQConnectionFactory.setBrokerURL("vm://it-oms?broker.persistent=false&broker.useJmx=false");
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
		FIXDataDictionaryManager.setFIXVersion(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
		MessageBundleManager.registerCoreMessageBundle();
		MessageBundleManager.registerMessageBundle("photon", "photon_fix_messages");
	}

	private void initMessageListeners(){
		
		quotesTopic = new ActiveMQTopic("quotes");
		tradesTopic = new ActiveMQTopic("trades");


		quoteJmsOperations = SpringUtils.createJmsTemplate(pooledInternalConnectionFactory, quotesTopic);
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
		
		registryListener = plugin.newQuoteListenerForAdapter(new DirectMessageListenerAdapter() {
			@Override
			protected Object doOnMessage(Object convertedMessage) {
				try {
					scriptRegistry.onEvent((Message) convertedMessage);
				} catch (Exception e) {
					logger.error("Exception in script ", e);
				}
				return null;
			}
		});
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

	public JmsOperations getQuoteJmsOperations() {
		return quoteJmsOperations;
	}


	public SimpleMessageListenerContainer newQuoteListenerForAdapter(SessionAwareMessageListener adapter)
	{
		SimpleMessageListenerContainer container = 
			SpringUtils.createSimpleMessageListenerContainer(
					internalConnectionFactory, adapter, quotesTopic, null);
		return container;
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

}

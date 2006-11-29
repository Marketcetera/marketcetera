package org.marketcetera.photon;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.bsf.BSFManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.marketcetera.photon.quotefeed.IQuoteFeedAware;
import org.marketcetera.photon.quotefeed.IQuoteFeedConstants;
import org.marketcetera.photon.scripting.EventScriptController;
import org.marketcetera.photon.scripting.IScript;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.photon.scripting.ScriptingEventType;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.marketcetera.quotefeed.IQuoteFeedFactory;
import org.marketcetera.spring.JMSFIXMessageConverter;
import org.osgi.framework.BundleContext;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

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

	private IQuoteFeed quoteFeed;

	private Logger mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);

	private MessageListenerAdapter quotesMarketDataViewListener;

	private MessageListenerAdapter quotesScriptListener;

	private MessageListenerAdapter tradesScriptListener;

	private JmsOperations quoteJmsOperations;

	private ActiveMQTopic tradesTopic;

	private ActiveMQTopic quotesTopic;

	private ScriptRegistry scriptRegistry;

	private PhotonController photonController;

	private IDFactory idFactory;

	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger";

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
		BSFManager.registerScriptingEngine(IScript.RUBY_LANG_STRING,
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });
		initResources();
		initIDFactory();
		initInternalConnectionFactory();
		initFIXMessageHistory();
		initQuoteFeed();
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
		internalConnectionFactory = new ActiveMQConnectionFactory();
		((ActiveMQConnectionFactory)internalConnectionFactory).setBrokerURL("vm://it-oms?broker.persistent=false");
	}

	private void initScriptRegistry() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
		scriptRegistry = new ScriptRegistry();
		scriptRegistry.setInitialRegistryValueString(thePreferenceStore.getString(ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE));
		try {
			scriptRegistry.afterPropertiesSet();
		} catch (Exception e) {
		}
		thePreferenceStore.addPropertyChangeListener(scriptRegistry);
		
		
		EventScriptController tradesController = new EventScriptController();
		tradesController.setScriptList(scriptRegistry.getScriptList(ScriptingEventType.TRADE));
		tradesScriptListener.setDelegate(tradesController);
		EventScriptController quotesController = new EventScriptController();
		quotesController.setScriptList(scriptRegistry.getScriptList(ScriptingEventType.QUOTE));
		quotesScriptListener.setDelegate(quotesController);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
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

	private void initQuoteFeed() {
        try {
        	IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        	IExtensionPoint extensionPoint =
        	extensionRegistry.getExtensionPoint(IQuoteFeedConstants.EXTENSION_POINT_ID);
        	IExtension[] extensions = extensionPoint.getExtensions();
        	if (extensions != null && extensions.length > 0)
        	{
        		IConfigurationElement[] configurationElements = extensions[0].getConfigurationElements();
        		IConfigurationElement feedElement = configurationElements[0];
        		String factoryClass = feedElement.getAttribute(IQuoteFeedConstants.FEED_FACTORY_CLASS_ATTRIBUTE);
        		Class<IQuoteFeedFactory> clazz = (Class<IQuoteFeedFactory>) Class.forName(factoryClass);
        		Constructor<IQuoteFeedFactory> constructor = clazz.getConstructor( new Class[0] );
        		IQuoteFeedFactory factory = constructor.newInstance(new Object[0]);
        		IQuoteFeed targetQuoteFeed = factory.getInstance("", "", "");
        		if (targetQuoteFeed != null){
        			quoteFeed = targetQuoteFeed;
        			targetQuoteFeed.setQuoteJmsOperations(quoteJmsOperations);
        		}
    		}
    	} catch (Exception ex){
    		PhotonPlugin.getMainConsoleLogger().error("Exception starting quote feed: "+ex.getMessage());
    	}
	}

	private void initMessageListeners(){
		
		quotesTopic = new ActiveMQTopic("quotes");
		tradesTopic = new ActiveMQTopic("trades");

		ConnectionFactory internalConnectionFactory = PhotonPlugin.getDefault().getInternalConnectionFactory();

		quotesMarketDataViewListener = SpringUtils.createMessageListenerAdapter("onQuote",
				new JMSFIXMessageConverter(), internalConnectionFactory ,
				quotesTopic);
		
		quotesScriptListener = SpringUtils.createMessageListenerAdapter("onEvent",
				new JMSFIXMessageConverter(), internalConnectionFactory,
				quotesTopic);

		tradesScriptListener = SpringUtils.createMessageListenerAdapter("onEvent",
				new JMSFIXMessageConverter(), internalConnectionFactory,
				tradesTopic);
		
		quoteJmsOperations = SpringUtils.createJmsTemplate(internalConnectionFactory, quotesTopic);

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


	public ConnectionFactory getInternalConnectionFactory() {
		return internalConnectionFactory;
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

	public IQuoteFeed getQuoteFeed() {
		return quoteFeed;
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


	public void registerMarketDataView(IQuoteFeedAware view) {
		quotesMarketDataViewListener.setDelegate(view);
		view.setQuoteFeed(quoteFeed);
	}
	public void unregisterMarketDataView(IQuoteFeedAware view) {
		quotesMarketDataViewListener.setDelegate(null);
		view.setQuoteFeed(null);
	}

}

package org.marketcetera.photon;

import java.lang.reflect.Constructor;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.marketcetera.photon.quotefeed.IQuoteFeedAware;
import org.marketcetera.photon.quotefeed.IQuoteFeedConstants;
import org.marketcetera.photon.scripting.EventScriptController;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.photon.scripting.ScriptingEventType;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.marketcetera.quotefeed.IQuoteFeedFactory;
import org.marketcetera.spring.JMSFIXMessageConverter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * This class provides methods for some basic application services,
 * as well as singleton member variables for Photon components, along
 * with static getters.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class Application implements IPlatformRunnable, IPropertyChangeListener {

	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger";

	private static Logger mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);
    private static IDFactory idFactory;
	private static PhotonController photonController;
	

	private static FIXMessageHistory fixMessageHistory;
	private static IQuoteFeed quoteFeed;
	private static ScriptRegistry scriptRegistry;
	private static ScopedPreferenceStore preferenceStore;

	public static final String PLUGIN_ID = "org.marketcetera.photon";
	
	public static final String JMS_OUT_ENDPOINT_URI = "jms.out";
	public static final String JMS_IN_ENDPOINT_URI = "jms.in";
	public static final String QUOTE_FEED_ENDPOINT_URI = "quotefeed.in";

	private static ClassPathXmlApplicationContext mainApplicationContext;

	private ClassPathXmlApplicationContext jmsApplicationContext;

	private ClassPathXmlApplicationContext outgoingJmsTemplate;

	private ClassPathXmlApplicationContext quoteFeedApplicationContext;

	private ActiveMQConnectionFactory internalConnectionFactory;

	private ActiveMQTopic quotesTopic;

	private static MessageListenerAdapter quotesMarketDataViewListener;


	private JmsTemplate quoteJmsTemplate;

	private ActiveMQTopic tradesTopic;

	private MessageListenerAdapter quotesScriptListener;

	private MessageListenerAdapter tradesScriptListener;

	
	
	/**
	 * This method is called by the Eclipse RCP, and therefore is the main 
	 * entry point to the Application.  This method initializes the FIX version
	 * for the application (to FIX 4.2), the FIXMessageHistory, the JMSConnector
	 * the OrderManager.  Finally it creates the display, and creates and runs
	 * the workbench.
	 * 
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 * @see PlatformUI#createDisplay()
	 * @see PlatformUI#createAndRunWorkbench(Display, org.eclipse.ui.application.WorkbenchAdvisor)
	 */
	public Object run(Object args) throws Exception {
		initPreferenceStore();
		preferenceStore.addPropertyChangeListener(this);

		initResources();
		
		fixMessageHistory = new FIXMessageHistory();

//		mainApplicationContext = new ClassPathXmlApplicationContext("photon-spring.xml");
//		photonController = (PhotonController) mainApplicationContext.getBean("photonController");
		
		initInternalBroker();
		initQuoteFeed();
		initScriptRegistry();
		
		startQuoteFeed();
		
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}

	}

	private void initScriptRegistry() {
		scriptRegistry = new ScriptRegistry();
		scriptRegistry.setInitialRegistryValueString(getPreferenceStore().getString(ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE));
		try {
			scriptRegistry.afterPropertiesSet();
		} catch (Exception e) {
		}
		getPreferenceStore().addPropertyChangeListener(scriptRegistry);
		
		
		EventScriptController tradesController = new EventScriptController();
		tradesController.setScriptList(scriptRegistry.getScriptList(ScriptingEventType.TRADE));
		tradesScriptListener.setDelegate(tradesController);
		EventScriptController quotesController = new EventScriptController();
		quotesController.setScriptList(scriptRegistry.getScriptList(ScriptingEventType.QUOTE));
		quotesScriptListener.setDelegate(quotesController);
	}

	private void startJMS() {
		if (jmsApplicationContext != null){
			jmsApplicationContext.stop();
		}
		try {
			jmsApplicationContext = new ClassPathXmlApplicationContext(new String[]{"jms.xml"}, mainApplicationContext);
		} catch (Exception ex){
			getMainConsoleLogger().error("Exception connecting to message queue", ex);
		}
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
        			targetQuoteFeed.setQuoteJmsTemplate(quoteJmsTemplate);
        		}
    		}
    	} catch (Exception ex){
    		getMainConsoleLogger().error("Exception starting quote feed: "+ex.getMessage());
    	}
	}

	private void startQuoteFeed() {
    	quoteFeed.start();
	}
	
	private void initInternalBroker(){

		internalConnectionFactory = new ActiveMQConnectionFactory();
		internalConnectionFactory.setBrokerURL("vm://it-oms?broker.persistent=false");
		
		quotesTopic = new ActiveMQTopic("quotes");
		tradesTopic = new ActiveMQTopic("trades");

		quotesMarketDataViewListener = createMessageListenerAdapter("onQuote",
				new JMSFIXMessageConverter(), internalConnectionFactory,
				quotesTopic);
		
		quotesScriptListener = createMessageListenerAdapter("onEvent",
				new JMSFIXMessageConverter(), internalConnectionFactory,
				quotesTopic);

		tradesScriptListener = createMessageListenerAdapter("onEvent",
				new JMSFIXMessageConverter(), internalConnectionFactory,
				tradesTopic);
		
		quoteJmsTemplate = new JmsTemplate();
		quoteJmsTemplate.setConnectionFactory(internalConnectionFactory);
		quoteJmsTemplate.setDefaultDestination(quotesTopic);
		quoteJmsTemplate.afterPropertiesSet();


	}

	private MessageListenerAdapter createMessageListenerAdapter(
			String methodName, MessageConverter converter,
			ConnectionFactory connectionFactory, Destination destination) {
		MessageListenerAdapter listener;
		listener = new MessageListenerAdapter();
		listener.setDefaultListenerMethod(methodName);
		listener.setMessageConverter(converter);

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setMessageListener(listener);
		container.setDestination(destination);
		container.afterPropertiesSet();
		container.start();
		return listener;
	}
	
	public static void initPreferenceStore() {
		preferenceStore = new ScopedPreferenceStore(new ConfigurationScope(),
				PLUGIN_ID);
	}

	public static void initResources() throws FIXFieldConverterNotAvailable
	{
		FIXDataDictionaryManager.setFIXVersion(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
		MessageBundleManager.registerCoreMessageBundle();
		MessageBundleManager.registerMessageBundle("photon", "photon_fix_messages");
	}
	

	/**
	 * Accessor for the console logger singleton.  This logger writes
	 * messages into the main console displayed to the user in the application.
	 * @return the main console logger
	 */
	public static Logger getMainConsoleLogger()
	{
		return mainConsoleLogger;
	}
	
	
	/** 
	 * Accessor for the OrderManager singleton.  The OrderManager is the 
	 * holder of most of the business logic for the application.
	 * @return the order manager singleton
	 */
	public static PhotonController getOrderManager()
	{
		return photonController;
	}
	

	/**
	 * Accessor for the FIXMessageHistory singleton.
	 * 
	 * @return the FIXMessageHistory singleton
	 */
	public static FIXMessageHistory getFIXMessageHistory() {
		return fixMessageHistory;
	}

	/**
	 * Accessor for the IDFactory singleton.
	 * 
	 * @return the IDFactory singleton
	 */
	public static IDFactory getIDFactory() {
		return idFactory;
	}
	
	public static ScriptRegistry getScriptRegistry() {
		return scriptRegistry;
	}
	
	/**
	 * This method is not part of the API and only intended for testing, i.e., supplying a mock
	 * script registry.
	 */
	public static void setScriptRegistry(ScriptRegistry registry) {
		scriptRegistry = registry;
	}
	
	private void changeLogLevel(String levelValue){
		Logger logger = getMainConsoleLogger();
		if (PhotonPage.LOG_LEVEL_VALUE_ERROR.equals(levelValue)){
			logger.setLevel(Level.ERROR);
		} else if (PhotonPage.LOG_LEVEL_VALUE_WARN.equals(levelValue)){
			logger.setLevel(Level.WARN);
		} else if (PhotonPage.LOG_LEVEL_VALUE_INFO.equals(levelValue)){
			logger.setLevel(Level.INFO);
		} else if (PhotonPage.LOG_LEVEL_VALUE_DEBUG.equals(levelValue)){
			logger.setLevel(Level.DEBUG);
		}
		logger.info("Changed log level to '"+levelValue+"'");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PhotonPage.LOG_LEVEL_KEY)){
			changeLogLevel(""+event.getNewValue());
		}
	}

	public static ScopedPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}


	public static void registerMarketDataView(IQuoteFeedAware view) {
		quotesMarketDataViewListener.setDelegate(view);
		view.setQuoteFeed(quoteFeed);
	}
	public static void unregisterMarketDataView(IQuoteFeedAware view) {
		quotesMarketDataViewListener.setDelegate(null);
		view.setQuoteFeed(null);
	}
}

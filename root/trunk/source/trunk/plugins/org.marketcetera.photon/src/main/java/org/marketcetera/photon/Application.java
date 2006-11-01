package org.marketcetera.photon;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.URL;

import javax.jms.JMSException;

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
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.core.FeedComponent.FeedStatus;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.HttpDatabaseIDFactory;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.photon.quotefeed.IQuoteFeedConstants;
import org.marketcetera.photon.scripting.IScriptRegistry;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.photon.scripting.ScriptingAwareQuoteFeed;
import org.marketcetera.quickfix.ConnectionConstants;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.marketcetera.quotefeed.IQuoteFeedFactory;

import quickfix.Message;

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

	
	private static final String CONTEXT_FACTORY_NAME_DEFAULT = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	private static final String CONNECTION_FACTORY_NAME_DEFAULT = "ConnectionFactory";
	private static final String INCOMING_TOPIC_NAME_DEFAULT = "oms-messages";
	private static final String OUTGOING_QUEUE_NAME_DEFAULT = "oms-commands";
	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger";

	
	private static Logger mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);
    private static IDFactory idFactory;
	private static OrderManager orderManager;
	private static JMSConnector jmsConnector;
	

	private static FIXMessageHistory fixMessageHistory;
	private static IQuoteFeed quoteFeed;
	private static IScriptRegistry scriptRegistry = new ScriptRegistry();
	private static ScopedPreferenceStore preferenceStore;

	public static final String PLUGIN_ID = "org.marketcetera.photon";
	
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

		jmsConnector = new JMSConnector();

		orderManager = new OrderManager(fixMessageHistory);
		
		setUpQuoteFeed();
		
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
	
	private void setUpQuoteFeed() {
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
				IQuoteFeed targetQuoteFeed = factory.getInstance("datasvr.tradearca.com:8092", "", "");
				if (targetQuoteFeed != null)
					quoteFeed = new ScriptingAwareQuoteFeed(targetQuoteFeed);
			}
		} catch (Exception ex){
			getMainConsoleLogger().error("Exception starting quote feed: "+ex.getMessage());
		}
	}

	
	/**
	 * Initializes (or re-initializes) the connection to the JMS server,
	 * by opening a connection to the URL specified in the JMS preferences.
	 * The connection is established in its own thread to avoid tying up the UI
	 * thread of the application.  The JMSConnector that is returned, therefore
	 * may not be fully initialized.
	 * 
	 * @see JMSPreferencePage
	 * @return the newly initialized JMSConnector
	 */
	public static void initJMSConnector()
	{

		ScopedPreferenceStore preferences = new ScopedPreferenceStore(new ConfigurationScope(), Application.PLUGIN_ID);
        EclipseConfigData config = new EclipseConfigData(preferences);
		final String incomingTopicNameString = config.get(ConnectionConstants.JMS_INCOMING_TOPIC_KEY, INCOMING_TOPIC_NAME_DEFAULT);
		final String outgoingQueueNameString = config.get(ConnectionConstants.JMS_OUTGOING_QUEUE_KEY, OUTGOING_QUEUE_NAME_DEFAULT);
		final String contextFactoryString = config.get(ConnectionConstants.JMS_CONTEXT_FACTORY_KEY, CONTEXT_FACTORY_NAME_DEFAULT);
		final String jmsURLString = config.get(ConnectionConstants.JMS_URL_KEY, "");
		final String jmsConnectionFactoryString = config.get(ConnectionConstants.JMS_CONNECTION_FACTORY_KEY, CONNECTION_FACTORY_NAME_DEFAULT);
			Thread jmsConnectThread = new Thread(){
				public void run() {
					try {
						jmsConnector.shutdown();
			        	jmsConnector.init(
							incomingTopicNameString,
							outgoingQueueNameString,
							contextFactoryString,
							jmsURLString,
							jmsConnectionFactoryString
							);
					} catch (JMSException e) {
						getMainConsoleLogger().error("Could not connect to JMS server {"
								+ incomingTopicNameString +", "
								+ outgoingQueueNameString +", "
								+ contextFactoryString +", "
								+ jmsURLString +", "
								+ jmsConnectionFactoryString +"}"
								, e);
					}
		            try {
						if (getJMSStatus().equals(FeedStatus.AVAILABLE)){
								jmsConnector.setTopicListener(orderManager.getMessageListener());
						}
					} catch (JMSException e) {
						getMainConsoleLogger().error("Could not set up JMS connection.", e);
					}
				}
        	};
        	jmsConnectThread.start();
        	
	}

	public static void initQuoteFeed() throws IOException
	{
		quoteFeed.connect();
	}
	
	public static void initIDFactory() throws IOException
	{
		URL url = new URL(
				"http",
				getPreferenceStore().getString(ConnectionConstants.WEB_APP_HOST_KEY),
				getPreferenceStore().getInt(ConnectionConstants.WEB_APP_PORT_KEY),
				"/id_repository/get_next_batch"
		);
		HttpDatabaseIDFactory tempIDFactory = new HttpDatabaseIDFactory(url);
		try {
			tempIDFactory.grabIDs();
			idFactory = tempIDFactory;
		} catch (Throwable t) {
			getMainConsoleLogger().warn("Error connecting to web app for ID base, reverting to built in IDFactory.");
			idFactory = new InMemoryIDFactory(System.currentTimeMillis(),"-"+InetAddress.getLocalHost().toString());
		}
		getOrderManager().setIDFactory(idFactory);
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
	public static OrderManager getOrderManager()
	{
		return orderManager;
	}
	

	/**
	 * Accessor for status information for the JMSConnector singleton.
	 * 
	 * @return the FeedStatus corresponding with the current status of
	 *         the jms connection
	 */
	public static FeedStatus getJMSStatus() {
		return jmsConnector.getFeedStatus();
	}


	/**
	 * Sends a message to the outgoing queue, destined for the OMS.
	 * First checks to see if the feed status on the jmsConnector is 
	 * FeedStatus.AVAILABLE, then proceeds to send the message.  If 
	 * there is a previously unknown problem with the JMS connection,
	 * a JMSException will be thrown.
	 * 
	 * @see JMSConnector#sendToQueue(Message)
	 * 
	 * @param message
	 * @return <code>true</code> if the message was successfully sent to the queue; <code>false</code> otherwise.
	 * @throws JMSException
	 */
	public static boolean sendToQueue(Message message) throws JMSException {
		if (jmsConnector.getFeedStatus() == FeedStatus.AVAILABLE){
			jmsConnector.sendToQueue(message);
			return true;
		} else {
			Application.getMainConsoleLogger().error("Could not send message to queue ");
			return false;
		}
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
	 * Accessor for the JMSConnector singleton.
	 * 
	 * @return the JMSConnector singleton
	 */
	public static JMSConnector getJMSConnector() {
		return jmsConnector;
	}

	/**
	 * Accessor for the IDFactory singleton.
	 * 
	 * @return the IDFactory singleton
	 */
	public static IDFactory getIDFactory() {
		return idFactory;
	}
	
	public static IQuoteFeed getQuoteFeed() {
		return quoteFeed;
	}

	public static IScriptRegistry getScriptRegistry() {
		return scriptRegistry;
	}
	
	/**
	 * This method is not part of the API and only intended for testing, i.e., supplying a mock
	 * script registry.
	 */
	public static void setScriptRegistry(IScriptRegistry registry) {
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
}

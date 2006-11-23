package org.marketcetera.photon;

import org.apache.bsf.BSFManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPlatformRunnable;
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
import org.marketcetera.photon.scripting.IScript;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
	private static ScriptRegistry scriptRegistry = new ScriptRegistry();
	private static ScopedPreferenceStore preferenceStore;

	public static final String PLUGIN_ID = "org.marketcetera.photon";
	
	public static final String JMS_OUT_ENDPOINT_URI = "jms.out";
	public static final String JMS_IN_ENDPOINT_URI = "jms.in";
	public static final String QUOTE_FEED_ENDPOINT_URI = "quotefeed.in";

	private ClassPathXmlApplicationContext mainApplicationContext;

	private ClassPathXmlApplicationContext jmsApplicationContext;

	private ClassPathXmlApplicationContext outgoingJmsTemplate;

	private ClassPathXmlApplicationContext quoteFeedApplicationContext;
	
	
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

		BSFManager.registerScriptingEngine(IScript.RUBY_LANG_STRING,
				"org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });

		initResources();
		
		fixMessageHistory = new FIXMessageHistory();

		mainApplicationContext = new ClassPathXmlApplicationContext("photon-spring.xml");
		photonController = (PhotonController) mainApplicationContext.getBean("photonController");
		
		startJMS();
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

	private void startQuoteFeed() {
		if (quoteFeedApplicationContext != null){
			quoteFeedApplicationContext.stop();
		}
		quoteFeedApplicationContext = new ClassPathXmlApplicationContext(new String[]{"quotefeed.xml"}, mainApplicationContext);
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
	
	public static IQuoteFeed getQuoteFeed() {
		return quoteFeed;
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
}

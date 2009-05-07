package org.marketcetera.photon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.logging.LogManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.photon.views.OptionOrderTicketController;
import org.marketcetera.photon.views.OptionOrderTicketModel;
import org.marketcetera.photon.views.OrderTicketModel;
import org.marketcetera.photon.views.SecondaryIDCreator;
import org.marketcetera.photon.views.StockOrderTicketController;
import org.marketcetera.photon.views.StockOrderTicketModel;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.Strategy;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.rubypeople.rdt.core.RubyCore;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.SecurityType;

/* $License$ */

/**
 * The main plugin class to be used in the Photon application. This class is not
 * synchronized and should only be accessed from the UI thread.
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class PhotonPlugin 
    extends AbstractUIPlugin
    implements Messages, IPropertyChangeListener
{

	public static final String ID = "org.marketcetera.photon"; //$NON-NLS-1$

	//The shared instance.
	private static PhotonPlugin plugin;

	private TradeReportsHistory mTradeReportsHistory;

	
	/**
	 * Cannot be initialized until after logging infrastructure is set up
	 */
	private Logger mainConsoleLogger;

	private PhotonController photonController;

	private BundleContext bundleContext;
	
	public static final String MAIN_CONSOLE_LOGGER_NAME = "main.console.logger"; //$NON-NLS-1$
	
	public static final String STRATEGY_LOGGER_NAME = org.marketcetera.core.Messages.USER_MSG_CATEGORY;

    public static final String DEFAULT_PROJECT_NAME = "ActiveScripts"; //$NON-NLS-1$

	private static final String RUBY_NATURE_ID = ".rubynature"; //$NON-NLS-1$

	private FIXMessageFactory messageFactory;

	private FIXVersion fixVersion;

	private SecondaryIDCreator secondaryIDCreator = new SecondaryIDCreator();

	private OrderTicketModel stockOrderTicketModel;

	private OptionOrderTicketModel optionOrderTicketModel;

	private StockOrderTicketController stockOrderTicketController;

	private OptionOrderTicketController optionOrderTicketController;
	
	private BrokerManager mBrokerManager;

	private PositionEngine mPositionEngine;
	
	private SessionStartTimeProvider mSessionStartTimeProvider = new SessionStartTimeProvider();

    private ServiceRegistration mPositionEngineService;

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
		
		configureLogs();
		
		mainConsoleLogger = Logger.getLogger(MAIN_CONSOLE_LOGGER_NAME);
		
		new DefaultScope().getNode("org.rubypeople.rdt.launching").putBoolean("org.rubypeople.rdt.launching.us.included.jruby", true); //$NON-NLS-1$ //$NON-NLS-2$

		String level = getPreferenceStore().getString(PhotonPreferences.CONSOLE_LOG_LEVEL);
		changeLogLevel(level == null ? PhotonPage.LOG_LEVEL_VALUE_INFO : level);
		
		// This sets the internal broker to use on thread per "listener"?
		// Needed because the version of JRuby we're using doesn't play well
		// with mutliple threads
		// TODO: is this still needed??
		System.setProperty("org.apache.activemq.UseDedicatedTaskRunner", "true"); //$NON-NLS-1$ //$NON-NLS-2$

		initMessageFactory();
		initTradeReportsHistory();
		
		initPhotonController();
		PhotonPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	public void initOrderTickets(){
		stockOrderTicketModel = new StockOrderTicketModel(messageFactory);
		optionOrderTicketModel = new OptionOrderTicketModel(messageFactory);
		stockOrderTicketController = new StockOrderTicketController(stockOrderTicketModel);
		optionOrderTicketController = new OptionOrderTicketController(optionOrderTicketModel);
	}
	
	private void initPhotonController() {
		photonController = new PhotonController();
		photonController.setMessageHistory(mTradeReportsHistory);
		photonController.setMainConsoleLogger(getMainConsoleLogger());
	}

	private void initTradeReportsHistory() {
		mTradeReportsHistory = new TradeReportsHistory(messageFactory);
	}



	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		mPositionEngine = null;
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

	private void initMessageFactory() throws FIXFieldConverterNotAvailable {
		fixVersion = FIXVersion.FIX_SYSTEM;
		messageFactory = fixVersion.getMessageFactory();
		CurrentFIXDataDictionary.setCurrentFIXDataDictionary(
				FIXDataDictionaryManager.initialize(fixVersion, 
						fixVersion.getDataDictionaryURL()));
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
	 * Accessor for the TradeReportsHistory singleton.
	 * 
	 * @return the TradeReportsHistory singleton
	 */
	public TradeReportsHistory getTradeReportsHistory() {
		return mTradeReportsHistory;
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
	
	public BundleContext getBundleContext() {
		return bundleContext;
	}


	public void ensureDefaultProject(IProgressMonitor monitor){
		monitor.beginTask("Ensure default project", 2); //$NON-NLS-1$
		

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject newProject = root.getProject(
	                DEFAULT_PROJECT_NAME);
			IProjectDescription description = workspace.newProjectDescription(newProject.getName());

			if (!newProject.exists()) {
					newProject.create(description, new SubProgressMonitor(monitor, 1));
			}
	        // this is the full path of the "ActiveScripts" directory - pass this in by default to Ruby scripts in order to pick up
	        //  any other scripts in the same dir for "requires" directives
	        String determineClasspath = ResourcesPlugin.getWorkspace().getRoot().getProject(DEFAULT_PROJECT_NAME).getLocation().toString();
	        System.setProperty(Strategy.CLASSPATH_PROPERTYNAME,
	                           determineClasspath);
			if (!newProject.isOpen()){
				newProject.open(monitor);
			}
	
			try {
				if (!newProject.hasNature(RUBY_NATURE_ID)){
					try {
						RubyCore.addRubyNature(newProject, new SubProgressMonitor(monitor, 1));
					} catch (Throwable t){
						// RDT possibly not included...
					    mainConsoleLogger.error(CANNOT_LOAD_RUBY.getText(),
					                            t);
					}
				}
			} catch (CoreException e) {
				if (mainConsoleLogger.isDebugEnabled())
					mainConsoleLogger.debug("Exception trying to determine nature of default project.", //$NON-NLS-1$
					                        e);
			}
		} catch (Throwable t){
			mainConsoleLogger.error(CANNOT_START_DEFAULT_SCRIPT_PROJECT.getText(),
			                        t);
		}
			
		monitor.done();
	}
	
	public void changeLogLevel(String levelValue){
		Logger strategyLogger = Logger.getLogger(STRATEGY_LOGGER_NAME);
		if (PhotonPage.LOG_LEVEL_VALUE_ERROR.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.ERROR);
			strategyLogger.setLevel(Level.ERROR);
		} else if (PhotonPage.LOG_LEVEL_VALUE_WARN.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.WARN);
			strategyLogger.setLevel(Level.WARN);
		} else if (PhotonPage.LOG_LEVEL_VALUE_INFO.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.INFO);
			strategyLogger.setLevel(Level.INFO);
		} else if (PhotonPage.LOG_LEVEL_VALUE_DEBUG.equals(levelValue)){
			mainConsoleLogger.setLevel(Level.DEBUG);
			strategyLogger.setLevel(Level.DEBUG);
		}
		mainConsoleLogger.info(LOGGER_LEVEL_CHANGED.getText(levelValue));
	}

	public FIXMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public FIXDataDictionary getFIXDataDictionary() {
		return CurrentFIXDataDictionary.getCurrentFIXDataDictionary();
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
	
	public OrderTicketModel getStockOrderTicketModel(){
		return stockOrderTicketModel;
	}
	
	public OptionOrderTicketModel getOptionOrderTicketModel(){
		return optionOrderTicketModel;
	}
	
	public StockOrderTicketController getStockOrderTicketController() {
		return stockOrderTicketController;
	}
	
	public OptionOrderTicketController getOptionOrderTicketController() {
		return optionOrderTicketController;
	}
	
	/**
	 * Returns the order ticket appropriate for the given message (based
	 * on security type).
	 * @param orderMessage the message specifying the type of order ticket.
	 * @return the controller for the appropriate order ticket.
	 */
	public IOrderTicketController getOrderTicketController(Message orderMessage) {
		try {
			// This works for orders and execution reports
			if (FIXMessageUtil.isEquityOptionOrder(orderMessage)
					|| (FIXMessageUtil.isExecutionReport(orderMessage) && SecurityType.OPTION
							.equals(orderMessage.getString(SecurityType.FIELD)))) {
				return getOptionOrderTicketController();
			}
		} catch (FieldNotFound e) {
			// not an option
		}
		return getStockOrderTicketController();
	}

	
	/**
	 * @return the next secondary ID for use in IWorkbenchPage.showView()
	 */
	public String getNextSecondaryID() {
		return secondaryIDCreator.getNextSecondaryID();
	}

	public MarketDataManager getMarketDataManager() {
		return MarketDataManager.getCurrent();
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		PhotonImages.initializeSharedImages(reg);
	}

	/**
	 * The system property that is set to a unique number for every photon
	 * process. An attempt is made to use the pid value as the value of this
	 * property. However, if that doesn't work, the system time at the time
	 * this property is set, is set as the value of this property. 
	 */
	private static final String PROCESS_UNIQUE_PROPERTY = "org.marketcetera.photon.unique"; //$NON-NLS-1$
	/**
	 * log4j configuration file name.
	 */
	private static final String LOG4J_CONFIG = "photon-log4j.properties"; //$NON-NLS-1$
	/**
	 * java logging configuration file name.
	 */
	private static final String JAVA_LOGGING_CONFIG = "java.util.logging.properties"; //$NON-NLS-1$

    /**
     * The system property name that contains photon installation
     * directory
     */
	private static final String APP_DIR_PROP="org.marketcetera.appDir"; //$NON-NLS-1$
	
	/**
	 * The configuration sub directory for the application
	 */
	private static final String CONF_DIR = "conf"; //$NON-NLS-1$

	/**
	 * Delay for rereading log4j configuration.
	 */
	private static final int LOGGER_WATCH_DELAY = 20*1000;


	/**
	 * Configure Logs
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void configureLogs() throws FileNotFoundException, IOException {
		// Fetch the java process ID. Do note that this mechanism relies on
		// a non-public interface of the jvm but its very useful to be able
		// to use the pid.
		String id = ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^0-9]", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if(id == null || id.trim().length() < 1) {
			id = String.valueOf(System.currentTimeMillis());  
		}
		// Supply the pid as a system property so that it can be used in
		// log 4j configuration
		System.setProperty(PROCESS_UNIQUE_PROPERTY,id);
		//Figure out if the application install dir is specified
        String appDir=System.getProperty(APP_DIR_PROP);
        File confDir = null;
		// Configure loggers
        if(appDir != null) {
        	File dir = new File(appDir,CONF_DIR);
        	if(dir.isDirectory()) {
        		confDir = dir;
        	}
        }
        // Configure Java Logging
        boolean logConfigured = false;
        if (confDir != null) {
            File logConfig = new File(confDir,JAVA_LOGGING_CONFIG);
			if (logConfig.isFile()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(logConfig);
					LogManager.getLogManager().readConfiguration(fis);
					logConfigured = true;
				} catch (Exception ignored) {
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException ignored) {
						}
					}
				}

			}
		}
		//Do default configuration, if its not already done.
        if(!logConfigured) {
    		LogManager.getLogManager().readConfiguration(getClass().
    				getClassLoader().getResourceAsStream(
    						JAVA_LOGGING_CONFIG));
        }
        
        // Configure Log4j
		// Remove default configuration done via log4j.properties file
		// present in one of the jars that we depend on 
		BasicConfigurator.resetConfiguration();
		logConfigured = false;
		if(confDir != null) {
	        File logConfig = new File(confDir,LOG4J_CONFIG);
	        if(logConfig.isFile()) {
	        	PropertyConfigurator.configureAndWatch(
	        			logConfig.getAbsolutePath(),LOGGER_WATCH_DELAY);
	        	logConfigured = true;
	        } 			
		}
        if(!logConfigured) {
    		//Do default log4j configuration, if its not already done.
    		PropertyConfigurator.configure(getClass().
    				getClassLoader().getResource(LOG4J_CONFIG));
        }
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PhotonPreferences.CONSOLE_LOG_LEVEL)){
			PhotonPlugin.getDefault().changeLogLevel(""+event.getNewValue()); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the {@link BrokerManager} singleton for this plug-in.
	 * Typically, this should be accessed through
	 * {@link BrokerManager#getCurrent()}.
	 * 
	 * @return the BrokerManager singleton for this plug-in
	 */
	public BrokerManager getBrokerManager() {
		if (mBrokerManager == null) {
			mBrokerManager = new BrokerManager();
		}
		return mBrokerManager;
	}
	
	/**
	 * Starts a background job to reconnect to the market data feed.
	 */
	public void reconnectMarketDataFeed() {
		new Job(Messages.CONNECTING_TO_MARKET_DATA_JOB_NAME.getText()) {
		
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				MarketDataManager marketDataManager = PhotonPlugin.getDefault().getMarketDataManager();
				marketDataManager.reconnectFeed();
				return Status.OK_STATUS;
			}
		}.schedule();
	}
	
	/**
	 * Returns an object that provides the currently effective session start time. Unlike the
	 * PhotonPreferences.TRADING_HISTORY_START_TIME preference value which is just a time of day,
	 * this value is the exact date object that was used during the last connection to the server.
	 * 
	 * @return the session start time provider
	 */
	public ISessionStartTimeProvider getSessionStartTimeProvider() {
		return mSessionStartTimeProvider;
	}
	
	/**
	 * Sets the session start time.
	 * 
	 * @see PhotonPlugin#getSessionStartTimeProvider()
	 * 
	 * @param newSessionStartTime the new session start time
	 */
	public void setSessionStartTime(Date newSessionStartTime) {
		mSessionStartTimeProvider.setSessionStartTime(newSessionStartTime);
	}
	
	/**
	 * Registers the position engine as an OSGi service.
	 * 
	 * @param engine position engine to register
	 */
	public synchronized void registerPositionEngine(PositionEngine engine) {
		mPositionEngine = engine;
		mPositionEngineService = getBundleContext().registerService(PositionEngine.class.getName(), engine, null);
	}
	
	/**
	 * Disposes the registered position engine server if one exists.
	 */
	public synchronized void disposePositionEngine() {
		if (mPositionEngine != null) {
			mPositionEngine.dispose();
			mPositionEngine = null;
		}
		if (mPositionEngineService != null) {
			mPositionEngineService.unregister();
			mPositionEngineService = null;
		}
	}
}

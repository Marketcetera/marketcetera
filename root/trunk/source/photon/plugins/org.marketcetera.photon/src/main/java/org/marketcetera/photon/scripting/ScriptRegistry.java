package org.marketcetera.photon.scripting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jruby.bsf.JRubyPlugin;
import org.jruby.exceptions.RaiseException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.marketdata.MarketDataFeed;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.scripting.ScriptLoggingUtil;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;

import quickfix.Message;

/* $License$ */

/**
 * Script registry implementation.
 * 
 * @author andrei@lissovski.org
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public class ScriptRegistry
    implements InitializingBean, Messages
{

	public static final String RUBY_LANG_STRING = "ruby"; //$NON-NLS-1$
	protected BSFManager bsfManager;
	protected BSFEngine engine;
	private Classpath additionalClasspath =  new Classpath();
	private Classpath currentClasspath;
	protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private Logger logger;
	private Map<String, Strategy> registeredStrategies;
	
	static String [] JRUBY_PLUGIN_PATH = {
		"lib/ruby/site_ruby/1.8", //$NON-NLS-1$
		"lib/ruby/site_ruby/1.8/java", //$NON-NLS-1$
		"lib/ruby/site_ruby",  //$NON-NLS-1$
		"lib/ruby/1.8",  //$NON-NLS-1$
		"lib/ruby/1.8/java", //$NON-NLS-1$
		"lib/active_support", //$NON-NLS-1$
		"lib/active_support/active_support", //$NON-NLS-1$
		"lib/active_support/active_support/vendor" //$NON-NLS-1$
	};
	
	static String [] JRUBY_WORKSPACE_PATH = {
		"" //$NON-NLS-1$
	};
	private MarketDataFeedTracker marketDataFeedTracker;
	
	public ScriptRegistry() 
	{
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		marketDataFeedTracker = new MarketDataFeedTracker(bundleContext);
		marketDataFeedTracker.open();
		logger = PhotonPlugin.getMainConsoleLogger();
		registeredStrategies = new HashMap<String, Strategy>();
	}
	/**
	 * this subscriber is used to register with each market data feed to receive every market data tick
	 * to pass to the scripts
	 */
	private final ISubscriber mFeedSubscriber = new ISubscriber() {
        @Override
        public boolean isInteresting(Object inData)
        {
            if(inData instanceof HasFIXMessage &&
               ((HasFIXMessage)inData).getMessage() != null) {
                return true;
            }
            PhotonPlugin.getMainConsoleLogger().warn(REGISTRY_DISCARDED_MESSAGE.getText(inData));
            return false;
        }
        @Override
        public void publishTo(Object inData)
        {
            if(inData instanceof HasFIXMessage) {
                onMarketDataEvent(((HasFIXMessage)inData).getMessage());
            }
            // TODO also notify scripts for BBO and depth-of-book?
            // TODO also notify scripts for ExecutionReports?
            // TODO also notify scripts on any FIX message? - onFIXEvent
        }           
	};
	/**
     * Connects the script registry to the given market data feed.
     * 
     * <p>Indicates to this <code>ScriptRegistry</code> that the given
     * <code>IMarketDataFeed</code> is capable of receiving data.  The
     * same feed may be passed multiple times to this method with no
     * ill effects.
     * 
     * @param inFeed a <code>IMarketDataFeed</code> value
	 */	
    public void connectToMarketDataFeed(MarketDataFeed<?,?> inFeed)
	{
        logger.debug(String.format("Registering feed %s with the Script Registry", //$NON-NLS-1$
                                   inFeed));
	    inFeed.subscribeToAll(mFeedSubscriber);
	}
	/**
	 * Disconnects the script registry from the given market data feed.
	 *
	 * <p>Executing this method will cause the given data feed to stop delivering
	 * messages to this <code>ScriptRegistry</code>.  Calling this method with
	 * a feed to which this <code>ScriptRegistry</code> is not already connected
	 * with have no effect.
	 * 
     * @param inFeed a <code>IMarketDataFeed</code> value
	 */
	public void disconnectFromMarketDataFeed(MarketDataFeed<?,?> inFeed)
	{
        logger.debug(String.format("Unregistering feed %s with the Script Registry", //$NON-NLS-1$
                                   inFeed));
        inFeed.unsubscribeFromAll(mFeedSubscriber);
	}
	
	private void initBSFManager() throws BSFException {
		currentClasspath = new Classpath();
		currentClasspath.add(EclipseUtils.getPluginPath(PhotonPlugin.getDefault()).append("src").append("main").append("resources")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		currentClasspath.add(ResourcesPlugin.getWorkspace().getRoot().getProject(PhotonPlugin.DEFAULT_PROJECT_NAME).getLocation());

		
		currentClasspath.addAll(additionalClasspath);
		IPath jrubyPluginPath = EclipseUtils.getPluginPath(JRubyPlugin.getDefault());
		PhotonPlugin.getMainConsoleLogger().debug("Using base path of JRuby plugin: "+jrubyPluginPath.toString()); //$NON-NLS-1$
		updateClasspath(currentClasspath, jrubyPluginPath, JRUBY_PLUGIN_PATH);
		updateClasspath(currentClasspath, EclipseUtils.getWorkspacePath(), JRUBY_WORKSPACE_PATH);
		String classpathString = currentClasspath.toString();
		bsfManager = new BSFManager();
		bsfManager.setClassPath(classpathString);

		engine = bsfManager.loadScriptingEngine("ruby"); //$NON-NLS-1$

		engine.eval("<java>", 1, 1, "require 'active_support/core_ext'"); //$NON-NLS-1$ //$NON-NLS-2$
		engine.eval("<java>", 1, 1, "require 'active_support/dependencies'"); //$NON-NLS-1$ //$NON-NLS-2$
		engine.eval("<java>", 1, 1, "require 'photon'"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	

	public void setAdditionalClasspath(List<IPath> pluginPath) {
		additionalClasspath.addAll(pluginPath);
	}


	private void updateClasspath(List<IPath> pathElements, IPath pluginPath, String[] pathStrings) {
		for (String aPath : pathStrings) {
			pathElements.add(pluginPath.append(aPath));
		}
	}


	public void afterPropertiesSet() throws Exception {
		initBSFManager();

	}


	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param requireString the argument to the Ruby require method
	 * @return true if the script is already registered
	 * @throws BSFException
	 */
	public boolean isRegistered(final String requireString) {
		Future<Boolean> future = scheduler.submit(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return doIsRegistered(requireString);
			}		
		});
		try {
			return future.get();
		} catch (Exception e) {
			logger.debug("Error getting a value out of a future.get", e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param fileName the argument to the Ruby require method
	 * @throws BSFException
	 */
	public void unregister(final String fileName) {
		scheduler.execute(new Runnable() {
			public void run() {
				doUnregister(fileName);
			}		
		});
	}

	/**
	 * Note that this method expects a file name, formatted as an
	 * argument to the Ruby "require" method.  For example, the workspace
	 * path "/foo/bar.rb", would correspond to the require method parameter,
	 * "foo/bar".
	 * 
	 * @param fileName the argument to the Ruby require method
	 * @throws BSFException
	 */
	public void register(final String fileName) throws BSFException {
		Future<BSFException> result = scheduler.submit(new Callable<BSFException>() {
			public BSFException call() {
				return doRegister(fileName);
			}		
		});
			try {
			BSFException exception = result.get();
			if (exception != null) {
				throw exception;
			}
		} catch (InterruptedException ignored) {
			// no-op
		} catch (ExecutionException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof RaiseException){
				RaiseException re = (RaiseException) cause;
				ScriptLoggingUtil.error(logger, re);
			} else {
				Throwable toLog = ex;
				if (cause != null){
					toLog = cause;
				}
				logger.error(UNABLE_TO_REGISTER_SCRIPT.getText(fileName),
				             toLog);
			}
		}
	}

    /** Sets up for the {@link Strategy#timeout_callback} function to be called after the specified delay,
     * passing the clientdata object into it, if the strategy is still registered
     * @param strategy  The strategy which we are setting up this delayed callback for
     * @param delay Length of the delay before calling the {@link Strategy#timeout_callback} function
     * @param unit  Units of the delay
     * @param clientData    ClientData object passed back to the {@link Strategy#timeout_callback} function.
     */
	public ScheduledFuture<?> registerTimedCallback(Strategy strategy, final long delay,
                                                    TimeUnit unit, Object clientData)
	{
		ScheduledFuture<?> future = getScheduler().schedule(createStrategyCallbackRunnable(strategy, clientData), delay, unit);
		if(logger.isDebugEnabled()) { logger.debug("registering delay callback for "+ delay + " in "+unit); } //$NON-NLS-1$ //$NON-NLS-2$
		return future;
	}

	public ScheduledFuture<?> registerCallbackAtFixedRate(final Strategy strategy, final long delay,
			final long period, TimeUnit unit, final Object clientData)
	{
		ScheduledFuture<?> future = getScheduler().scheduleAtFixedRate(createStrategyCallbackRunnable(strategy, clientData), delay, period, unit);
		if(logger.isDebugEnabled()) { logger.debug("registering delay callback for "+ delay + " in "+unit); } //$NON-NLS-1$ //$NON-NLS-2$
		return future;
	}

	private Runnable createStrategyCallbackRunnable(final Strategy strategy,
			final Object clientData) {
		return new Runnable(){
			public void run() {
				if(logger.isDebugEnabled()) { logger.debug("starting stategy callback on ["+strategy.getName()+"]"); } //$NON-NLS-1$ //$NON-NLS-2$
				try {
                    if(doIsRegistered(strategy.getName())) {
                        strategy.timeout_callback(clientData);
                    } else {
                    	if(logger.isDebugEnabled()) { logger.debug("strategy ["+strategy.getName()+"] is no longer registered"); } //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } catch(RaiseException ex) {
					logger.error(CALLBACK_FUNCTION_ERROR.getText(ex.getException()));
					ScriptLoggingUtil.error(logger, ex);
				}
				if(logger.isDebugEnabled()) { logger.debug("finished strategy callback on ["+strategy.getName()+"]"); } //$NON-NLS-1$ //$NON-NLS-2$
			}
		};
	}

	
	/**
	 * Note that this method expects a file name, formatted as an argument to
	 * the Ruby "require" method. For example, the workspace path "/foo/bar.rb",
	 * would correspond to the require method parameter, "foo/bar".
	 * 
	 * @param fileName
	 *            the argument to the Ruby require method
	 * @throws BSFException
	 */
	public void scriptChanged(final String fileName) throws BSFException{
		Future<BSFException> result = scheduler.submit(new Callable<BSFException>() {
			public BSFException call() {
				return doScriptChanged(fileName);
			}
		});
		try {
			BSFException exception = result.get();
			if (exception != null) {
				throw exception;
			}
		} catch (InterruptedException ignored) {
			// no-op
		} catch (ExecutionException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof RaiseException) {
				ScriptLoggingUtil.error(logger, (RaiseException) cause);
			} else {
				logger.warn(CANNOT_GET_SCRIPT_CHANGE_RESULT.getText(),
				            cause);
			}
		}
	}

	public void onMarketDataEvent(final Message message){
		scheduler.execute(new Runnable() {
			public void run() {
				doOnMarketDataEvent(message);
			}		
		});
	}

	public void onFIXEvent(final Message message){
		scheduler.execute(new Runnable() {
			public void run() {
				doOnFIXEvent(message);
			}		
		});
	}

	public void projectAdded(final String absolutePath) {
		scheduler.execute(new Runnable() {
			public void run() {
				doProjectAdded(absolutePath);
			}
		});
	}


	public void projectRemoved(final String absolutePath) {
		scheduler.execute(new Runnable() {
			public void run() {
				doProjectRemoved(absolutePath);
			}});
	}

	/** Ability to run an arbitrary passed in script */
	public Object evalScript(final String script) throws Exception{
		Future<Object> result = scheduler.submit(new Callable<Object>() {
			public Object call() throws Exception {
				return bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, script);	} //$NON-NLS-1$
		});
		return result.get();
	}	
	

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	private void doOnFIXEvent(final Message message) {
		try {
			for (Strategy aStrategy : registeredStrategies.values()) {
				aStrategy.on_fix_message(message);
			}
		} catch (RaiseException e) {
			ScriptLoggingUtil.error(logger, e);
		}
	}


	
	private void doOnMarketDataEvent(final Message message) {
		try {
			for (Strategy aStrategy : registeredStrategies.values()) {
				aStrategy.on_market_data_message(message);
			}
		} catch (RaiseException e) {
			ScriptLoggingUtil.error(logger, e);
		}
	}

	private void doProjectAdded(final String absolutePath) {
		currentClasspath.add(0, absolutePath);
		bsfManager.setClassPath(currentClasspath.toString());
	}


    /** Override in tests if you need to create strategies on the fly
     * and don't actually want to regsiter them.
     */
    protected Boolean doIsRegistered(final String requireString) {
		return registeredStrategies.containsKey(requireString);
	}


	private void doUnregister(final String fileName) {
		Strategy strategy = registeredStrategies.remove(fileName);
		if (strategy != null){
			strategy.on_dispose();
		}
	}


	private BSFException doRegister(final String fileName) {
		try {
			String classInstanceEvalString = "Photon.new_instance('"+fileName+"')"; //$NON-NLS-1$ //$NON-NLS-2$
			Object strategyObject = bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, classInstanceEvalString); //$NON-NLS-1$
			if (strategyObject instanceof Strategy){
				registeredStrategies.put(fileName, (Strategy) strategyObject);
                Strategy strategy = ((Strategy)strategyObject);
				strategy.setName(fileName);
				strategy.on_create();
            } else {
                throw new IllegalArgumentException(NO_STRATEGY_SUBCLASS.getText(fileName));
			}
			return null;
		} catch (BSFException e) {
			ScriptLoggingUtil.error(logger, e);
			return e;
		}
	}


	private BSFException doScriptChanged(final String fileName) {
		try {
			boolean reregister = doIsRegistered(fileName);
			doUnregister(fileName);
			String evalString = "Dependencies.loaded.reject! {|s| s.ends_with?('"+ fileName + "')}"; //$NON-NLS-1$ //$NON-NLS-2$
			bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString); //$NON-NLS-1$
			evalString = "require_dependency '" + fileName + "'"; //$NON-NLS-1$ //$NON-NLS-2$
			bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString); //$NON-NLS-1$
			if (reregister) {
				BSFException e = doRegister(fileName);
				if (e != null){
					logger.error(UNREGISTERING_SCRIPT.getText(fileName));
				}
			}
			return null;
		} catch (BSFException e) {
			return e;
		}
	}


	private void doProjectRemoved(final String absolutePath) {
		currentClasspath.remove(absolutePath);
		bsfManager.setClassPath(currentClasspath.toString());
	}		

}

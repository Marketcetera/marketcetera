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
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.scripting.ScriptLoggingUtil;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;

import quickfix.Message;


/**
 * Script registry implementation.
 * 
 * @author andrei@lissovski.org
 * @author gmiller
 */
public class ScriptRegistry implements InitializingBean {

	public static final String RUBY_LANG_STRING = "ruby";
	protected BSFManager bsfManager;
	protected BSFEngine engine;
	private Classpath additionalClasspath =  new Classpath();
	private Classpath currentClasspath;
	protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private Logger logger;
	private Map<String, Strategy> registeredStrategies;
	
	static String [] JRUBY_PLUGIN_PATH = {
		"lib/ruby/site_ruby/1.8",
		"lib/ruby/site_ruby/1.8/java",
		"lib/ruby/site_ruby", 
		"lib/ruby/1.8", 
		"lib/ruby/1.8/java",
		"lib/active_support",
		"lib/active_support/active_support",
		"lib/active_support/active_support/vendor"
	};
	
	static String [] JRUBY_WORKSPACE_PATH = {
		""
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

	
	private void initBSFManager() throws BSFException {
		currentClasspath = new Classpath();
		currentClasspath.add(EclipseUtils.getPluginPath(PhotonPlugin.getDefault()).append("src").append("main").append("resources"));
		currentClasspath.add(ResourcesPlugin.getWorkspace().getRoot().getProject(PhotonPlugin.DEFAULT_PROJECT_NAME).getLocation());

		
		currentClasspath.addAll(additionalClasspath);
		IPath jrubyPluginPath = EclipseUtils.getPluginPath(JRubyPlugin.getDefault());
		PhotonPlugin.getMainConsoleLogger().debug("Using base path of JRuby plugin: "+jrubyPluginPath.toString());
		updateClasspath(currentClasspath, jrubyPluginPath, JRUBY_PLUGIN_PATH);
		updateClasspath(currentClasspath, EclipseUtils.getWorkspacePath(), JRUBY_WORKSPACE_PATH);
		String classpathString = currentClasspath.toString();
		bsfManager = new BSFManager();
		bsfManager.setClassPath(classpathString);

		engine = bsfManager.loadScriptingEngine("ruby");

		engine.eval("<java>", 1, 1, "require 'active_support/core_ext'");
		engine.eval("<java>", 1, 1, "require 'active_support/dependencies'");
		engine.eval("<java>", 1, 1, "require 'photon'");
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
			logger.debug("Error getting a value out of a future.get", e);
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
				logger.error("Unable to register script", toLog);
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
		if(logger.isDebugEnabled()) { logger.debug("registering delay callback for "+ delay + " in "+unit); }
		return future;
	}

	public ScheduledFuture<?> registerCallbackAtFixedRate(final Strategy strategy, final long delay,
			final long period, TimeUnit unit, final Object clientData)
	{
		ScheduledFuture<?> future = getScheduler().scheduleAtFixedRate(createStrategyCallbackRunnable(strategy, clientData), delay, period, unit);
		if(logger.isDebugEnabled()) { logger.debug("registering delay callback for "+ delay + " in "+unit); }
		return future;
	}

	private Runnable createStrategyCallbackRunnable(final Strategy strategy,
			final Object clientData) {
		return new Runnable(){
			public void run() {
				if(logger.isDebugEnabled()) { logger.debug("starting stategy callback on ["+strategy.getName()+"]"); }
				try {
                    if(doIsRegistered(strategy.getName())) {
                        strategy.timeout_callback(clientData);
                    } else {
                    	if(logger.isDebugEnabled()) { logger.debug("strategy ["+strategy.getName()+"] is no longer registered"); }
                    }
                } catch(RaiseException ex) {
					logger.error("Error in timeout_callback function: "+ex.getException());
					ScriptLoggingUtil.error(logger, ex);
				}
				if(logger.isDebugEnabled()) { logger.debug("finished strategy callback on ["+strategy.getName()+"]"); }
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
			// TODO: internationalize this
			Throwable cause = ex.getCause();
			if (cause instanceof RaiseException) {
				ScriptLoggingUtil.error(logger, (RaiseException) cause);
			} else {
				logger.warn("Unable to get a result of Ruby script change", cause);
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
				return bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, script);	}
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
			String classInstanceEvalString = "Photon.new_instance('"+fileName+"')";
			Object strategyObject = bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, classInstanceEvalString);
			if (strategyObject instanceof Strategy){
				registeredStrategies.put(fileName, (Strategy) strategyObject);
                Strategy strategy = ((Strategy)strategyObject);
				strategy.setName(fileName);
				strategy.on_create();
            } else {
				throw new IllegalArgumentException("File '"+fileName+"' does not contain a subclass of Strategy");
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
			String evalString = "Dependencies.loaded.reject! {|s| s.ends_with?('"+ fileName + "')}";
			bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
			evalString = "require_dependency '" + fileName + "'";
			bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
			if (reregister) {
				BSFException e = doRegister(fileName);
				if (e != null){
					logger.error("Script unregistered due to exception, see previous errors");
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

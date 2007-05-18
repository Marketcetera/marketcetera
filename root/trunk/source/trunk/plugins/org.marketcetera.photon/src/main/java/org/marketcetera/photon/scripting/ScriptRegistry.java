package org.marketcetera.photon.scripting;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jruby.bsf.JRubyPlugin;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
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

	private static final String MESSAGE_BEAN_NAME = "message";
	public static final String RUBY_LANG_STRING = "ruby";
	protected BSFManager bsfManager;
	protected BSFEngine engine;
	private Classpath additionalClasspath =  new Classpath();
	private Classpath currentClasspath;
	protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private Logger logger;

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
		marketDataFeedTracker.setMarketDataListener(new MarketDataListener() {
			public void onMessage(Message aQuote) {
				onMarketDataEvent(aQuote);
			}
		});
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

		Object result; // wasted
		result = engine.eval("<java>", 1, 1, "require 'active_support/core_ext'");
		result = engine.eval("<java>", 1, 1, "require 'active_support/dependencies'");
		result = engine.eval("<java>", 1, 1, "require 'photon'");
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
			// TODO: internationalize this
			logger.warn("Unable to get a result of Ruby script registration", ex);
		}
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
			logger.warn("Unable to get a result of Ruby script change", ex);
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
			bsfManager.undeclareBean(MESSAGE_BEAN_NAME);
			bsfManager.declareBean(MESSAGE_BEAN_NAME, message, message.getClass());
			bsfManager.exec(RUBY_LANG_STRING, "<java>", 1, 1, "Photon.on_fix_message($message)");
		} catch (BSFException e) {
			ScriptLoggingUtil.error(logger, e);
		}
	}


	
	private void doOnMarketDataEvent(final Message message) {
		try {
			bsfManager.undeclareBean(MESSAGE_BEAN_NAME);
			bsfManager.declareBean(MESSAGE_BEAN_NAME, message, message.getClass());
			bsfManager.exec(RUBY_LANG_STRING, "<java>", 1, 1, "Photon.on_market_data_message($message)");
		} catch (BSFException e) {
			ScriptLoggingUtil.error(logger, e);
		}
	}

	private void doProjectAdded(final String absolutePath) {
		currentClasspath.add(0, absolutePath);
		bsfManager.setClassPath(currentClasspath.toString());
	}


	private Boolean doIsRegistered(final String requireString) {
		try {
			String evalString = "Photon.is_registered?('"+requireString+"')";
			return (Boolean) bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
		} catch (BSFException e) {
			ScriptLoggingUtil.error(logger, e);
		}
		return false;
	}


	private void doUnregister(final String fileName) {
		try {
			String evalString = "Photon.unregister('"+fileName+"')";
			bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
		} catch (BSFException e) {
			ScriptLoggingUtil.error(logger, e);
		}
	}


	private BSFException doRegister(final String fileName) {
		try {
			String evalString = "Photon.register('"+fileName+"')";
			bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, evalString);
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
				doRegister(fileName);
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

package org.marketcetera.photon.actions;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataFeedFactory;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.IMarketDataConstants;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.quickfix.ConnectionConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ReconnectMarketDataFeedJob extends Job {

	private static AtomicBoolean reconnectInProgress = new AtomicBoolean(false);
	private BundleContext bundleContext;
	MarketDataFeedTracker marketDataFeedTracker;

	
	public ReconnectMarketDataFeedJob(String name) {
		super(name);
		bundleContext = PhotonPlugin.getDefault().getBundleContext();
		marketDataFeedTracker = new MarketDataFeedTracker(bundleContext);
		marketDataFeedTracker.open();

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		Logger logger = plugin.getMainLogger();
		Logger marketDataLogger = plugin.getMarketDataLogger();
		if (reconnectInProgress.getAndSet(true)){
			return Status.CANCEL_STATUS;
		}
		boolean succeeded = false;
		try {
			disconnect(marketDataFeedTracker);
		} catch (Throwable th) {
			logger.warn("Could not disconnect from quote feed");
		}
		try {

			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
	    	IExtensionPoint extensionPoint =
	    	extensionRegistry.getExtensionPoint(IMarketDataConstants.EXTENSION_POINT_ID);
	    	IExtension[] extensions = extensionPoint.getExtensions();

	    	Set<String> startupFeeds = getStartupFeeds();

	    	if (logger.isDebugEnabled()) { logger.debug("Marketdata: examining "+extensions.length+" extensions"); }
	    	if (extensions != null && extensions.length > 0)
	    	{
	    		for (IExtension anExtension : extensions) {
	    			String pluginName = anExtension.getContributor().getName();
					if (startupFeeds.contains(pluginName)) {
				    	if (logger.isDebugEnabled()) { logger.debug("Marketdata: using "+pluginName); }
		    			IConfigurationElement[] configurationElements = anExtension.getConfigurationElements();
			    		IConfigurationElement feedElement = configurationElements[0];
			    		String factoryClass = feedElement.getAttribute(IMarketDataConstants.FEED_FACTORY_CLASS_ATTRIBUTE);
			    		Class<IMarketDataFeedFactory> clazz = (Class<IMarketDataFeedFactory>) Class.forName(factoryClass);
			    		Constructor<IMarketDataFeedFactory> constructor = clazz.getConstructor( new Class[0] );
			    		IMarketDataFeedFactory factory = constructor.newInstance(new Object[0]);
			    		ScopedPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), pluginName);
			    		String url = getPreference(store, ConnectionConstants.MARKETDATA_URL_SUFFIX);
			    		String user = getPreference(store, ConnectionConstants.MARKETDATA_USER_SUFFIX);
			    		String password = getPreference(store, ConnectionConstants.MARKETDATA_PASSWORD_SUFFIX);
			    		Map<String, Object> parameters = getParameters(factory, store, pluginName);
			    		IMarketDataFeed targetQuoteFeed = factory.getInstance(url, user, password, parameters, marketDataLogger);
			    		// Quote feed must be started before registration so
						// that resubscription works properly. See bug #213.
			    		targetQuoteFeed.start();
			    		
		    			MarketDataFeedService marketDataFeedService = new MarketDataFeedService(targetQuoteFeed);
						ServiceRegistration registration = bundleContext.registerService(MarketDataFeedService.class.getName(), marketDataFeedService, null);
		    			marketDataFeedService.setServiceRegistration(registration);
		    			marketDataFeedService.afterPropertiesSet();
		    			
		    			succeeded = true;
		    			break;
	    			} else {
				    	 logger.warn("Marketdata: not using "+pluginName+" because it is not listed in 'org.marketcetera.photon/marketdata.startup'");
	    			}
				}
			}
	
	    	if (logger.isDebugEnabled()) { logger.debug("Marketdata: done examining "+extensions.length+" extensions"); }
		} catch (Exception e) {
			logger.error("Exception connecting to market data feed: "+e.getMessage(), e);
			return Status.CANCEL_STATUS;
		} finally {
			reconnectInProgress.set(false);
			if (!succeeded){
				logger.error("Error connecting to market data feed");
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;

	}

	private Map<String, Object> getParameters(IMarketDataFeedFactory factory, ScopedPreferenceStore store, String pluginName) {
		String[] keys = factory.getAllowedPropertyKeys();
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : keys) {
			String fqKey = key;
			if (store.contains(fqKey)){
				map.put(key, store.getString(fqKey));
			}
		}
		return map;
	}

	private String getPreference(ScopedPreferenceStore store, String ... pieces) {
		return store.getString(constructKey(pieces));
	}

	private String constructKey(String... pieces) {
		StringBuilder builder = new StringBuilder();
		int i;
		for (i = 0; i < pieces.length-1; i++) {
			builder.append(pieces[i]);
			builder.append('.');
		}
		builder.append(pieces[i]);
		return builder.toString();
	}

	private Set<String> getStartupFeeds() {
		String startupString = PhotonPlugin.getDefault().getPreferenceStore().getString(ConnectionConstants.MARKETDATA_STARTUP_KEY);
		startupString.split("[\\s,]+");
		return new HashSet<String>(Arrays.asList(startupString.split("[\\s,]+")));
	}

	public static void disconnect(MarketDataFeedTracker marketDataFeedTracker) {

		MarketDataFeedService service = (MarketDataFeedService) marketDataFeedTracker.getMarketDataFeedService();
		if (service != null){
			ServiceRegistration serviceRegistration = service.getServiceRegistration();
			if (serviceRegistration != null){
				serviceRegistration.unregister();
			}
		}

		RuntimeException caughtException = null;
		if (service != null){
			try {
				service.stop();
			} catch (RuntimeException e) {
				caughtException = e;
			}
		}
		if (caughtException != null){
			throw caughtException;
		}
	}
	
	// TODO: refactor this and run()
	public static String [][] getFeedNames() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    	IExtensionPoint extensionPoint =
    	extensionRegistry.getExtensionPoint(IMarketDataConstants.EXTENSION_POINT_ID);
    	IExtension[] extensions = extensionPoint.getExtensions();

    	String[][] result = new String[extensions.length][2];
    	int i = 0;
    	for (IExtension anExtension : extensions) {
			IContributor contributor = anExtension.getContributor();

			String pluginName = contributor.getName();
			String providerName = pluginName;
			try {
    			IConfigurationElement[] configurationElements = anExtension.getConfigurationElements();
	    		IConfigurationElement feedElement = configurationElements[0];

	    		String factoryClass = feedElement.getAttribute(IMarketDataConstants.FEED_FACTORY_CLASS_ATTRIBUTE);
	    		Class<IMarketDataFeedFactory> clazz = (Class<IMarketDataFeedFactory>) Class.forName(factoryClass);
	    		Constructor<IMarketDataFeedFactory> constructor = clazz.getConstructor( new Class[0] );
	    		IMarketDataFeedFactory factory = constructor.newInstance(new Object[0]);
	    		providerName = factory.getProviderName();
			} catch (Exception ex){
				// do nothing.
			}
			
			result[i][0] = providerName;
			result[i][1] = pluginName;
			i++;
		}
    	return result;
	}

}

package org.marketcetera.photon.actions;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataFeedFactory;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.IMarketDataConstants;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

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
		Logger logger = PhotonPlugin.getMainConsoleLogger();
		if (reconnectInProgress.getAndSet(true)){
			return Status.CANCEL_STATUS;
		}
		boolean succeeded = false;
		try {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
	    	IExtensionPoint extensionPoint =
	    	extensionRegistry.getExtensionPoint(IMarketDataConstants.EXTENSION_POINT_ID);
	    	IExtension[] extensions = extensionPoint.getExtensions();
	    	if (extensions != null && extensions.length > 0)
	    	{
	    		IConfigurationElement[] configurationElements = extensions[0].getConfigurationElements();
	    		IConfigurationElement feedElement = configurationElements[0];
	    		String factoryClass = feedElement.getAttribute(IMarketDataConstants.FEED_FACTORY_CLASS_ATTRIBUTE);
	    		Class<IMarketDataFeedFactory> clazz = (Class<IMarketDataFeedFactory>) Class.forName(factoryClass);
	    		Constructor<IMarketDataFeedFactory> constructor = clazz.getConstructor( new Class[0] );
	    		IMarketDataFeedFactory factory = constructor.newInstance(new Object[0]);
	    		IMarketDataFeed targetQuoteFeed = factory.getInstance("", "", "");
    			ServiceRegistration registration = bundleContext.registerService(MarketDataFeedService.class.getName(), new MarketDataFeedService(targetQuoteFeed), null);
    			targetQuoteFeed.start();
    			succeeded = true;
			}
	
		} catch (Exception e) {
			logger.error("Exception connecting to quote feed", e);
			return Status.CANCEL_STATUS;
		} finally {
			if (!succeeded){
				logger.error("Error connecting to quote feed");
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;

	}

	public void disconnect() {
		MarketDataFeedService service = (MarketDataFeedService) marketDataFeedTracker.getMarketDataFeedService();
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
	
	@Override
	protected void finalize() throws Throwable {
		marketDataFeedTracker.close();
	}


}

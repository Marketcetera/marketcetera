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
import org.marketcetera.core.IFeedComponent.FeedStatus;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.quotefeed.IQuoteFeedConstants;
import org.marketcetera.photon.quotefeed.QuoteFeedService;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.marketcetera.quotefeed.IQuoteFeedFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class ReconnectQuoteFeedJob extends Job {

	private static AtomicBoolean reconnectInProgress = new AtomicBoolean(false);
	private BundleContext bundleContext;
	ServiceTracker quoteFeedTracker;

	
	public ReconnectQuoteFeedJob(String name) {
		super(name);
		bundleContext = PhotonPlugin.getDefault().getBundleContext();
		quoteFeedTracker = new ServiceTracker(bundleContext, QuoteFeedService.class.getName(), null);
		quoteFeedTracker.open();

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
	    		targetQuoteFeed.setQuoteJmsOperations(PhotonPlugin.getDefault().getQuoteJmsOperations());
	    		if (targetQuoteFeed != null
	    				&& targetQuoteFeed.getFeedStatus() != FeedStatus.ERROR){
		    		QuoteFeedService feedService = new QuoteFeedService();
		    		feedService.setQuoteFeed(targetQuoteFeed);
		    		feedService.afterPropertiesSet();
	    			ServiceRegistration registration = bundleContext.registerService(QuoteFeedService.class.getName(), feedService, null);
	    			feedService.setServiceRegistration(registration);
	    			targetQuoteFeed.start();
	    			succeeded = true;
	    		}
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
		QuoteFeedService feed = (QuoteFeedService) quoteFeedTracker.getService();
		RuntimeException caughtException = null;
		if (feed != null){
			try {
				feed.getQuoteFeed().stop();
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
		quoteFeedTracker.close();
	}


}

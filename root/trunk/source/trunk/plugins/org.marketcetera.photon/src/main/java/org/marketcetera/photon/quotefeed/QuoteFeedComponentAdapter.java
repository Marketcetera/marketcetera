package org.marketcetera.photon.quotefeed;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.marketcetera.core.IFeedComponent;
import org.marketcetera.photon.DelegatingFeedComponentAdapter;
import org.marketcetera.photon.FeedComponentAdapterBase;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.marketcetera.quotefeed.IQuoteFeedFactory;
import org.springframework.jms.core.JmsOperations;

public class QuoteFeedComponentAdapter extends DelegatingFeedComponentAdapter {

	private IQuoteFeed quoteFeed;
	private JmsOperations quoteJmsOperations;
	private FeedStatus myStatus = FeedStatus.OFFLINE;
	
	@Override
	public void afterPropertiesSet() throws Exception {
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
	    		if (targetQuoteFeed != null){
	    			quoteFeed = targetQuoteFeed;
	    			targetQuoteFeed.setQuoteJmsOperations(quoteJmsOperations);
	    		}
			}
	
			super.afterPropertiesSet();
			quoteFeed.start();
			succeeded = true;
		} finally {
			if (!succeeded){
				quoteFeed = null;
				myStatus = FeedStatus.ERROR;
			}
			fireFeedComponentChanged();
		}
	}

	@Override
	public IFeedComponent getDelegateFeedComponent() {
		return quoteFeed;
	}

	public JmsOperations getQuoteJmsOperations() {
		return quoteJmsOperations;
	}

	public void setQuoteJmsOperations(JmsOperations quoteJmsOperations) {
		this.quoteJmsOperations = quoteJmsOperations;
	}

	@Override
	protected FeedStatus getAdapterFeedStatus() {
		return myStatus;
	}
	
	

}

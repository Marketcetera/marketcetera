package org.marketcetera.photon.internal.marketdata;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.ListenerList;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.URNUtils;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener.IFeedStatusEvent;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * This is the internal implementation of {@link IMarketDataFeed}. It is initialized with a
 * ModuleURN for a market data module provider and abstracts interactions with the underlying module
 * framework.
 * <p>
 * Each instance supports the registration of {@code IFeedStatusChangedListener} objects. This is
 * used to notify {@code MarketDataManager} when the status of a feed changes.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class MarketDataFeed implements IMarketDataFeed {

	public static final String MARKET_DATA_PROVIDER_TYPE = "mdata"; //$NON-NLS-1$
	private static final String FEED_STATUS_ATTRIBUTE = "FeedStatus"; //$NON-NLS-1$

	private static final FeedStatusFilter sFeedStatusFilter = new FeedStatusFilter();

	private final ModuleManager mModuleManager = ModuleSupport.getModuleManager();
	private final MBeanServerConnection mMBeanServer = ModuleSupport.getMBeanServerConnection();
	private final FeedStatusNotificationListener mFeedStatusNotificationListener = new FeedStatusNotificationListener();
	private final ListenerList mStatusListeners = new ListenerList();

	private final ModuleURN mInstanceURN;
	private final AbstractMarketDataModuleMXBean mMBeanProxy;
	private final String mDescription;

	/**
	 * Contructor. An exception will be thrown if the supplied URN is invalid or does not correspond
	 * to a valid market data feed provider. Valid providers must have provider type "mdata" and be
	 * singletons. The singleton instance must have an MXBean interface that implements
	 * {@link AbstractMarketDataModuleMXBean} and {@link NotificationEmitter}.
	 * 
	 * @param providerURN
	 *            the URN of the feed provider
	 * @throws I18NException
	 *             if the feed provider is invalid
	 */
	public MarketDataFeed(final ModuleURN providerURN) throws I18NException {
		try {
			URNUtils.validateProviderURN(providerURN);
			if (!providerURN.providerType().equals(MARKET_DATA_PROVIDER_TYPE))
				throw new I18NException(Messages.MARKET_DATA_FEED_INVALID_PROVIDER_TYPE);
			mDescription = mModuleManager.getProviderInfo(providerURN).getDescription();
			List<ModuleURN> instances = mModuleManager.getModuleInstances(providerURN);
			if (instances.size() != 1)
				throw new I18NException(Messages.MARKET_DATA_FEED_NOT_SINGLETON);
			mInstanceURN = instances.get(0);
			if (!mModuleManager.getModuleInfo(mInstanceURN).isEmitter())
				throw new I18NException(Messages.MARKET_DATA_FEED_NOT_EMITTER);
			ObjectName objectName = mInstanceURN.toObjectName();
			if (!mMBeanServer.isRegistered(objectName)
					|| !mMBeanServer.isInstanceOf(objectName, AbstractMarketDataModuleMXBean.class
							.getName()))
				throw new I18NException(Messages.MARKET_DATA_FEED_INVALID_INTERFACE);
			mMBeanProxy = JMX.newMXBeanProxy(mMBeanServer, objectName,
					AbstractMarketDataModuleMXBean.class, true);
			((NotificationEmitter) mMBeanProxy).addNotificationListener(
					mFeedStatusNotificationListener, sFeedStatusFilter, null);
		} catch (MXBeanOperationException e) {
			throw new I18NException(e, Messages.MARKET_DATA_FEED_INVALID_OBJECT_NAME);
		} catch (UndeclaredThrowableException e) {
			throw new I18NException(e.getCause(), Messages.MARKET_DATA_FEED_INVALID_INTERFACE);
		} catch (IOException e) {
			throw new I18NException(e, Messages.MARKET_DATA_FEED_INVALID_INTERFACE);
		} catch (InstanceNotFoundException e) {
			throw new I18NException(e, Messages.MARKET_DATA_FEED_INVALID_INTERFACE);
		}
	}

	@Override
	public ModuleURN getURN() {
		return mInstanceURN;
	}

	@Override
	public String getId() {
		return mInstanceURN.parent().toString();
	}

	@Override
	public String getName() {
		return mDescription;
	}

	@Override
	public FeedStatus getStatus() {
		try {
			return FeedStatus.valueOf(mMBeanProxy.getFeedStatus());
		} catch (Exception e) {
			Messages.MARKET_DATA_FEED_INVALID_STATUS_NOTIFICATION.warn(MarketDataFeed.this, e,
					Arrays.toString(FeedStatus.values()));
			return FeedStatus.UNKNOWN;
		}
	}

	@Override
	public Set<Capability> getCapabilities() {
		try {
			Set<Capability> capabilities = mMBeanProxy.getCapabilities();
			Validate.noNullElements(capabilities);
			return capabilities;
		} catch (Exception e) {
			Messages.MARKET_DATA_FEED_FAILED_TO_DETERMINE_CAPABILITY.error(this, e, mInstanceURN);
			return EnumSet.of(Capability.UNKNOWN);
		}
	}

	/**
	 * Attempt to reconnect the feed.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the feed does not support this behavior
	 */
	public void reconnect() {
		mMBeanProxy.reconnect();
	}

	/**
	 * Adds a listener to this feed.
	 * 
	 * @param listener
	 *            to be notified when the feed status changes
	 */
	public void addFeedStatusChangedListener(final IFeedStatusChangedListener listener) {
		mStatusListeners.add(listener);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeFeedStatusChangedListener(final IFeedStatusChangedListener listener) {
		mStatusListeners.remove(listener);
	}

	private void notifyListeners(final FeedStatus oldValue, final FeedStatus newValue) {
		FeedStatusEvent event = createFeedStatusEvent(oldValue, newValue);
		Object[] changeListeners = mStatusListeners.getListeners();
		for (Object object : changeListeners) {
			((IFeedStatusChangedListener) object).feedStatusChanged(event);
		}
	}

	/**
	 * This method is provided for {@link MarketDataManager} to generate notifications when the
	 * active feed changes. It should only be used for that purpose.
	 * 
	 * @param oldValue
	 *            old status value
	 * @param newValue
	 *            new status value
	 * 
	 * @return a {@link FeedStatusEvent} with the given parameters
	 */
	public FeedStatusEvent createFeedStatusEvent(final FeedStatus oldValue,
			final FeedStatus newValue) {
		return new FeedStatusEvent(this, oldValue, newValue);
	}

	/**
	 * Handles JMX notifications, assuming they have been filtered by {@code FeedStatusFilter}.
	 */
	@ClassVersion("$Id$")
	private final class FeedStatusNotificationListener implements NotificationListener {

		@Override
		public void handleNotification(final Notification notification, final Object handback) {
			AttributeChangeNotification change = (AttributeChangeNotification) notification;
			try {
				notifyListeners(FeedStatus.valueOf(change.getOldValue().toString()), FeedStatus
						.valueOf(change.getNewValue().toString()));
			} catch (Exception e) {
				Messages.MARKET_DATA_FEED_INVALID_STATUS_NOTIFICATION.warn(MarketDataFeed.this, e,
						Arrays.toString(FeedStatus.values()));
			}
		}

	}

	/**
	 * Filters notifications, accepting only "FeedStatus" attribute change notifications.
	 */
	@ClassVersion("$Id$")
	private static final class FeedStatusFilter implements NotificationFilter {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isNotificationEnabled(final Notification notification) {
			return (notification instanceof AttributeChangeNotification)
					&& ((AttributeChangeNotification) notification).getAttributeName().equals(
							FEED_STATUS_ATTRIBUTE);
		}
	};

	/**
	 * Event object for feed status changes.
	 */
	@ClassVersion("$Id$")
	public static final class FeedStatusEvent extends EventObject implements IFeedStatusEvent {
		private static final long serialVersionUID = 1L;
		private final FeedStatus mOldStatus;
		private final FeedStatus mNewStatus;

		private FeedStatusEvent(final Object source, final FeedStatus oldStatus,
				final FeedStatus newStatus) {
			super(source);
			mOldStatus = oldStatus;
			mNewStatus = newStatus;
		}

		@Override
		public FeedStatus getOldStatus() {
			return mOldStatus;
		}

		@Override
		public FeedStatus getNewStatus() {
			return mNewStatus;
		}
	}
}
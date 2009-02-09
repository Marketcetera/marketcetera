package org.marketcetera.photon.marketdata;

import static org.marketcetera.marketdata.Messages.*;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.UnsupportedRequestParameterType;

/* $License$ */

/**
 * Factory for {@link MockMarketDataModule}, for testing.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MockMarketDataModuleFactory extends ModuleFactory {

	static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:mock");
	static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");
	static MockMarketDataModule sInstance;

	public MockMarketDataModuleFactory() {
		super(PROVIDER_URN, new MockI18NMessage("Mock Feed"), false, false);
	}

	@Override
	public Module create(Object... inParameters) throws ModuleCreationException {
		sInstance = new MockMarketDataModule(INSTANCE_URN);
		return sInstance;
	}
	
	/**
	 * Mock market data module for testing.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	public static class MockMarketDataModule extends Module implements
			AbstractMarketDataModuleMXBean, NotificationEmitter, DataEmitter {

		private final NotificationBroadcasterSupport mNotificationDelegate;
		
		private final AtomicLong mSequence = new AtomicLong();

		private String mStatus;

		private DataEmitterSupport mEmitSupport;
		
		void setStatus(String status) {
			mStatus = status;
		}
		
		void fireNotification(String oldStatus, String newStatus) {
			mNotificationDelegate.sendNotification(new AttributeChangeNotification(this,
	                mSequence.getAndIncrement(),
	                System.currentTimeMillis(),
	                FEED_STATUS_CHANGED.getText(),
	                "FeedStatus", //$NON-NLS-1$
	                "String", //$NON-NLS-1$
	                oldStatus,
	                newStatus));
		}
		
		void emitData(Object data) {
			if (mEmitSupport != null) {
				mEmitSupport.send(data);
			}
		}

		protected MockMarketDataModule(ModuleURN urn) {
			super(urn, false);
			MBeanNotificationInfo notifyInfo = new MBeanNotificationInfo(
					new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
					AttributeChangeNotification.class.getName(),
					"Mock Market Data Module notification");
			mNotificationDelegate = new NotificationBroadcasterSupport(notifyInfo);
			sInstance = this;
		}

		@Override
		protected void preStart() throws ModuleException {
		}

		@Override
		protected void preStop() throws ModuleException {
		}

		@Override
		public String getFeedStatus() {
			return mStatus;
		}

		@Override
		public void removeNotificationListener(NotificationListener listener,
				NotificationFilter filter, Object handback)
				throws ListenerNotFoundException {
			mNotificationDelegate.removeNotificationListener(listener, filter,
					handback);
		}

		@Override
		public void addNotificationListener(NotificationListener listener,
				NotificationFilter filter, Object handback)
				throws IllegalArgumentException {
			mNotificationDelegate.addNotificationListener(listener, filter, handback);
		}

		@Override
		public MBeanNotificationInfo[] getNotificationInfo() {
			return mNotificationDelegate.getNotificationInfo();
		}

		@Override
		public void removeNotificationListener(NotificationListener listener)
				throws ListenerNotFoundException {
			mNotificationDelegate.removeNotificationListener(listener);
		}

		@Override
		public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
		}

		@Override
		public void requestData(DataRequest inRequest,
				DataEmitterSupport inSupport)
				throws UnsupportedRequestParameterType,
				IllegalRequestParameterValue {
			mEmitSupport = inSupport;			
		}

		@Override
		public void reconnect() {
		}

	}
}

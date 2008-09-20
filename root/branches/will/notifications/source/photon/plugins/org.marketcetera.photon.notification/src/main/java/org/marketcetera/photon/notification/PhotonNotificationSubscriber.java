package org.marketcetera.photon.notification;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Core notification subscriber. Adapts {@link ISubscriber} to {@link INotificationProvider}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class PhotonNotificationSubscriber implements ISubscriber,
		INotificationProvider {

	/**
	 * Queue for incoming notifications
	 */
	private Queue<INotification> queue;

	@Override
	public boolean isInteresting(Object inData) {
		return inData instanceof INotification;
	}

	@Override
	public void publishTo(final Object inData) {
		getNotificationQueue().add((INotification) inData);
	}

	@Override
	public Queue<INotification> getNotificationQueue() {
		if (queue == null)
			queue = new ConcurrentLinkedQueue<INotification>();
		return queue;
	}
}

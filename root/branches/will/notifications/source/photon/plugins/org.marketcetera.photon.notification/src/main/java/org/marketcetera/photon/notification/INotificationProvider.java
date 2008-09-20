package org.marketcetera.photon.notification;

import java.util.Queue;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for notification providers.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public interface INotificationProvider {

	/**
	 * Returns a queue that can be used to retrieve notifications. The queue
	 * should be thread safe since it may be processed from a separate thread.
	 * 
	 * @return the queue that provides notifications
	 */
	Queue<INotification> getNotificationQueue();
}

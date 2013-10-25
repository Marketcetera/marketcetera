package org.marketcetera.core.notifications;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.core.publisher.ISubscriber;

/* $License$ */

/**
 * Coordinates receipt and delivery of {@link INotification} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface INotificationManager
    extends IPublisher
{
    /**
     * Subscribes to all notifications.
     * 
     * <p>If the given <code>ISubscriber</code> is already subscribed this method does nothing.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void subscribe(ISubscriber inSubscriber);
    /**
     * Unsubscribes to all notifications.
     * 
     * <p>If the given <code>ISubscriber</code> is not already subscribed this method does nothing.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void unsubscribe(ISubscriber inSubscriber);
    /**
     * Publishes an <code>INotification</code>.
     * 
     * <p>The given <code>INotification</code> is published to all subscribers in the order they
     * subscribed.
     *
     * @param inNotification an <code>INotification</code> value
     */
    public void publish(INotification inNotification);
}

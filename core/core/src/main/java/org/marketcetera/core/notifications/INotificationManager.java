package org.marketcetera.core.notifications;

import org.marketcetera.api.systemmodel.Publisher;
import org.marketcetera.api.systemmodel.Subscriber;

/* $License$ */

/**
 * Coordinates receipt and delivery of {@link INotification} objects.
 *
 * @version $Id: INotificationManager.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.8.0
 */
public interface INotificationManager
    extends Publisher
{
    /**
     * Subscribes to all notifications.
     * 
     * <p>If the given <code>ISubscriber</code> is already subscribed this method does nothing.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void subscribe(Subscriber inSubscriber);
    /**
     * Unsubscribes to all notifications.
     * 
     * <p>If the given <code>ISubscriber</code> is not already subscribed this method does nothing.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void unsubscribe(Subscriber inSubscriber);
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

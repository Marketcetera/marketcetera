package org.marketcetera.core.notifications;

import java.util.concurrent.ExecutionException;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.publisher.Subscriber;
import org.marketcetera.core.publisher.PublisherEngine;

/* $License$ */

/**
 * Provides common behaviors to notification executor implementors.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractNotificationExecutor
        implements NotificationExecutor
{
    /* (non-Javadoc)
     * @see com.marketcetera.cannonballtrading.notification.NotificationExecutor#notify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    public void notify(INotification inNotification)
    {
        publish(inNotification);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#subscribe(org.marketcetera.core.publisher.Subscriber)
     */
    @Override
    public void subscribe(Subscriber inSubscriber)
    {
        publisher.subscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#unsubscribe(org.marketcetera.core.publisher.Subscriber)
     */
    @Override
    public void unsubscribe(Subscriber inSubscriber)
    {
        publisher.unsubscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#publish(java.lang.Object)
     */
    @Override
    public void publish(Object inData)
    {
        publisher.publish(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#publishAndWait(java.lang.Object)
     */
    @Override
    public void publishAndWait(Object inData)
            throws InterruptedException, ExecutionException
    {
        publisher.publishAndWait(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#getSubscriptionCount()
     */
    @Override
    public int getSubscriptionCount()
    {
        return publisher.getSubscriptionCount();
    }
    /**
     * manages subscriptions and publications
     */
    private PublisherEngine publisher = new PublisherEngine(true);
}

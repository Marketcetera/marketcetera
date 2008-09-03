package org.marketcetera.core.notifications;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;

/* $License$ */

/**
 * Collects and dispenses <code>INotification</code> objects produced.
 * 
 * <p>This implementation is responsible for collating and distributing <code>INotification</code>
 * objects generated.  Objects interested in receiving <code>INotification</code> objects should
 * register for them as follows:
 * <pre>
 *     NotificationManager.getNotificationManager().subscribe(ISubscriber);
 * </pre>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: AccountID.java 9456 2008-07-31 22:28:30Z klim $") //$NON-NLS-1$
public class NotificationManager
        implements INotificationManager
{
    /**
     * singleton instance of <code>NotificationManager</code>
     */
    private static final INotificationManager sNotificationManager = new NotificationManager();
    /**
     * manages subscriptions and publications
     */
    private final PublisherEngine mPublisher = new PublisherEngine();
    /**
     * Gets the <code>INotificationManager</code> instance.
     *
     * @return an <code>INotificationManager</code> value
     */
    public static INotificationManager getNotificationManager()
    {
        return sNotificationManager;
    }
    /**
     * Create a new NotificationManager instance.
     */
    private NotificationManager()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotificationManager#subscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void subscribe(ISubscriber inSubscriber)
    {
        mPublisher.subscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotificationManager#unsubscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void unsubscribe(ISubscriber inSubscriber)
    {
        mPublisher.unsubscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotificationManager#publish(org.marketcetera.core.notifications.INotification)
     */
    @Override
    public void publish(INotification inNotification)
    {
        getPublisher().publish(inNotification);
    }
    /**
     * Gets the <code>PublisherEngine</code> for this object.
     *
     * @return a <code>PublisherEngine</code> value
     */
    private PublisherEngine getPublisher()
    {
        return mPublisher;
    }
}

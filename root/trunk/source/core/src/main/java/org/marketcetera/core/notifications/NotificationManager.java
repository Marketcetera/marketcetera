package org.marketcetera.core.notifications;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.util.log.SLF4JLoggerProxy;

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
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
        // create a special subscriber that is notified of every publication
        subscribe(new NotificationLogger());
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
    /**
     * Logs all notifications to a particular logger category.
     * 
     * <p>All notifications are logged at <code>INFO</code> level to
     * <code>org.marketcetera.notifications.log</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.8.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    private static class NotificationLogger
        implements ISubscriber
    {
        /**
         * the special category to use for notifications
         */
        private static final String CATEGORY = "notifications.log"; //$NON-NLS-1$
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return inData instanceof INotification;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            SLF4JLoggerProxy.info(CATEGORY,
                                  inData.toString());
        }
    }
}

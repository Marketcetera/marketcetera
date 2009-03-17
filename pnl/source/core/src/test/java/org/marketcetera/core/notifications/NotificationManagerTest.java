package org.marketcetera.core.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.core.publisher.PublisherEngineTest;
import org.marketcetera.marketdata.MarketDataFeedTestBase;

/* $License$ */

/**
 * Tests {@link NotificationManager}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.8.0
 */
public class NotificationManagerTest
{
    /**
     * Tests that the static getter returns a non-null value.
     *
     * @throws Exception
     */
    @Test
    public void testGetter()
        throws Exception
    {
        assertNotNull(NotificationManager.getNotificationManager());
    }
    /**
     * Tests that the subscribe and publish mechanism functions as expected.
     * 
     * <p>Note that this is not an exhaustive test of the {@link PublisherEngine} because
     * that is covered in {@link PublisherEngineTest}.
     *
     * @throws Exception
     */
    @Test
    public void testSubscribeAndPublish()
        throws Exception
    {
        // create a sample subscriber and notification
        MockSubscriber subscriber = new MockSubscriber();
        MockNotification notification = new MockNotification("subject", //$NON-NLS-1$
                                                             "body", //$NON-NLS-1$
                                                             INotification.Severity.MEDIUM,
                                                             this.toString());
        // make sure the subscriber starts in an empty state
        assertEquals(0,
                     subscriber.getPublishCount());
        assertNull(subscriber.getData());
        // subscribe our publisher to notifications
        NotificationManager.getNotificationManager().subscribe(subscriber);
        // record how long it takes for a publication to get through - we'll use this time later for the negative case
        long startingTime = System.currentTimeMillis();
        // publish our test notification
        NotificationManager.getNotificationManager().publish(notification);
        // wait until our subscriber is notified
        MarketDataFeedTestBase.waitForPublication(subscriber);
        // record how long it took the notification to go through
        long notificationTime = System.currentTimeMillis();
        // prove that the notification *did* get through
        assertEquals(notification,
                     subscriber.getData());
        // reset out subscriber for next time
        subscriber.reset();
        assertEquals(0,
                     subscriber.getPublishCount());
        assertNull(subscriber.getData());
        // unsubscribe from the notification manager - we won't receive the next publication
        NotificationManager.getNotificationManager().unsubscribe(subscriber);
        // publish again, wait a very decent interval (using the interval we captured earlier or at min 10ms) * 10 to make *really* sure
        // in reality, this shoudl amount to somewhere around 250ms
        long waitTime = Math.min((notificationTime - startingTime),
                                 10) * 10;
        // publish the notification that should not go through
        NotificationManager.getNotificationManager().publish(notification);
        // wait for the time we agreed upon above
        Thread.sleep(waitTime);
        // make sure that the publication did not come through
        assertEquals(0,
                     subscriber.getPublishCount());
        assertNull(subscriber.getData());
    }
    /**
     * Sample implementation of <code>INotification</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.8.0
     */
    public static class MockNotification
        implements INotification
    {
        private static final long serialVersionUID = 1L;
        /**
         * the subject of the notification
         */
        private final String mSubject;
        /**
         * the body of the notification
         */
        private final String mBody;
        /**
         * the severity of the notification
         */
        private final Severity mSeverity;
        /**
         * the date of the notification
         */
        private final Date mDate;
        /**
         * the originator of the notification
         */
        private final String mOriginator;
        /**
         * Create a new MockNotification instance.
         *
         * @param inSubject a <code>String</code> value
         * @param inBody a <code>String</code> value
         * @param inSeverity a <code>Severity</code> value
         * @param inOriginator a <code>String</code> value
         */
        private MockNotification(String inSubject,
                                 String inBody,
                                 Severity inSeverity,
                                 String inOriginator)
        {
            mSubject = inSubject;
            mBody = inBody;
            mSeverity = inSeverity;
            mDate = new Date();
            mOriginator = inOriginator;
        }
        @Override
        public String getBody()
        {
            return mBody;
        }
        @Override
        public Date getDate()
        {
            return mDate;
        }
        @Override
        public String getOriginator()
        {
            return mOriginator;
        }
        @Override
        public Severity getSeverity()
        {
            return mSeverity;
        }
        @Override
        public String getSubject()
        {
            return mSubject;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mBody == null) ? 0 : mBody.hashCode());
            result = prime * result + ((mDate == null) ? 0 : mDate.hashCode());
            result = prime * result + ((mOriginator == null) ? 0 : mOriginator.toString().hashCode());
            result = prime * result + ((mSeverity == null) ? 0 : mSeverity.hashCode());
            result = prime * result + ((mSubject == null) ? 0 : mSubject.hashCode());
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final MockNotification other = (MockNotification) obj;
            if (mBody == null) {
                if (other.mBody != null)
                    return false;
            } else if (!mBody.equals(other.mBody))
                return false;
            if (mDate == null) {
                if (other.mDate != null)
                    return false;
            } else if (!mDate.equals(other.mDate))
                return false;
            if (mOriginator == null) {
                if (other.mOriginator != null)
                    return false;
            } else if (!mOriginator.toString().equals(other.mOriginator.toString()))
                return false;
            if (mSeverity == null) {
                if (other.mSeverity != null)
                    return false;
            } else if (!mSeverity.equals(other.mSeverity))
                return false;
            if (mSubject == null) {
                if (other.mSubject != null)
                    return false;
            } else if (!mSubject.equals(other.mSubject))
                return false;
            return true;
        }
    }
}

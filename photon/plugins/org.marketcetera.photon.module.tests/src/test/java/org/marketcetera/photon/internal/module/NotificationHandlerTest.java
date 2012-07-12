package org.marketcetera.photon.internal.module;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.module.IDataFlowLabelProvider;
import org.marketcetera.photon.test.OSGITestUtil;

/* $License$ */

/**
 * Tests {@link NotificationHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class NotificationHandlerTest {

    @Test
    public void testReceiveData() throws Exception {
        MockNotificationManager mockManager = new MockNotificationManager();
        INotification notification = Notification.high("test", "body",
                "originator");
        new NotificationHandler(mockManager, null).receivedData(new DataFlowID(
                "1"), notification);
        assertPublished(mockManager.getPublished(), notification, "[1] test");
    }

    @Test
    public void testLabelProvider() throws Exception {
        MockNotificationManager mockManager = new MockNotificationManager();
        IDataFlowLabelProvider mockLabelProvider = mock(IDataFlowLabelProvider.class);
        DataFlowID dataFlowId1 = new DataFlowID("1");
        when(mockLabelProvider.getLabel(dataFlowId1)).thenReturn("One");
        DataFlowID dataFlowId2 = new DataFlowID("xyz");
        when(mockLabelProvider.getLabel(dataFlowId2)).thenReturn("Two");
        INotification notification = Notification.high("test", "body",
                "originator");
        NotificationHandler fixture = new NotificationHandler(mockManager,
                OSGITestUtil.provide(mockLabelProvider));
        fixture.receivedData(dataFlowId1, notification);
        assertPublished(mockManager.getPublished(), notification, "[One] test");
        fixture.receivedData(dataFlowId2, notification);
        assertPublished(mockManager.getPublished(), notification, "[Two] test");
        fixture.receivedData(new DataFlowID("3"), notification);
        assertPublished(mockManager.getPublished(), notification, "[3] test");
        fixture = new NotificationHandler(mockManager, OSGITestUtil
                .<IDataFlowLabelProvider> provide(null));
        fixture.receivedData(dataFlowId1, notification);
        assertPublished(mockManager.getPublished(), notification, "[1] test");
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("notificationManager") {
            @Override
            protected void run() throws Exception {
                new NotificationHandler(null, null);
            }
        };
    }

    private void assertPublished(INotification published,
            INotification original, String subject) {
        assertThat(published.getSubject(), is(subject));
        assertThat(published.getBody(), is(original.getBody()));
        assertThat(published.getOriginator(), is(original.getOriginator()));
        assertThat(published.getSeverity(), is(original.getSeverity()));
        assertThat(published.getDate(), is(original.getDate()));
    }

    private static class MockNotificationManager implements
            INotificationManager {

        private INotification mPublished;

        @Override
        public void publish(INotification inNotification) {
            mPublished = inNotification;
        }

        public INotification getPublished() {
            return mPublished;
        }

        @Override
        public void subscribe(ISubscriber inSubscriber) {
        }

        @Override
        public void unsubscribe(ISubscriber inSubscriber) {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.IPublisher#publish(java.lang.Object)
         */
        @Override
        public void publish(Object inData)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.IPublisher#publishAndWait(java.lang.Object)
         */
        @Override
        public void publishAndWait(Object inData)
                throws InterruptedException, ExecutionException
        {
            throw new UnsupportedOperationException();
        }
    }
}
